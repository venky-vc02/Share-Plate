package com.training.shareplate.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.training.shareplate.R
import com.training.shareplate.model.ReceiveItem

class ReceiveAdapter(options: FirestoreRecyclerOptions<ReceiveItem>)
    :FirestoreRecyclerAdapter<ReceiveItem, ReceiveAdapter.ReceiveViewHolder>(options){

    private lateinit var listener: OnClickReceiveListener

    inner class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val reasonText: TextView = itemView.findViewById(R.id.rText_reason)
        private val ownerText : TextView = itemView.findViewById(R.id.rText_owner)
        private val timeText: TextView = itemView.findViewById(R.id.rText_time)
        private val peopleText: TextView = itemView.findViewById(R.id.rText_people_count)
        private val locationText: TextView = itemView.findViewById(R.id.rText_location)
        private val orderBtn: TextView = itemView.findViewById(R.id.rOrder_btn)


        fun bind(model : ReceiveItem) {
            reasonText.text = model.reason
            ownerText.text = model.listedBy
            timeText.text = model.time
            peopleText.text = model.peopleCount
            locationText.text = model.location
            orderBtn.isEnabled = model.status == "UnClaimed"
            if(!orderBtn.isEnabled) orderBtn.setTextColor(Color.GRAY)

            orderBtn.setOnClickListener{
                listener.onRequestClick(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiveViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_receive, parent, false)
        return ReceiveViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceiveViewHolder, position: Int, model: ReceiveItem) {
        holder.bind(model)
        model.docId = snapshots.getSnapshot(position).id
    }

    interface OnClickReceiveListener {
        fun onRequestClick(item: ReceiveItem)
    }

    fun setOnClickListener(listener: OnClickReceiveListener) {
        this.listener = listener
    }
}