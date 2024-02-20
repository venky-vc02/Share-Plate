package com.training.shareplate

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.training.shareplate.adapter.ShowOrdersAdapter
import com.training.shareplate.model.OrderItem
import com.training.shareplate.model.ShareItem

class ShowOrders : ComponentActivity() {

    private lateinit var adapter : ShowOrdersAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_orders)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        database = FirebaseDatabase.getInstance()

        recyclerView = findViewById(R.id.new_orders_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val query = firestore.collection("food_listings")
            .whereEqualTo("listUser", auth.currentUser?.uid)

        val options = FirestoreRecyclerOptions.Builder<OrderItem>()
            .setQuery(query, OrderItem::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = ShowOrdersAdapter(options)
        recyclerView.adapter = adapter

        adapter.setOnClickListener(object: ShowOrdersAdapter.OnClickOrderListener{
            override fun onAcceptClick(item: OrderItem) {
                firestore.collection("food_listings").document(item.orderId)
                    .update("status", "Accepted")
                    .addOnSuccessListener {
                        database.reference.child("food_listings").child(item.orderId).child("status")
                            .setValue("Accepted").addOnSuccessListener {
                                Toast.makeText(applicationContext, "Order Accepted", Toast.LENGTH_SHORT).show()
                            }
                    }
            }

        })
    }
}