package com.training.shareplate.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.training.shareplate.R
import com.training.shareplate.model.AddListItem

class AddListAdapter(private val dataList: MutableList<AddListItem>) :
    RecyclerView.Adapter<AddListAdapter.AddListViewHolder>() {

    inner class AddListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val input1 : TextInputEditText = itemView.findViewById(R.id.add_food_itemName)
        val input2 : TextInputEditText = itemView.findViewById(R.id.add_food_itemQuantity)
        val removeBtn : ImageButton = itemView.findViewById(R.id.add_food_removeBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_food, parent, false)
        return AddListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: AddListViewHolder, position: Int) {
        val item = dataList[position]
        holder.input1.setText(item.itemName)
        holder.input2.setText(item.quantity)

        holder.input1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                item.itemName = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}

        })

        holder.input2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                item.quantity = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}

        })

        holder.removeBtn.setOnClickListener{
            dataList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}