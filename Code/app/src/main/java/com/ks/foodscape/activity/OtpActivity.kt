package com.ks.foodscape.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ks.foodscape.R
import com.ks.foodscape.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class OtpActivity : AppCompatActivity() {

    lateinit var etOtp: EditText
    lateinit var etpw: EditText
    lateinit var etCpw: EditText
    lateinit var btnSubmit: Button
    lateinit var toolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        etOtp=findViewById(R.id.etOtpOtp)
        etpw=findViewById(R.id.etpwOtp)
        etCpw=findViewById(R.id.etconfirmPwOtp)
        btnSubmit=findViewById(R.id.btnSubmitOtp)
        toolbar=findViewById(R.id.toolbarOtp)
        sharedPreferences=getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)

        setSupportActionBar(toolbar)
        supportActionBar?.title="Reset Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnSubmit.setOnClickListener {

            if (etCpw.text.toString() != etpw.text.toString())
                Toast.makeText(this@OtpActivity, "Passwords do not match!", Toast.LENGTH_SHORT)
            else {

                val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                val queue = Volley.newRequestQueue(this@OtpActivity)
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", intent.getStringExtra("mobile_number"))
                jsonParams.put("password", etpw.text.toString())
                jsonParams.put("otp", etOtp.text.toString())

                if (ConnectionManager().checkConnectivity(this@OtpActivity)) {

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, jsonParams,
                        Response.Listener {

                            try {
                                val jsondata = it.getJSONObject("data")
                                val success = jsondata.getBoolean("success")
                                if (success) {
                                    val data = jsondata.getString("successMessage")
                                    Toast.makeText(
                                        this@OtpActivity,
                                        data,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this@OtpActivity, LoginActivity::class.java)
                                    sharedPreferences.edit().clear().apply()
                                    startActivity(intent)

                                } else {
                                    val data = jsondata.getString("errorMessage")
                                    Toast.makeText(
                                        this@OtpActivity,
                                        data,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@OtpActivity,
                                    "Some exception occurred",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        },
                        Response.ErrorListener {
                            if (this@OtpActivity != null)
                                Toast.makeText(
                                    this@OtpActivity,
                                    it.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                        }) {

                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "c5cb48cf92fab1"
                            return headers
                        }

                    }

                    queue.add(jsonObjectRequest)

                } else {
                    val dialog = android.app.AlertDialog.Builder(this@OtpActivity)
                    dialog.setTitle("Error!")
                    dialog.setMessage("Internet Connection not found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        this@OtpActivity?.finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@OtpActivity)
                    }
                    dialog.create().show()
                }

            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}