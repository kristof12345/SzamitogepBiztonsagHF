package com.itsecurityteam.caffstore.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.model.Caff
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class CaffsAdapter: RecyclerView.Adapter<CaffsAdapter.CaffViewHolder>() {
    class CaffViewHolder(private val listener: ((Caff) -> Unit)?, inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.store_item, parent, false)) {

        private val image: ImageView = itemView.findViewById(R.id.ivThumbnail)
        private val creator: TextView = itemView.findViewById(R.id.tvCreator)
        private val creation: TextView = itemView.findViewById(R.id.tvCreationDate)
        private val base: MaterialCardView = itemView.findViewById(R.id.cvItemBase)

        fun bind(caff: Caff) {
            image.setImageBitmap(caff.thumbnail)
            creator.text = caff.creator

            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)
                .withZone(ZoneId.systemDefault())
            creation.text = caff.creationDate.format(formatter)

            base.setOnClickListener {
                listener?.invoke(caff)
            }
        }
    }

    private var listener: ((Caff) -> Unit)? = null

    private var caffs = emptyList<Caff>()
    var Caffs: List<Caff>
        get() = caffs
        set(value) {
            caffs = value
            notifyDataSetChanged()
        }

    fun setOnClickListener(listener: (caff: Caff) -> Unit) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaffViewHolder {
        return CaffViewHolder(listener, LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: CaffViewHolder, position: Int) {
        holder.bind(caffs[position])
    }

    override fun getItemCount(): Int {
        return caffs.size
    }
}