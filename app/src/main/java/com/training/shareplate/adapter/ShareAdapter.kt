package com.training.shareplate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.training.shareplate.R
import com.training.shareplate.model.ShareItem

class ShareAdapter(options: FirestoreRecyclerOptions<ShareItem>) :
    FirestoreRecyclerAdapter<ShareItem, ShareAdapter.ShareViewHolder>(options) {

    private lateinit var listener: OnClickShareListener

    interface OnClickShareListener {
        fun onDeleteClick(item: ShareItem)
    }

    fun setOnClickListener(listener: OnClickShareListener) {
        this.listener = listener
    }

    inner class ShareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val reasonText: TextView = itemView.findViewById(R.id.text_reason)
        private val timeText: TextView = itemView.findViewById(R.id.text_time)
        private val peopleText: TextView = itemView.findViewById(R.id.text_people_count)
        private val locationText: TextView = itemView.findViewById(R.id.text_location)
        private val deleteBtn: TextView = itemView.findViewById(R.id.delete_btn)


        fun bind(model : ShareItem) {
            reasonText.text = model.reason
            timeText.text = model.time
            peopleText.text = model.peopleCount
            locationText.text = model.location

            deleteBtn.setOnClickListener{
                listener.onDeleteClick(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_share, parent, false)
        return ShareViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShareViewHolder, position: Int, model: ShareItem) {
        holder.bind(model)
        model.docId = snapshots.getSnapshot(position).id
    }
}