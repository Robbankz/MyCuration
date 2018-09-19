package com.phicdy.mycuration.util.test

import com.phicdy.mycuration.util.UrlUtil
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UrlUtilTest {

    @Test
    fun testRemoveUrlParameter() {
        val removedUrl = UrlUtil.removeUrlParameter("http://harofree.blog.fc2.com/?ps")
        assertEquals("http://harofree.blog.fc2.com/", removedUrl)
    }

    @Test
    fun testHasParameterUrl() {
        assertEquals(true, UrlUtil.hasParameterUrl("http://www.xxx.com/?aaa"))
        assertEquals(true, UrlUtil.hasParameterUrl("https://www.xxx.com/?aaa"))
        assertEquals(true, UrlUtil.hasParameterUrl("http://www.xxx.com/aaa/?bbb"))
        assertEquals(false, UrlUtil.hasParameterUrl("http://www.xxx.com/aaa"))
        assertEquals(false, UrlUtil.hasParameterUrl("http://www.xxx.com?/aaa"))
    }
}
