package com.phicdy.mycuration

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.phicdy.mycuration.advertisement.AdProvider
import com.phicdy.mycuration.data.preference.PreferenceHelper
import com.phicdy.mycuration.data.repository.RssRepository
import com.phicdy.mycuration.domain.alarm.AlarmManagerTaskManager
import com.phicdy.mycuration.rss.IconFetchWorker
import com.phicdy.mycuration.tracker.TrackerHelper
import com.phicdy.mycuration.util.FileUtil
import com.phicdy.mycuration.util.log.TimberTree
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltAndroidApp
class MyApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
    companion object {
        fun setUp(context: Context): FirebaseAnalytics {
            return FirebaseAnalytics.getInstance(context)
        }
    }

    @Inject
    lateinit var adProvider: AdProvider

    @Inject
    lateinit var rssRepository: RssRepository

    @Inject
    lateinit var timberTree: TimberTree

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(timberTree)
            if (FlipperUtils.shouldEnableFlipper(this)) {
                SoLoader.init(this, false)
                val client = AndroidFlipperClient.getInstance(this)
                client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
                client.addPlugin(DatabasesFlipperPlugin(this))
                client.addPlugin(SharedPreferencesFlipperPlugin(this, "FilterPref"))
                client.start()
            }
        }

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

        PreferenceHelper.setUp(this)

        TrackerHelper.setTracker(setUp(this))

        // For old version under 1.6.0
        FileUtil.setUpAppPath(this)
        File(FileUtil.iconSaveFolder()).let { dir ->
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles()?.forEach { icon ->
                    icon.delete()
                }
                dir.delete()
            }
        }

        AlarmManagerTaskManager(this).setFixUnreadCountAlarm()
        WorkManager.initialize(this, Configuration.Builder()
                .setWorkerFactory(DefaultWorkerFactory(rssRepository))
                .build())
        val saveRequest =
                PeriodicWorkRequestBuilder<IconFetchWorker>(1, TimeUnit.DAYS)
                        .build()
        WorkManager.getInstance(this).apply {
            cancelAllWork()
            enqueue(saveRequest)
        }

        adProvider.init(this)
    }
}
