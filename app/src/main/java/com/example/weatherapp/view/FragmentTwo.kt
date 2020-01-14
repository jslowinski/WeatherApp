package com.example.weatherapp.view


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.weatherapp.R
import com.example.weatherapp.database.entity.CurrentEntity
import com.example.weatherapp.database.entity.Last7DaysEntity
import com.example.weatherapp.manager.CurrentManager
import com.example.weatherapp.manager.Last7DaysManager
import com.example.weatherapp.view.list.Last7DaysListItem
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_fragment_two.*
import kotlinx.android.synthetic.main.item_7days.*
import kotlinx.android.synthetic.main.item_7days.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 *
 */
class FragmentTwo : Fragment() {

    //region Swipe

    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable

    private fun showProgress(){
        swipeHistory.isRefreshing = true
    }

    private fun hideProgress(){
        swipeHistory.isRefreshing = false
    }

    //endregion

    //region Tag

    private val TAG = FragmentTwo::class.java.simpleName

    //endregion

    //region API

    private val last7DaysManager by lazy {
        Last7DaysManager()
    }

    private val currentMenager by lazy {
        CurrentManager()
    }

    private val disposables: CompositeDisposable = CompositeDisposable()

    //endregion

    private fun handleGetSuccess(last7days: List<Last7DaysEntity>) {
        Log.e(TAG, "Successfuly loaded")

        val items = last7days.map {
            Last7DaysListItem(it)
        }

        itemAdapter.setNewList(items)
    }


    private fun handleGetError(throwable: Throwable) {

        // Log an error.
        Log.e(TAG, "An error occurred while fetching films.")

    }

    private val itemAdapter = ItemAdapter<Last7DaysListItem>()

    private val fastadapter = FastAdapter.with(itemAdapter)


    private fun initializeRecyclerView() {
        recycler_view_7days.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
            adapter = fastadapter
        }


        fastadapter.addEventHook(object : ClickEventHook<Last7DaysListItem>() {

            override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                return viewHolder.itemView.run { listOf(item_7days_cardView) }
            }


            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<Last7DaysListItem>,
                item: Last7DaysListItem
            ) {
                if (v.expandableView.visibility == View.GONE){
                    v.expandableView.visibility = View.VISIBLE
//                    TransitionManager.beginDelayedTransition(v.item_7days_cardView, AutoTransition())
                } else {
                    v.expandableView.visibility = View.GONE
//                    TransitionManager.beginDelayedTransition(v.item_7days_cardView, AutoTransition())
                }
                recycler_view_7days.refreshDrawableState()
            }

        })

        fastadapter.addEventHook(object : ClickEventHook<Last7DaysListItem>() {

            override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                return viewHolder.itemView.run { listOf(daysdetail) }
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<Last7DaysListItem>,
                item: Last7DaysListItem
            ) {
                val itemHistory = item.model

                val hourDetail = Intent(context, HourHistory::class.java)

                hourDetail.putExtra("date",itemHistory.date)
                hourDetail.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(hourDetail)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun handleGetCurrentSuccess(current: CurrentEntity){

        // Log the fact.
        Log.i(TAG, "Successfully loaded current.")

        //textView2.visibility = View.VISIBLE
        actualtempair.text = "${current.tempair}°C"
        actualtempinside.text =  "Temperatura wewnątrz ${current.tempinside}°C"
        actualpressure.text = (Html.fromHtml("<b>${current.pressure} hPa</b><br /> Ciśnienie"))
        actualhumidity.text =  (Html.fromHtml("<b>${current.humidity} %</b><br /> Wilgotność"))
        if (current.rain.toString() == "false") {
            actualrain.text = (Html.fromHtml("<b>Brak</b>"))
        } else {
            actualrain.text = (Html.fromHtml("<b>Występują</b><br />%.2f mm".format(current.raingauge)))
        }
        actualdewpoint.text = (Html.fromHtml("<b>%.2f °C</b><br /> Punkt Rosy".format(current.dewpoint)))
        actualwind.text = (Html.fromHtml("<b>%.2f km/h</b><br /> Wiatr".format(current.wind)))

    }

    private fun handleGetCurrentError(throwable: Throwable) {

        //Log an error
        Log.e(TAG, "An error occured while fetching current from api.")
        //Log.e(TAG, throwable.localizedMessage)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_fragment_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var currentTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH")
        val formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        var formatted = currentTime.format(formatter)

        actualhistory.setOnClickListener {
            val hourDetail = Intent(context, HourHistory::class.java)

            var formatted2 = currentTime.format(formatter2)
            hourDetail.putExtra("date",formatted2)
            hourDetail.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(hourDetail)

        }

        //Load current from cache.
        currentMenager
            .getCurrent()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleGetCurrentSuccess,
                this::handleGetCurrentError
            ).addTo(disposables)

        //Load current from api
        currentMenager
            .downloadCurrent("day/$formatted:00")
            .andThen(currentMenager.getCurrent())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{ showProgress() }
            .doFinally{ hideProgress() }
            .subscribe(
                this::handleGetCurrentSuccess,
                this::handleGetCurrentError
            )
            .addTo(disposables)

        //Load from cache
        last7DaysManager
            .getLast7Days()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleGetSuccess,
                this::handleGetError
            ).addTo(disposables)

        //Load from api
        last7DaysManager
            .downloadLast7Days("last7days")
            .andThen(last7DaysManager.getLast7Days())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{ showProgress() }
            .doFinally{ hideProgress() }
            .subscribe(
                this::handleGetSuccess,
                this::handleGetError
            ).addTo(disposables)

        initializeRecyclerView()

        mHandler = Handler()

        swipeHistory.setOnRefreshListener {

            currentTime = LocalDateTime.now()
            formatted = currentTime.format(formatter)

            mRunnable = Runnable {
                //Load current from cache.
                currentMenager
                    .getCurrent()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        this::handleGetCurrentSuccess,
                        this::handleGetCurrentError
                    ).addTo(disposables)

                //Load current from api
                currentMenager
                    .downloadCurrent("day/$formatted:00")
                    .andThen(currentMenager.getCurrent())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe{ showProgress() }
                    .doFinally{ hideProgress() }
                    .subscribe(
                        this::handleGetCurrentSuccess,
                        this::handleGetCurrentError
                    )
                    .addTo(disposables)

                //Load from cache
                last7DaysManager
                    .getLast7Days()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        this::handleGetSuccess,
                        this::handleGetError
                    ).addTo(disposables)

                //Load from api
                last7DaysManager
                    .downloadLast7Days("last7days")
                    .andThen(last7DaysManager.getLast7Days())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe{ showProgress() }
                    .doFinally{ hideProgress() }
                    .subscribe(
                        this::handleGetSuccess,
                        this::handleGetError
                    ).addTo(disposables)

                initializeRecyclerView()
            }
            mHandler.post(mRunnable)
        }
    }

}
