package com.example.weatherapp.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.get
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.api.ApiClient
import com.example.weatherapp.api.model.HourHistoryDto
import com.example.weatherapp.view.list.HourListItem
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.dsl.modelAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_hour_history.*
import kotlinx.android.synthetic.main.fragment_fragment_two.*
import kotlinx.android.synthetic.main.item_hour_history.*
import kotlinx.android.synthetic.main.item_hour_history.view.*

class HourHistory : AppCompatActivity() {

    //region API

    private val apiService by lazy {
        ApiClient.create()
    }

    //endregion

    //region Tag

    private val TAG = HourHistory::class.java.simpleName

    //endregion

    //region Recyclerview

    private val itemAdapter = ItemAdapter<HourListItem>()

    private val fastAdapter = FastAdapter.with(itemAdapter)

    private fun initializeRecyclerView() {
        recycler_view_hour.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
            adapter = fastAdapter
        }

        fastAdapter.addEventHook(object : ClickEventHook<HourListItem>() {

            override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                return viewHolder.itemView.run { listOf(item_hour_history_cardView) }
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<HourListItem>,
                item: HourListItem
            ) {
                if (v.expandableHourView.visibility == View.GONE){
                    v.expandableHourView.visibility = View.VISIBLE
//                    TransitionManager.beginDelayedTransition(v.item_7days_cardView, AutoTransition())
                } else {
                    v.expandableHourView.visibility = View.GONE
//                    TransitionManager.beginDelayedTransition(v.item_7days_cardView, AutoTransition())
                }
                v.refreshDrawableState()
            }

        })
    }

    //endregion

    var date = "day/"

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hour_history)

        var title = ""
        for (i in 0..9) {
            date += intent.getStringExtra("date")[i]
            title += intent.getStringExtra("date")[i]
        }
        apiService
            .fetchHourHistory(date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.list }
            .subscribe(
                { handleFetchHistorySuccess(it)},
                { handleFetchHistoryError(it)}
            )

        initializeRecyclerView()

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = "Pogoda w dniu $title"
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun handleFetchHistorySuccess(model: List<HourHistoryDto>){

        val items = model.map {
            HourListItem(it)
        }

        Log.e(TAG, items.size.toString())

        itemAdapter.setNewList(items)
    }

    private fun handleFetchHistoryError(throwable: Throwable) {
        // Log an error.
        Log.e(TAG, "An error occurred while fetching films.")
        Log.e(TAG, throwable.localizedMessage)
    }
}
