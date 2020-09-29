package com.itsecurityteam.caffstore.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.model.Caff
import com.itsecurityteam.caffstore.model.Comment
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class CommentAdapter(private val deletable: Boolean) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    class CommentViewHolder(private val listener: ((Comment) -> Unit)?, private val deletable: Boolean, inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.comment_item, parent, false)) {

        private val creator: TextView = itemView.findViewById(R.id.tvCommentName)
        private val creation: TextView = itemView.findViewById(R.id.tvCommentDate)
        private val text: TextView = itemView.findViewById(R.id.tvCommentText)
        private val delete: ImageButton = itemView.findViewById(R.id.ibDelete)

        fun bind(comment: Comment) {
            creator.text = comment.userName
            text.text = comment.text

            val formatter =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)
                    .withZone(ZoneId.systemDefault())
            creation.text = comment.addTime.format(formatter)

            if (deletable) {
                delete.setOnClickListener {
                    listener?.invoke(comment)
                }

                delete.visibility = View.VISIBLE
            }
        }
    }

    private var listener: ((Comment) -> Unit)? = null

    private var comments = emptyList<Comment>()
    var commentsProp: List<Comment>
        get() = comments
        set(value) {
            comments = value
            notifyDataSetChanged()
        }

    fun setOnDeleteListener(listener: (comment: Comment) -> Unit) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(listener, deletable, LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(commentsProp[position])
    }

    override fun getItemCount(): Int {
        return commentsProp.size
    }
}