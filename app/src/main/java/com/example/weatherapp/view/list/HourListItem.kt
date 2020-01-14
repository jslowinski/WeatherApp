package com.example.weatherapp.view.list

import android.annotation.SuppressLint
import android.text.Html
import android.view.View
import com.example.weatherapp.R
import com.example.weatherapp.api.model.HourHistoryDto
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.ModelAbstractItem
import kotlinx.android.synthetic.main.item_hour_history.view.*
import com.example.weatherapp.view.list.HourListItem.HourViewHolder
import kotlin.math.ln

class HourListItem(model: HourHistoryDto) : ModelAbstractItem<HourHistoryDto, HourViewHolder>(model){

    override val layoutRes: Int
        get() = R.layout.item_hour_history

    override val type: Int
        get() = R.id.hourHistory_type_id

    override fun getViewHolder(v: View): HourViewHolder {
        return HourViewHolder(v)
    }



    @SuppressLint("SetTextI18n")
    class HourViewHolder(itemView: View) : FastAdapter.ViewHolder<HourListItem>(itemView){

        fun calculateDewPoint(tempair: Double, humidity: Double): Double {
            val variable = (ln(humidity/100) + ((17.27 * tempair) / (237.3 + tempair))) / 17.27
            return (237.3 * variable) / (1 - variable)
        }

        override fun bindView(item: HourListItem, payloads: MutableList<Any>) {
            val model = item.model

            if (model.rain.toString() == "false"){
                //nie pada
                if (model.wind >= 20) {
                    itemView.houricon.setImageResource(R.drawable.icons8_windy_weather_100px)
                } else {
                    if (model.tempair >= 20){
                        itemView.houricon.setImageResource(R.drawable.icons8_sun_100px)
                    } else if (model.tempair < 20 && model.tempair >= 10){
                        itemView.houricon.setImageResource(R.drawable.icons8_partly_cloudy_day_100px)
                    } else {
                        itemView.houricon.setImageResource(R.drawable.icons8_cloud_100px)
                    }
                }
            } else {
                //pada
                if (model.tempair <= 0){
                    //temperatura na minusie
                    itemView.houricon.setImageResource(R.drawable.icons8_snow_100px)
                } else {
                    //temperatura na plusie
                    itemView.houricon.setImageResource(R.drawable.icons8_rain_100px)
                }

            }

            itemView.hour.text = model.time
            itemView.hourtempair.text = model.tempair.toString() + "°C"
            itemView.hourtempinside.text = "Temperatura wewnątrz ${model.tempinside}°C"
            itemView.hourhumidity.text = (Html.fromHtml("<b>${model.humidity} %</b><br /> Wilgotność"))
            itemView.hourpressure.text = (Html.fromHtml("<b>${model.pressure} hPa</b><br /> Ciśnienie"))
            itemView.hourwind.text = (Html.fromHtml("<b>%.2f km/h</b><br /> Wiatr".format(model.wind)))
            if (model.rain.toString() == "false") {
                itemView.hourrain.text = (Html.fromHtml("<b>Brak</b>"))
            } else {
                itemView.hourrain.text = (Html.fromHtml("<b>Występowały</b><br />%.2f mm".format(model.raingauge)))
            }

            itemView.hourdewpoint.text = (Html.fromHtml("<b>%.2f °C</b><br /> Punkt rosy".format(calculateDewPoint(model.tempair,model.humidity))))
        }

        override fun unbindView(item: HourListItem) {
            itemView.hour.text = null
            itemView.hourtempair.text = null
            itemView.hourtempinside.text = null
            itemView.hourhumidity.text = null
            itemView.hourpressure.text = null
            itemView.hourwind.text = null
            itemView.hourrain.text = null
            itemView.hourdewpoint.text = null

        }
    }
}

