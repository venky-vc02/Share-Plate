package com.training.shareplate

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.training.shareplate.adapter.AddListAdapter
import com.training.shareplate.model.AddListItem
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

class AddFoodList : AppCompatActivity() {

    private val dataList = mutableListOf<AddListItem>()
    private lateinit var adapter: AddListAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database : FirebaseDatabase
    private lateinit var currentUID : String
    private lateinit var userName : String

    private lateinit var selectDate : TextInputEditText
    private lateinit var selectTime : TextInputEditText

    private lateinit var newReason : String
    private lateinit var newDate : String
    private lateinit var newTime : String
    private lateinit var newLocation : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food_list)

        auth = FirebaseAuth.getInstance()
        currentUID = auth.currentUser?.uid ?: String()
        userName = auth.currentUser?.email.toString()

        firestore = FirebaseFirestore.getInstance()
        database = FirebaseDatabase.getInstance()

        val reasonInput : TextInputEditText = findViewById(R.id.add_reason)
        selectDate = findViewById(R.id.add_date)
        selectTime = findViewById(R.id.add_time)
        val locationInput : TextInputEditText = findViewById(R.id.add_location)
        val addFoodItemsBtn : ImageButton = findViewById(R.id.add_food_inputBtn)
        val addedFoodList : RecyclerView = findViewById(R.id.add_food_list)
        val createFoodOrder : Button = findViewById(R.id.afc_add_order)

        adapter = AddListAdapter(dataList)
        addedFoodList.adapter = adapter
        addedFoodList.layoutManager = LinearLayoutManager(this);

        addFoodItemsBtn.setOnClickListener {
            dataList.add(AddListItem("", ""))
            adapter.notifyItemInserted(dataList.size - 1)
        }

        reasonInput.doAfterTextChanged { text ->
            if (text.isNullOrEmpty()) {
                reasonInput.error = "Reason Field Cannot be kept empty"
            }
        }

        selectDate.doAfterTextChanged { text ->
            if (text.isNullOrEmpty()) {
                reasonInput.error = "Date is required"
            }
        }

        selectTime.doAfterTextChanged { text ->
            if (text.isNullOrEmpty()) {
                reasonInput.error = "Time is required"
            }
        }

        locationInput.doAfterTextChanged { text ->
            if (text.isNullOrEmpty()) {
                reasonInput.error = "Location Field cannot be kept empty"
            }
        }

        selectDate.setOnClickListener {
            showDatePickerDialog()
        }

        selectTime.setOnClickListener {
            showTimePickerDialog()
        }

        createFoodOrder.setOnClickListener {
            newReason = reasonInput.text.toString()
            newDate = selectDate.text.toString()
            newTime = selectTime.text.toString()
            newLocation = locationInput.text.toString()

            startOrderCreate()

        }
    }

    private fun startOrderCreate() {
        var count = 0
        val foodItemsList = ArrayList<AddListItem>()
        for(item in dataList) {
            count += item.quantity.toInt()
            foodItemsList.add(AddListItem(item.itemName, item.quantity))
        }

        count.toDouble().roundToInt()
        val peopleCount = "For $count people"

        val finalData = hashMapOf(
            "listUser" to currentUID,
            "listedBy" to "Listed by $userName",
            "location" to newLocation,
            "peopleCount" to peopleCount,
            "reason" to newReason,
            "status" to "Unclaimed",
            "time" to "$newDate $newTime",
            "foodList" to foodItemsList,
        )

        val finalStatus = hashMapOf(
            "listUser" to currentUID,
            "status" to "Unclaimed",
            "time" to "$newDate $newTime",
            "peopleCount" to peopleCount,
            "listedBy" to "Listed by $userName",
            "location" to newLocation
        )

        firestore.collection("food_listings").add(finalData)
            .addOnSuccessListener {documentReference ->
                val docId = documentReference.id
                val dataReference = database.reference
                dataReference.child("food_listings").child(docId).setValue(finalStatus)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Order Created Successfully", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, ShareActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
                    }
            }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, hourSel: Int, minuteSel: Int ->
                // Format the time
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR, hourSel)
                cal.set(Calendar.MINUTE, minuteSel)
                val timeFormat = SimpleDateFormat("hh:mm a z", Locale.getDefault())
                // Set selected time to the EditText
                selectTime.setText(timeFormat.format(cal.time))
            },
            hour,
            minute,
            false // Use 24-hour format
        )
        timePickerDialog.show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val formattedDate = formatDate(year, month, dayOfMonth)
                // Set selected date to the EditText
                selectDate.setText(formattedDate)
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val suffix = when (dayOfMonth % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
        val dateFormat = SimpleDateFormat("MMM d'$suffix' yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}