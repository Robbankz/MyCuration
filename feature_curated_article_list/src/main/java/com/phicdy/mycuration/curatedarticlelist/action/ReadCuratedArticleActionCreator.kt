package com.phicdy.mycuration.curatedarticlelist.action

import com.phicdy.mycuration.core.ActionCreator2
import com.phicdy.mycuration.core.Dispatcher
import com.phicdy.mycuration.curatedarticlelist.CuratedArticleItem
import com.phicdy.mycuration.data.repository.ArticleRepository
import com.phicdy.mycuration.data.repository.RssRepository
import com.phicdy.mycuration.entity.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReadCuratedArticleActionCreator @Inject constructor(
        private val dispatcher: Dispatcher,
        private val articleRepository: ArticleRepository,
        private val rssRepository: RssRepository
) : ActionCreator2<Int, List<CuratedArticleItem>> {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun run(position: Int, items: List<CuratedArticleItem>) {
        withContext(Dispatchers.IO) {
            when (val item = items[position]) {
                is CuratedArticleItem.Advertisement -> return@withContext
                is CuratedArticleItem.Content -> {
                    val oldStatus = item.value.status
                    if (oldStatus == Article.READ) {
                        return@withContext
                    }
                    val article = item.value
                    articleRepository.saveStatus(article.id, Article.READ)
                    val rss = rssRepository.getFeedById(article.feedId)
                    rss?.let {
                        rssRepository.updateUnreadArticleCount(article.feedId, rss.unreadAriticlesCount - 1)
                    }
                    article.status = Article.READ
                    dispatcher.dispatch(ReadArticleAction(position))
                }
            }
        }
    }
}