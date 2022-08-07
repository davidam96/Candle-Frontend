package net.davidam.candle.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.davidam.candle.model.WordDocument

class CustomAdapterWord(val context: Context,
                        val layout: Int
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

    internal fun setWords(productos: List<WordDocument>) {
        this.dataList = productos
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: WordDocument){
            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem

/*            val tvTituloProducto = itemView.findViewById<TextView>(R.id.tvTituloProductoRow)
            tvTituloProducto.text = dataItem.titulo

            val ivStar = itemView.findViewById<ImageView>(R.id.ivStar)
            if (dataItem.fav) {
                ivStar.setImageResource(android.R.drawable.star_big_on)
            } else {
                ivStar.setImageResource(android.R.drawable.star_big_off)
            }*/

            // foto de internet a traves de Picasso
            //  val ivImagenProducto = itemView.findViewById<ImageView>(R.id.ivImagenProductoRow)
            //  Picasso.get().load(dataItem.imagen).into(ivImagenProducto)

            itemView.tag = dataItem
        }

    }
}