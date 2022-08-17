package net.davidam.candle.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.davidam.candle.R
import net.davidam.candle.model.WordDocument

class CustomAdapterWord(
    private val context: Context,
    private val layout: Int
) : RecyclerView.Adapter<CustomAdapterWord.ViewHolder>() {

    private var dataList: List<WordDocument> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    internal fun setWords(wordDocs: List<WordDocument>) {
        this.dataList = wordDocs
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: WordDocument){
            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem

            val tvWord = itemView.findViewById<TextView>(R.id.tvWord)
            tvWord.text = dataItem.words
            val tvWordMeaning = itemView.findViewById<TextView>(R.id.tvWordMeaning)
            val rnd: Int = (Math.random()*(dataItem.meanings!!.size)).toInt()
            tvWordMeaning.text = dataItem.meanings!![rnd]

            //  val hsvWord = itemView.findViewById<HorizontalScrollView>(R.id.hsvWord)
            val hsvWordMeaning = itemView.findViewById<HorizontalScrollView>(R.id.hsvWordMeaning)

            val ivWord = itemView.findViewById<ImageView>(R.id.ivWord)
            if (dataItem.imageUrl == "") {
                ivWord.visibility = View.GONE
                //  hsvWord.layoutParams.width = -1 // (POR HACER) Cambiar hsvWord por llWord...
                tvWord.layoutParams.width = -1
                hsvWordMeaning.layoutParams.width = -1
            } else {
                // foto de internet a traves de Picasso
                val url = "https://yamurrah.com.au/wp-content/uploads/2019/01/dog-square.jpg"
                Picasso.get().load(url).resize(80.dpToPx(context), 80.dpToPx(context)).into(ivWord)
            }

            itemView.tag = dataItem
        }

        private fun Int.dpToPx(context: Context) =
            this * context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
    }
}