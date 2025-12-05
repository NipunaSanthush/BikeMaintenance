package com.example.bikemaintenance

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bikemaintenance.utils.SessionManager

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val session = SessionManager(this)

        if (session.isLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val etName = findViewById<EditText>(R.id.etSetupName)
        val etBike = findViewById<EditText>(R.id.etSetupBike)
        val etPlate = findViewById<EditText>(R.id.etSetupPlate)
        val btnStart = findViewById<Button>(R.id.btnGetStarted)

        btnStart.setOnClickListener {
            val name = etName.text.toString()
            val bike = etBike.text.toString()
            val plate = etPlate.text.toString()

            if (name.isNotEmpty() && bike.isNotEmpty() && plate.isNotEmpty()) {
                session.createLoginSession(name, bike, plate)

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}