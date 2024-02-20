package com.training.shareplate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.training.shareplate.R
import com.training.shareplate.model.OrderItem
import com.training.shareplate.model.ShareItem

class ShowOrdersAdapter(options: FirestoreRecyclerOptions<OrderItem>) :
    FirestoreRecyclerAdapter<OrderItem, ShowOrdersAdapter.ShowOrdersViewHolder>(options) {

    private lateinit var listener: OnClickOrderListener

    interface OnClickOrderListener {
        fun onAcceptClick(item: OrderItem)
    }

    fun setOnClickListener(listener: OnClickOrderListener) {
        this.listener = listener
    }

    inner class ShowOrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val orderIdView : TextView = itemView.findViewById(R.id.order_id)
        private val orderedByView : TextView = itemView.findViewById(R.id.order_orderedBy)
        private val requestDateView : TextView = itemView.findViewById(R.id.order_requestDate)
        val acceptBtn : TextView = itemView.findViewById(R.id.order_accept)

        fun bind(item: OrderItem){
            orderIdView.text = item.orderId
            orderedByView.text = item.orderedBy
            requestDateView.text = item.requestDate

            acceptBtn.setOnClickListener{
                listener.onAcceptClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowOrdersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return ShowOrdersViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShowOrdersViewHolder, position: Int, model: OrderItem) {
        model.orderId = snapshots.getSnapshot(position).id
        holder.bind(model)
    }
}