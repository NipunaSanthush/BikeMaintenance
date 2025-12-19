package com.example.bikemaintenance

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.bikemaintenance.utils.SessionManager
import java.util.Calendar

class SignUpActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var ivBrandLogo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        session = SessionManager(this)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etBirthday = findViewById<EditText>(R.id.etBirthday)
        val etMobile = findViewById<EditText>(R.id.etMobile)
        val etAddress = findViewById<EditText>(R.id.etAddress)

        val etBikeModel = findViewById<EditText>(R.id.etBikeModel)

        val etBikeNumber = findViewById<EditText>(R.id.etBikeNumber)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        val spinnerBrand = findViewById<Spinner>(R.id.spinnerBrand)
        ivBrandLogo = findViewById(R.id.ivBrandLogo)

        etBirthday.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                etBirthday.setText("$selectedYear-${selectedMonth + 1}-$selectedDay")
            }, year, month, day)
            dpd.show()
        }

        val brands = listOf("Honda", "Yamaha", "Bajaj", "TVS", "Suzuki", "Other")
        val brandAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, brands)
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBrand.adapter = brandAdapter

        spinnerBrand.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedBrand = brands[position]
                updateBrandLogo(selectedBrand)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnRegister.setOnClickListener {
            val name = etFullName.text.toString()
            val dob = etBirthday.text.toString()
            val mobile = etMobile.text.toString()
            val address = etAddress.text.toString()

            val bikeNum = etBikeNumber.text.toString()
            val username = etUsername.text.toString()
            val pass = etPassword.text.toString()

            val brand = spinnerBrand.selectedItem.toString()
            val model = etBikeModel.text.toString()

            if (name.isEmpty() || dob.isEmpty() || mobile.isEmpty() || username.isEmpty() || pass.isEmpty() || model.isEmpty()) {
                Toast.makeText(this, "Please fill all details!", Toast.LENGTH_SHORT).show()
            } else {
                session.createAccount(
                    name, username, pass, mobile, address, dob,
                    brand, model, bikeNum
                )

                Toast.makeText(this, "Account Created! Welcome $name", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        tvLoginLink.setOnClickListener {
            Toast.makeText(this, "Login Page Coming Soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateBrandLogo(brand: String) {
        val logoRes = when (brand) {
            "Honda" -> R.drawable.honda
            "Yamaha" -> R.drawable.yamaha
            "Bajaj" -> R.drawable.bajaj
            "TVS" -> R.drawable.tvs
            "Suzuki" -> R.drawable.suzuki
            else -> R.drawable.ic_motorcycle
        }

        try {
            ivBrandLogo.setImageResource(logoRes)
        } catch (e: Exception) {
            ivBrandLogo.setImageResource(R.drawable.ic_motorcycle)
        }
    }
}