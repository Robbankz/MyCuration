package com.phicdy.mycuration.presentation.presenter

import com.phicdy.mycuration.data.db.DatabaseAdapter
import com.phicdy.mycuration.data.filter.Filter
import com.phicdy.mycuration.presentation.view.FilterListView

class FilterListPresenter(private val view: FilterListView,
                          private val dbAdapter: DatabaseAdapter) : Presenter {

    override fun create() {}

    override fun resume() {
        view.initList(dbAdapter.allFilters)
    }

    override fun pause() {}

    fun onDeleteMenuClicked(position: Int, selectedFilter: Filter) {
        if (position < 0) return
        dbAdapter.deleteFilter(selectedFilter.id)
        view.remove(position)
        view.notifyListChanged()
    }

    fun onEditMenuClicked(selectedFilter: Filter) {
        val id = selectedFilter.id
        // Database table ID starts with 1, ID under 1 means invalid
        if (id <= 0) return
        view.startEditActivity(id)
    }

    fun onFilterCheckClicked(clickedFilter: Filter, isChecked: Boolean) {
        clickedFilter.isEnabled = isChecked
        dbAdapter.updateFilterEnabled(clickedFilter.id, isChecked)
    }
}
