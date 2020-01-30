package com.example.weatherapp.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.api.model.JsonFile
import com.example.weatherapp.database.entity.CurrentEntity
import com.example.weatherapp.database.entity.CurrentSummaryEntity
import com.example.weatherapp.database.entity.OWTempAirEntity
import com.example.weatherapp.database.entity.TempAirHistoryEntity
import com.example.weatherapp.manager.CurrentManager
import com.example.weatherapp.manager.OWTempAirManager
import com.example.weatherapp.manager.TempAirManager
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.day_summary.*
import kotlinx.android.synthetic.main.details.*
import kotlinx.android.synthetic.main.fragment_fragment_one.*
import kotlinx.android.synthetic.main.settingpopup.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FragmentOne : Fragment() {
//    region Change City Values
    private lateinit var settingDialog: AlertDialog
    private var cityName = arrayListOf<String>()
    private var cityId = arrayListOf<Int>()
//endregion

//    region Swipe

    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable

    private fun showProgress() {
        swipeActual.isRefreshing = true
    }

    private fun hideProgress() {
        swipeActual.isRefreshing = false
    }

    //endregion

//    region Tag

    private val TAG = FragmentOne::class.java.simpleName

//    endregion

//    region API

    private val currentMenager by lazy {
        CurrentManager()
    }

    private val owTempAirManager by lazy {
        OWTempAirManager()
    }

    private val tempAirManager by lazy {
        TempAirManager()
    }

    private val disposables: CompositeDisposable = CompositeDisposable()

    //endregion

//    region Fetch Data from Api

    @SuppressLint("SetTextI18n")
    private fun handleGetCurrentSuccess(current: CurrentEntity) {

        // Log the fact.
        Log.i(TAG, "Successfully loaded current.")

        //textView2.visibility = View.VISIBLE
        textView16.text = current.time
        tempAir.text = "${current.tempair}°C"
        tempinside.text = "${current.tempinside}°C"
        pressure.text = "${current.pressure} hPa"
        humidity.text = "${current.humidity} %"
        if (current.rain.toString() == "false") {
            rain.text = "Brak"
        } else {
            rain.text = "Występują\n%.2f mm".format(current.raingauge)
        }
        wind.text = "%.2f km/h".format(current.wind)
        dewpoint.text = "%.2f °C".format(current.dewpoint)

    }

    private fun handleGetCurrentErrorApi(throwable: Throwable) {

        //Log an error
        Log.e(TAG, "An error occured while fetching current from api.")
        //Log.e(TAG, throwable.localizedMessage)
    }

    private fun handleGetCurrentErrorCache(throwable: Throwable) {

        //Log an error
        Log.e(TAG, "An error occured while fetching current from cache.")
        //Log.e(TAG, throwable.localizedMessage)
    }

    private fun handleGetSummaryErrorApi(throwable: Throwable) {

        //Log an error
        Log.e(TAG, "An error occured while fetching current from api.")
        //Log.e(TAG, throwable.localizedMessage)
    }

    private fun handleGetSummaryErrorCache(throwable: Throwable) {

        //Log an error
        Log.e(TAG, "An error occured while fetching current z cache.")
        //Log.e(TAG, throwable.localizedMessage)
    }

    @SuppressLint("SetTextI18n")
    private fun handleGetSummarySuccess(summary: CurrentSummaryEntity) {
        // Log the fact.
        Log.i(TAG, "Successfully loaded summary.")

        Log.e(TAG, summary.toString())
        summarytempinside.text = "${summary.tempinside}°C"
        summarytempair.text = "${summary.tempair}°C"
        summarymintemp.text = "${summary.min}°C"
        summarymaxtemp.text = "${summary.max}°C"
        summarypressure.text = "${summary.pressure} hPa"
        summaryhumidity.text = "${summary.humidity} %"
        summaryraingauge.text = "${summary.raingauge} mm"
        summarysum.text = "%.2f mm".format(summary.sum)
        summarywind.text = "${summary.wind} km/h"
    }

//    endregion

//    region Chart

    private val entries = ArrayList<Entry>()
    private val totalCount = 23
    private var owaCount = 0
    private var label = 0

    //check internet status prevent double data in chart
    private fun hasNetwork(): Boolean {
        val isConnected: Boolean? // Initial Value
        val connMgr =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo

        isConnected = networkInfo != null && networkInfo.isConnected
        return isConnected
    }

    private fun fetchDataAndDrawChart(url: String) {

        if (this.hasNetwork()) {
            tempAirManager
                .dowloadTempAirHistory(url)
                .andThen(tempAirManager.getTempAirHistory())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::handleGetTempAirHistory,
                    this::handleGetCurrentErrorApi
                )
                .addTo(disposables)
        } else {
            tempAirManager
                .getTempAirHistory()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::handleGetTempAirHistory,
                    this::handleGetCurrentErrorApi
                )
                .addTo(disposables)
        }
    }


    private fun handleGetTempAirHistory(tempAirHistory: List<TempAirHistoryEntity>) {
        // Log the fact.
        Log.i(TAG, "Successfully loaded history.")


        owaCount = (totalCount - tempAirHistory.lastIndex) / 3
        label = 0

        Log.e(TAG, "Last index in history: ${tempAirHistory.lastIndex}")
        Log.e(TAG, "From owa need up $owaCount index")

        for (i in 0..tempAirHistory.lastIndex) {
            if (i % 3 == 0) {
                entries.add(Entry(label.toFloat(), tempAirHistory[i].tempair.toFloat()))
                label += 1
            }
        }

        val sharedPreferences =
            this.activity!!.getSharedPreferences("SP_INFO", Context.MODE_PRIVATE)
        val id = sharedPreferences.getInt("CityID", 3102447)
        val url = "forecast?id=${id}&appid=2d969eac5ee52e23ca8d4265cde83fb5"
        //Load data form OpenWeather Api
        if (hasNetwork()) {
            //Load from api
            owTempAirManager
                .downloadOWTempAir(url)
                .andThen(owTempAirManager.getOWTempAir())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::handleGetOWASuccess,
                    this::handleGetCurrentErrorApi
                )
                .addTo(disposables)
        } else {
            //Load from cache
            owTempAirManager
                .getOWTempAir()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::handleGetOWASuccess,
                    this::handleGetCurrentErrorApi
                )
                .addTo(disposables)
        }
    }

    private fun handleGetOWASuccess(owTempAir: List<OWTempAirEntity>) {
        val unixTime = (System.currentTimeMillis() / 1000L) + 3600
        Log.e(TAG, unixTime.toString())
        if (unixTime >= owTempAir[0].dt) {
            for (i in 0..owaCount) {
                Log.e(TAG, "Godzina ${owTempAir[i + 1].dt} temperatura ${owTempAir[i + 1].tempair}")
                entries.add(Entry(label.toFloat(), owTempAir[i + 1].tempair.toFloat() - 273.15F))
                label += 1
            }
        } else {
            for (i in 0..owaCount) {
                Log.e(TAG, "Godzina ${owTempAir[i].dt} temperatura ${owTempAir[i].tempair}")
                entries.add(Entry(label.toFloat(), owTempAir[i].tempair.toFloat() - 273.15F))
                label += 1
            }
        }

        //Draw chart
        drawChart(entries)
    }

    private fun drawChart(entries: ArrayList<Entry>) {

        val vl = LineDataSet(entries, "Temp")
        vl.setDrawValues(true)
        vl.setDrawFilled(false)
        vl.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        vl.circleRadius = 2f
        vl.circleHoleColor = Color.TRANSPARENT
        vl.setCircleColor(Color.WHITE)
        vl.cubicIntensity = 0.5f
        vl.lineWidth = 2f
        vl.color = Color.parseColor("#FFFFFF")
        vl.enableDashedLine(50f, 25f, 0f)
        vl.valueTextSize = 10f
        vl.valueTextColor = Color.WHITE
        linechart.data = LineData(vl)
        linechart.legend.isEnabled = false
        linechart.setTouchEnabled(false)
        linechart.setPinchZoom(false)
        val xAxis = linechart.xAxis
        xAxis.setDrawLabels(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.labelRotationAngle = 0f
        class MyXAxisFormatter : ValueFormatter() {
            private val hours = arrayOf(
                "00:00",
                "03:00",
                "06:00",
                "09:00",
                "12:00",
                "15:00",
                "18:00",
                "21:00",
                "00:00"
            )
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return hours.getOrNull(value.toInt()) ?: value.toString()
            }
        }
        xAxis.valueFormatter = MyXAxisFormatter()
        xAxis.textSize = 10f
        xAxis.textColor = Color.WHITE
        val yAxisLeft = linechart.axisLeft
        val yAxisRight = linechart.axisRight
        yAxisLeft.setDrawLabels(false)
        yAxisRight.setDrawLabels(false)
        linechart.description.isEnabled = false
        linechart.setNoDataTextColor(Color.WHITE)
        linechart.setNoDataText("Brak danych")
        linechart.invalidate()
    }

    //endregion

//    region Lifecycle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_one, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        readJson()

        val sharedPreferences =
            this.activity!!.getSharedPreferences("SP_INFO", Context.MODE_PRIVATE)

        //region Time

        var currentTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH")
        val formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        var formatted = currentTime.format(formatter)
        var chartdata = currentTime.format(formatter2)


        linechart.setNoDataText("Brak danych")
        linechart.invalidate()


        //endregion

        //region draw chart
        fetchDataAndDrawChart("chart/$chartdata")
        //endregion

        //region fetch current data
        //Load current from cache.
        currentMenager
            .getCurrent()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleGetCurrentSuccess,
                this::handleGetCurrentErrorCache
            ).addTo(disposables)

        //Load current from api
        currentMenager
            .downloadCurrent("day/$formatted:00")
            .andThen(currentMenager.getCurrent())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgress() }
            .doFinally { hideProgress() }
            .subscribe(
                this::handleGetCurrentSuccess,
                this::handleGetCurrentErrorApi
            )
            .addTo(disposables)

        //endregion

        //region fetch summary data
        currentMenager
            .getCurrentSummary()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleGetSummarySuccess,
                this::handleGetSummaryErrorCache
            ).addTo(disposables)

        currentMenager
            .downloadCurrentSummary("detail/$chartdata")
            .andThen(currentMenager.getCurrentSummary())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleGetSummarySuccess,
                this::handleGetSummaryErrorApi
            )
            .addTo(disposables)
        //endregion

        settings.setOnClickListener {
            settingPopup(sharedPreferences, chartdata)
        }

        val id = sharedPreferences.getInt("CityID", 3102447)
        for (i in 0 until cityName.size) {
            if (cityId[i] == id) {
                textView15.text = cityName[i]
            }
        }


        //region swipe fragment

        mHandler = Handler()
        swipeActual.setOnRefreshListener {

            currentTime = LocalDateTime.now()
            formatted = currentTime.format(formatter)
            chartdata = currentTime.format(formatter2)

            mRunnable = Runnable {

                currentMenager
                    .getCurrent()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        this::handleGetCurrentSuccess,
                        this::handleGetCurrentErrorCache
                    ).addTo(disposables)

                currentMenager
                    .downloadCurrent("day/$formatted:00")
                    .andThen(currentMenager.getCurrent())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgress() }
                    .doFinally { hideProgress() }
                    .subscribe(
                        this::handleGetCurrentSuccess,
                        this::handleGetCurrentErrorApi
                    )
                    .addTo(disposables)

                entries.clear()
                fetchDataAndDrawChart("chart/$chartdata")

                currentMenager
                    .getCurrentSummary()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        this::handleGetSummarySuccess,
                        this::handleGetSummaryErrorCache
                    ).addTo(disposables)

                currentMenager
                    .downloadCurrentSummary("detail/$chartdata")
                    .andThen(currentMenager.getCurrentSummary())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        this::handleGetSummarySuccess,
                        this::handleGetSummaryErrorApi
                    )
                    .addTo(disposables)
            }
            mHandler.post(mRunnable)
        }
        //endregion
    }
    //endregion

//    region Change City
    private fun readJson() {
        val gson = Gson()
        val text = resources.openRawResource(R.raw.data)
            .bufferedReader().use { it.readText() }
        val data = gson.fromJson(text, Array<JsonFile>::class.java)
        for (i in data.indices) {
            cityName.add(data[i].name)
            cityId.add(data[i].id)
        }
    }


    @SuppressLint("InflateParams")
    private fun settingPopup(sharedPreferences: SharedPreferences, chartdata: String) {
        val builder = AlertDialog.Builder(this.activity)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.settingpopup, null)
        val buttonYes = dialogLayout.findViewById<Button>(R.id.cancelPopupYes)
        val buttonNo = dialogLayout.findViewById<Button>(R.id.cancelPopupNo)
        val text = dialogLayout.findViewById<AutoCompleteTextView>(R.id.ACTVV)

        val adapter = ArrayAdapter<String>(
            context!!,
            R.layout.change_list_item,
            cityName
        )
        text.setAdapter(adapter)
        text.threshold = 1

        buttonNo.setOnClickListener {
            settingDialog.cancel()
        }

        buttonYes.setOnClickListener {
            save(sharedPreferences, text.text.toString(), chartdata)
        }

        builder.setView(dialogLayout)
        settingDialog = builder.create()
        settingDialog.show()
    }

    private fun save(sharedPreferences: SharedPreferences, text: String, chartdata: String) {
        var idNumber = 0
        var flag = false
        for (i in 0 until cityName.size) {
            if (cityName[i] == text) {
                idNumber = cityId[i]
                flag = true
            }
        }

        if (flag) {
            val editor = sharedPreferences.edit()
            editor.putInt("CityID", idNumber)
            editor.apply()
            Toast.makeText(context, "Pomyślnie zmieniono miasto", Toast.LENGTH_LONG).show()
            entries.clear()
            fetchDataAndDrawChart("chart/$chartdata")
            for (i in 0 until cityName.size) {
                if (cityId[i] == idNumber) {
                    textView15.text = cityName[i]
                }
            }
            settingDialog.cancel()
        } else Toast.makeText(context, "Wybierz inne miasto", Toast.LENGTH_LONG).show()
    }
    //endregion
}
