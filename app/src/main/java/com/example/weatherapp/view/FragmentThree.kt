package com.example.weatherapp.view


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.api.ApiClient
import com.example.weatherapp.api.model.CurrentDto
import com.example.weatherapp.api.model.CurrentSummaryDto
import com.example.weatherapp.api.model.FirstDayDto
import com.example.weatherapp.api.model.JsonFile
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_fragment_three.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.ln


@Suppress("DEPRECATION")
class FragmentThree : Fragment() {

    private lateinit var detailDialog: AlertDialog

    private lateinit var minDate: Date

    private var dayS: String = ""
    private var monthS: String = ""
    private var yearS: String = ""
    private var hourS: String = ""
    private var stringYear: String = ""
    private var stringHour: String = ""
    private var stringDate: String = ""

    private var dayS2: String = ""
    private var monthS2: String = ""
    private var yearS2: String = ""
    private var hourS2: String = ""
    private var stringYear2: String = ""
    private var stringHour2: String = ""
    private var stringDate2: String = ""

    private val c = Calendar.getInstance()

    private var year = c.get(Calendar.YEAR)
    private var month = c.get(Calendar.MONTH)
    private var day = c.get(Calendar.DAY_OF_MONTH)
    private var hour = c.get(Calendar.HOUR_OF_DAY)

    private var year2 = c.get(Calendar.YEAR)
    private var month2 = c.get(Calendar.MONTH)
    private var day2 = c.get(Calendar.DAY_OF_MONTH)
    private var hour2 = c.get(Calendar.HOUR_OF_DAY)

    //region API

    private val apiService by lazy {
        ApiClient.create()
    }

    //endregion

    //region Tag

    private val TAG = FragmentThree::class.java.simpleName

    //endregion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_three, container, false)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        apiService
            .fetchFirstDate()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { handleFirstDateSuccess(it)},
                { handleFetchHistoryError(it)}
            )

//        region first day
        datepicker_button.setOnClickListener {
            datepicker_button.isEnabled = false
            val dpd = DatePickerDialog(
                context!!,
                DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
                    day = mDay
                    month = mMonth
                    year = mYear
                    if (mDay < 10) {
                        dayS = "0$mDay"

                        monthS = if (mMonth < 10) {
                            "0${mMonth + 1}"
                        } else {
                            "${mMonth + 1}"
                        }
                    } else {
                        dayS = "$mDay"
                        monthS = if (mMonth < 10) {
                            "0${mMonth + 1}"
                        } else {
                            "${mMonth + 1}"
                        }
                    }
                    yearS = "$mYear"

                    stringYear = "$yearS-$monthS-$dayS"
                    hourpicker_button.isEnabled = true
                    datepicker_button.isEnabled = true
                },
                year,
                month,
                day
            )


            dpd.setOnCancelListener {
                datepicker_button.isEnabled = true
            }

            dpd.datePicker.maxDate = c.timeInMillis
            dpd.datePicker.minDate = minDate.time

            dpd.show()
        }

        hourpicker_button.setOnClickListener {
            hourpicker_button.isEnabled = false

            val tpd = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener{ _, mHour, mMin ->
                    hour = mHour
                    hourS = mHour.toString()
                    stringHour = hourS
                    stringDate = "$stringYear $stringHour:00"
                    hourpicker_button.isEnabled = true
                    apiService
                        .fetchPickedInfo("day/$stringYear/$stringHour:00")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { handleFetchHistorySuccess(it,stringDate)},
                            { handleFetchHistoryError(it)}
                        )
                },hour, 0, true)


            tpd.setOnCancelListener {
                hourpicker_button.isEnabled = true
            }

            tpd.show()
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(c.timeInMillis)
        val splitDate = currentDate.split("-")
        dayS = splitDate[2]
        monthS = splitDate[1]
        yearS = splitDate[0]

        val currentTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH")
        var formatted = currentTime.format(formatter)

        stringYear = currentDate
        stringDate= "$currentDate $formatted:00"

        apiService
            .fetchPickedInfo("day/$currentDate/$formatted:00")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { handleFetchHistorySuccess(it, stringDate)},
                { handleFetchHistoryError(it)}
            )

        firstday.setOnClickListener {
            apiService
                .fetchCurrentSummary("detail/$stringYear")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { handleFetchSummarySuccess(it)},
                    { handleFetchHistoryError(it)}
                )
        }

        pickhouricon.setOnClickListener{
            val hourDetail = Intent(context, HourHistory::class.java)

            hourDetail.putExtra("date",stringYear)
            hourDetail.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(hourDetail)
        }

//        endregion

//        region second day


        showanother.setOnClickListener {
            if (showanother.text.toString().equals("Pokaż drugi dzień"))
            {
                datepicker_button2.visibility = View.VISIBLE
                hourpicker_button2.visibility = View.VISIBLE
                showanother.text = "Ukryj drugi dzień"
            } else {
                showanother.text = "Pokaż drugi dzień"
                datepicker_button2.visibility = View.GONE
                hourpicker_button2.visibility = View.GONE
                hourpicker_button2.isEnabled = false
                anotherday.visibility = View.GONE
            }

        }

        datepicker_button2.setOnClickListener {
            datepicker_button2.isEnabled = false
            val dpd = DatePickerDialog(
                context!!,
                DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
                    day2 = mDay
                    month2 = mMonth
                    year2 = mYear
                    if (mDay < 10) {
                        dayS2 = "0$mDay"

                        monthS2 = if (mMonth < 10) {
                            "0${mMonth + 1}"
                        } else {
                            "${mMonth + 1}"
                        }
                    } else {
                        dayS2 = "$mDay"
                        monthS2 = if (mMonth < 10) {
                            "0${mMonth + 1}"
                        } else {
                            "${mMonth + 1}"
                        }
                    }
                    yearS2 = "$mYear"

                    stringYear2 = "$yearS2-$monthS2-$dayS2"
                    hourpicker_button2.isEnabled = true
                    datepicker_button2.isEnabled = true
                },
                year2,
                month2,
                day2
            )


            dpd.setOnCancelListener {
                datepicker_button2.isEnabled = true
            }

            dpd.datePicker.maxDate = c.timeInMillis
            dpd.datePicker.minDate = minDate.time

            dpd.show()
        }

        hourpicker_button2.setOnClickListener {
            hourpicker_button2.isEnabled = false

            val tpd = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener{ _, mHour, mMin ->
                    Log.e(TAG, hour2.toString())
                    hour2 = mHour
                    Log.e(TAG, mHour.toString())
                    hourS2 = mHour.toString()
                    stringHour2 = hourS2
                    stringDate2 = "$stringYear2 $stringHour2:00"
                    Log.e(TAG, stringDate2)
                    hourpicker_button2.isEnabled = true
                    apiService
                        .fetchPickedInfo("day/$stringYear2/$stringHour2:00")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { handleFetchHistory2Success(it,stringDate2)},
                            { handleFetchHistory2Error(it)}
                        )
                },hour2, 0, true)


            tpd.setOnCancelListener {
                hourpicker_button2.isEnabled = true
            }


            tpd.show()

        }

        anotherday.setOnClickListener {
            apiService
                .fetchCurrentSummary("detail/$stringYear2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { handleFetchSummarySuccess(it)},
                    { handleFetchHistoryError(it)}
                )
        }

        pickhouricon2.setOnClickListener{
            val hourDetail = Intent(context, HourHistory::class.java)

            hourDetail.putExtra("date",stringYear2)
            hourDetail.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(hourDetail)
        }

        // endregion

    }

    private fun calculateDewPoint(tempair: Double, humidity: Double): Double {
        val variable = (ln(humidity/100) + ((17.27 * tempair) / (237.3 + tempair))) / 17.27
        return (237.3 * variable) / (1 - variable)
    }

    @SuppressLint("SetTextI18n")
    private fun handleFetchHistorySuccess(model: CurrentDto, date : String){
        pickname.text = date
        picktempair.text = "${model.tempair}°C"
        picktempinside.text = "Temperatura wewnątrz ${model.tempinside}°C"
        pickhumidity.text = (Html.fromHtml("<b>${model.humidity} %</b><br /> Wilgotność"))
        pickpressure.text = (Html.fromHtml("<b>${model.pressure} hPa</b><br /> Ciśnienie"))
        pickwind.text = (Html.fromHtml("<b>%.2f km/h</b><br /> Wiatr".format(model.wind)))
        if (model.rain.toString() == "false") {
            pickrain.text = (Html.fromHtml("<b>Brak</b>"))
        } else {
            pickrain.text = (Html.fromHtml("<b>Występują</b><br />%.2f mm".format(model.raingauge)))
        }
        pickdewpoint.text = (Html.fromHtml("<b>%.2f °C</b><br /> Punkt Rosy".format( calculateDewPoint(model.tempair, model.humidity))))
    }

    @SuppressLint("SetTextI18n")
    private fun handleFetchHistory2Success(model: CurrentDto, date : String){
        anotherday.visibility = View.VISIBLE
        pickname2.text = date
        picktempair2.text = "${model.tempair}°C"
        picktempinside2.text ="Temperatura wewnątrz ${model.tempinside}°C"
        pickhumidity2.text = (Html.fromHtml("<b>${model.humidity} %</b><br /> Wilgotność"))
        pickpressure2.text = (Html.fromHtml("<b>${model.pressure} hPa</b><br /> Ciśnienie"))
        pickwind2.text = (Html.fromHtml("<b>%.2f km/h</b><br /> Wiatr".format(model.wind)))
        if (model.rain.toString() == "false") {
            pickrain2.text = (Html.fromHtml("<b>Brak</b>"))
        } else {
            pickrain2.text = (Html.fromHtml("<b>Występują</b><br />%.2f mm".format(model.raingauge)))
        }
        pickdewpoint2.text = (Html.fromHtml("<b>%.2f °C</b><br /> Punkt Rosy".format( calculateDewPoint(model.tempair, model.humidity))))


    }

    private fun handleFetchHistoryError(throwable: Throwable) {
        // Log an error.
        Log.e(TAG, "An error occurred while fetching data.")
        Toast.makeText(context, "Brak danych na wybraną datę", Toast.LENGTH_LONG).show()
        hour = c.get(Calendar.HOUR_OF_DAY)
        Log.e(TAG, throwable.localizedMessage)
    }

    private fun handleFetchHistory2Error(throwable: Throwable) {
        // Log an error.
        Log.e(TAG, "An error occurred while fetching data.")
        Toast.makeText(context, "Brak danych na wybraną datę", Toast.LENGTH_LONG).show()
        hour2 = c.get(Calendar.HOUR_OF_DAY)
        Log.e(TAG, throwable.localizedMessage)
    }

    private fun handleFetchSummarySuccess(model: CurrentSummaryDto){
        detailPopup(model)
    }


    @SuppressLint("InflateParams")
    fun detailPopup(model: CurrentSummaryDto) {
        val builder = AlertDialog.Builder( this.activity)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.detailpopup, null)
        val popupdateText = dialogLayout.findViewById<TextView>(R.id.popupdate)
        val buttonClose = dialogLayout.findViewById<Button>(R.id.OkButton)
        val popuptempinsideText = dialogLayout.findViewById<TextView>(R.id.popuptempinside)
        val popuptempair = dialogLayout.findViewById<TextView>(R.id.popuptempair)
        val popupmintemp = dialogLayout.findViewById<TextView>(R.id.popupmintemp)
        val popupmaxtemp = dialogLayout.findViewById<TextView>(R.id.popupmaxtemp)
        val popuphumidity = dialogLayout.findViewById<TextView>(R.id.popuphumidity)
        val popuppressure = dialogLayout.findViewById<TextView>(R.id.popuppressure)
        val popupraingauge = dialogLayout.findViewById<TextView>(R.id.popupraingauge)
        val popupsum = dialogLayout.findViewById<TextView>(R.id.popupsum)
        val popupwind = dialogLayout.findViewById<TextView>(R.id.popupwind)

        buttonClose.setOnClickListener{
            detailDialog.cancel()

        }
        popupdateText.text = "Podsumowanie całego dnia"
        popuptempinsideText.text = model.tempinside
        popuptempair.text = model.tempair
        popupmintemp.text = model.min.toString()
        popupmaxtemp.text = model.max.toString()
        popuphumidity.text = model.humidity
        popuppressure.text = model.pressure
        popupsum.text = model.sum.toString()
        popupraingauge.text = model.raingauge
        popupwind.text = model.wind

        builder.setView(dialogLayout)
        detailDialog = builder.create()
        detailDialog.show()
    }

    private fun handleFirstDateSuccess(model: FirstDayDto){
        minDate = model.date
    }

}
