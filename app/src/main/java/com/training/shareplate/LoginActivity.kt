package com.training.shareplate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity: ComponentActivity() {

    private lateinit var auth: FirebaseAuth //Firebase Authentication Instance
    private var dialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        auth = Firebase.auth //Initialising the Authentication Instance

        /* Share Button ID */
        val btnShare = findViewById<Button>(R.id.btnShare)

        /* Receive Button ID */
        val btnReceive = findViewById<Button>(R.id.btnReceive)

        /* Share Button on Click Listener */
        btnShare.setOnClickListener {
            if(auth.currentUser != null){
                val intent = Intent(this, ShareActivity::class.java)
                startActivity(intent)
            } else {
                beginLogin(this,"Share")
            }
        }

        /* Receive Button on Click Listener */
        btnReceive.setOnClickListener {
            if (auth.currentUser != null) {
                val intent = Intent(this, NeedActivity::class.java)
                startActivity(intent)
            } else {
                beginLogin(this,"Receive")
            }

        }
    }

    private fun beginLogin(context: Context, endActivity: String) {
        val view = layoutInflater.inflate(R.layout.fragment_login, null)
        dialog = BottomSheetDialog(context)

        dialog?.setCancelable(false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog?.setContentView(view)
        dialog?.show()

        val loginBtn = view.findViewById<Button>(R.id.loginBtn)

        val btnClose = view.findViewById<ImageButton>(R.id.closeDialog)

        btnClose.setOnClickListener {
            dialog?.dismiss()
        }

        loginBtn.setOnClickListener {
            val userEmail = view.findViewById<EditText>(R.id.emailAddress).text.toString()
            val userPass = view.findViewById<EditText>(R.id.password).text.toString()
            if (userEmail != "") {
                if (userPass != "") {
                    loginInUser(userEmail, userPass, endActivity);
                } else {
                    Toast.makeText(this, "Please Enter a Password", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please Enter your Email Address", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun loginInUser(userEmail: String, userPass: String, endActivity: String) {
        Log.d("Email", userEmail)
        auth.signInWithEmailAndPassword(userEmail, userPass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    dialog?.dismiss()
                    if (endActivity === "Share") {
                        val intent = Intent(applicationContext, ShareActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(applicationContext, NeedActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(
                        baseContext,
                        "Authentication failed. Please retry" + task.exception?.message,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}