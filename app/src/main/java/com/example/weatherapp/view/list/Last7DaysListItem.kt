package com.example.weatherapp.view.list

import android.annotation.SuppressLint
import android.text.Html
import android.view.View
import com.example.weatherapp.R
import com.example.weatherapp.database.entity.Last7DaysEntity
import com.mikepenz.fastadapter.FastAdapter.ViewHolder
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.example.weatherapp.view.list.Last7DaysListItem.Last7DaysViewHolder
import kotlinx.android.synthetic.main.item_7days.view.*


class Last7DaysListItem(model: Last7DaysEntity) : ModelAbstractItem<Last7DaysEntity, Last7DaysViewHolder>(model) {

    override val layoutRes: Int
        get() = R.layout.item_7days

    override val type: Int
        get() = R.id.last7days_type_id

    override fun getViewHolder(v: View): Last7DaysViewHolder {
        return Last7DaysViewHolder(v)
    }


    class Last7DaysViewHolder(itemView: View) : ViewHolder<Last7DaysListItem>(itemView) {



        @SuppressLint("SetTextI18n")
        override fun bindView(item: Last7DaysListItem, payloads: MutableList<Any>) {
            val model = item.model
            var data = ""
            for (i in 0..9){
                data += model.date[i]
            }
            itemView.daysname.text = data
            itemView.daystempairday.text = "${model.tempairday}/"
            itemView.daystempairnight.text = "${model.tempairnight}°C"
            itemView.daystempinside.text = "Temperatura wewnątrz ${model.tempinside}°C"
            itemView.daysrain.text = (Html.fromHtml("<b>${model.raingauge} mm</b><br /> Opady"))
            itemView.dayswind.text = (Html.fromHtml("<b>${model.wind} km/h</b><br /> Wiatr"))
            itemView.dayspressure.text = (Html.fromHtml("<b>${model.pressure} hPa</b><br /> Ciśnienie"))
            itemView.dayshumidity.text = (Html.fromHtml("<b>${model.humidity} %</b><br /> Wilgotność"))
            itemView.daystempair.text = (Html.fromHtml("<b>${model.tempair}°C</b><br /> Średnia temp"))


        }

        override fun unbindView(item: Last7DaysListItem) {
            itemView.daysname.text = null
            itemView.daystempairday.text = null
            itemView.daystempairnight.text = null
            itemView.daystempinside.text = null
            itemView.daysrain.text = null
            itemView.dayswind.text = null
            itemView.dayspressure.text = null
            itemView.dayshumidity.text = null
            itemView.daystempair.text = null
        }

    }



}

