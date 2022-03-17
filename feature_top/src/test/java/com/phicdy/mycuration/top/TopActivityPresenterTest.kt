package com.phicdy.mycuration.top

import com.phicdy.mycuration.data.repository.ArticleRepository
import com.phicdy.mycuration.data.repository.RssRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


class TopActivityPresenterTest {

    private lateinit var mockView: TopActivityView
    private lateinit var mockArticleRepository: ArticleRepository
    private lateinit var mockRssRepository: RssRepository
    private lateinit var presenter: TopActivityPresenter

    @Before
    fun setup() {
        mockView = mock()
        mockArticleRepository = mock()
        mockRssRepository = mock()
        presenter = TopActivityPresenter(mockView, mockRssRepository, mock())
    }

    @Test
    fun `not go to artcile search result when query is null`() {
        presenter.queryTextSubmit(null)
        verify(mockView, times(0)).goToArticleSearchResult(null.toString())
    }

    @Test
    fun `go to artcile search result when query is not null`() {
        presenter.queryTextSubmit("query")
        verify(mockView, times(1)).goToArticleSearchResult("query")
    }

    @Test
    fun `when edit ok button is clicked and new title is empty then show error toast`() = runBlocking {
        presenter.onEditFeedOkButtonClicked("", 0)
        verify(mockView, times(1)).showEditFeedTitleEmptyErrorToast()
    }

    @Test
    fun `when edit ok button is clicked and new title is blank then show error toast`() = runBlocking {
        presenter.onEditFeedOkButtonClicked("   ", 0)
        verify(mockView, times(1)).showEditFeedTitleEmptyErrorToast()
    }

    @Test
    fun `when edit ok button is clicked and succeeds then show success toast`() = runBlocking {
        whenever(mockRssRepository.saveNewTitle(anyInt(), anyString())).thenReturn(1)
        presenter.onEditFeedOkButtonClicked("newTitle", 0)
        verify(mockView, times(1)).showEditFeedSuccessToast()
    }

    @Test
    fun `when edit ok button is clicked and succeeds then the title will be updated`() = runBlocking {
        whenever(mockRssRepository.saveNewTitle(anyInt(), anyString())).thenReturn(1)
        presenter.resume() // init list
        presenter.onEditFeedOkButtonClicked("newTitle", 0)
        verify(mockView, times(1)).showEditFeedSuccessToast()
        verify(mockView, times(1)).updateFeedTitle(0, "newTitle")
    }

    @Test
    fun `when edit ok button is clicked and fails then show error toast`() = runBlocking {
        whenever(mockRssRepository.saveNewTitle(anyInt(), anyString())).thenReturn(0)
        presenter.onEditFeedOkButtonClicked("newTitle", 0)
        verify(mockView, times(1)).showEditFeedFailToast()
    }

    @Test
    fun `when delete ok button is clicked and fails then show error toast`() = runBlocking {
        whenever(mockRssRepository.deleteRss(anyInt())).thenReturn(false)
        presenter.onDeleteOkButtonClicked(0)
        verify(mockView, times(1)).showDeleteFailToast()
    }

    @Test
    fun `when delete ok button is clicked and succeeds then show success toast`() = runBlocking {
        whenever(mockRssRepository.deleteRss(anyInt())).thenReturn(true)
        presenter.resume() // init list
        presenter.onDeleteOkButtonClicked(0)
        verify(mockView, times(1)).showDeleteSuccessToast()
    }

}