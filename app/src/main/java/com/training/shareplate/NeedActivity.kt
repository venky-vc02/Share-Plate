package com.training.shareplate

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.training.shareplate.adapter.ReceiveAdapter
import com.training.shareplate.model.ReceiveItem
import java.util.Calendar

class NeedActivity : ComponentActivity() {

    private lateinit var adapter: ReceiveAdapter
    private lateinit var auth: FirebaseAuth //Firebase Authentication Instance
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_need)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        database = FirebaseDatabase.getInstance()

        val noListText : TextView = findViewById(R.id.needNoListTxt)
        val recyclerList : RecyclerView = findViewById(R.id.needFoodList)
        val logoutBtn : ImageButton = findViewById(R.id.btn_logout_need)

        val userId = auth.currentUser?.uid
        val query = FirebaseFirestore.getInstance().collection("food_listings")
            .whereNotEqualTo("listUser", userId)

        val options = FirestoreRecyclerOptions.Builder<ReceiveItem>()
            .setQuery(query, ReceiveItem::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = ReceiveAdapter(options)
        recyclerList.adapter = adapter
        recyclerList.layoutManager = LinearLayoutManager(this)

        val data = mapOf(
            "status" to "Requested",
            "orderedBy" to auth.currentUser?.email,
            "requestDate" to Calendar.getInstance().time.toString()
        )

        adapter.setOnClickListener(object : ReceiveAdapter.OnClickReceiveListener{
            override fun onRequestClick(item: ReceiveItem) {
                firestore.collection("food_listings").document(item.docId)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        database.reference.child("food_listings").child(item.docId)
                            .updateChildren(data).addOnSuccessListener {
                                Toast.makeText(applicationContext, "Order with ID: ${item.docId} is requested", Toast.LENGTH_SHORT).show()
                            }
                    }
            }

        })

        logoutBtn.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}