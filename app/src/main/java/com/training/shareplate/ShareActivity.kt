package com.training.shareplate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.training.shareplate.adapter.ShareAdapter
import com.training.shareplate.model.ShareItem

class ShareActivity : ComponentActivity() {

    private lateinit var adapter: ShareAdapter
    private lateinit var auth: FirebaseAuth //Firebase Authentication Instance
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        database = FirebaseDatabase.getInstance()

        val ordersBtn : ImageButton = findViewById(R.id.btn_show_orders)
        val recyclerList : RecyclerView = findViewById(R.id.foodList)
        val logoutBtn : ImageButton = findViewById(R.id.btn_logout_share)
        val addNewOrder : Button = findViewById(R.id.addShareFood)

        ordersBtn.setOnClickListener{
            startActivity(Intent(this, ShowOrders::class.java))
        }

        addNewOrder.setOnClickListener {
            startActivity(Intent(this, AddFoodList::class.java))
        }

        val userId = auth.currentUser?.uid
        val query = firestore.collection("food_listings")
                        .whereEqualTo("listUser", userId)

        val options = FirestoreRecyclerOptions.Builder<ShareItem>()
            .setQuery(query, ShareItem::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = ShareAdapter(options)
        recyclerList.adapter = adapter
        recyclerList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerList.itemAnimator = null
        logoutBtn.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        adapter.setOnClickListener(object : ShareAdapter.OnClickShareListener{
            override fun onDeleteClick(item: ShareItem) {
                firestore.collection("food_listings").document(item.docId)
                    .delete().addOnSuccessListener {
                        database.reference.child("food_listings").child(item.docId)
                            .removeValue().addOnSuccessListener {
                                Toast.makeText(applicationContext, "Your Order with ID: ${item.docId} is successfully Deleted", Toast.LENGTH_SHORT).show()
                            }
                    }
            }

        })
    }
}