package com.ks.foodscape.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ks.foodscape.R
import com.ks.foodscape.util.ConnectionManager
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var etIdLogin: EditText
    lateinit var etPwLogin: EditText
    lateinit var btnLogin: Button
    lateinit var txtForgotPwLogin: TextView
    lateinit var txtRegisterLogin: TextView
    lateinit var toolbarLogin: Toolbar
    var login=false

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)

        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn", false)
        login=isLoggedIn

        setContentView(R.layout.activity_login)

        toolbarLogin=findViewById(R.id.toolbarLogin)
        setSupportActionBar(toolbarLogin)
        supportActionBar?.title="Log In"

        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPwLogin = findViewById(R.id.txtForgotPwLogin)
        txtRegisterLogin = findViewById(R.id.txtRegisterLogin)
        etIdLogin = findViewById(R.id.etIdLogin)
        etPwLogin = findViewById(R.id.etPwLogin)


        btnLogin.setOnClickListener {

            val url= "http://13.235.250.119/v2/login/fetch_result"
            val queue= Volley.newRequestQueue(this@LoginActivity)
            val jsonParams=JSONObject()
            jsonParams.put("mobile_number",etIdLogin.text.toString())
            jsonParams.put("password",etPwLogin.text.toString())

            if(ConnectionManager().checkConnectivity(this@LoginActivity)){

                    val jsonObjectRequest=object : JsonObjectRequest(Request.Method.POST,url,jsonParams,
                        Response.Listener {
                            try{

                                val jsondata=it.getJSONObject("data")
                                val success=jsondata.getBoolean("success")
                                if(success){

                                    val data=jsondata.getJSONObject("data")
                                    login=true
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                                    sharedPreferences.edit().putString("Name",data.getString("name")).apply()
                                    sharedPreferences.edit().putString("Email",data.getString("email")).apply()
                                    sharedPreferences.edit().putString("MobNo",data.getString("mobile_number")).apply()
                                    sharedPreferences.edit().putString("Address",data.getString("address")).apply()
                                    sharedPreferences.edit().putString("User_id",data.getString("user_id")).apply()
                                    startActivity(intent)

                                }
                                else{
                                    val data=jsondata.getString("errorMessage")
                                    Toast.makeText(this@LoginActivity, data, Toast.LENGTH_SHORT).show()
                                }

                            }
                            catch (e: Exception){
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Some exception occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        Response.ErrorListener {
                            if (this@LoginActivity != null)
                                Toast.makeText(
                                    this@LoginActivity,
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

            }
            else{
                val dialog = android.app.AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error!")
                dialog.setMessage("Internet Connection not found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    this@LoginActivity?.finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@LoginActivity)
                }
                dialog.create().show()
            }

        }

        txtRegisterLogin.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        txtForgotPwLogin.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPassword1Activity::class.java)
            startActivity(intent)
        }

    }

    override fun onPause() {
        super.onPause()
        if(login)
            finish()
    }

    override fun onBackPressed() {
        val dialog = AlertDialog.Builder(this@LoginActivity)
        dialog.setTitle("Exit")
        dialog.setMessage("Are you sure you want to exit?")
        dialog.setPositiveButton("Yes") { text, listener ->
            ActivityCompat.finishAffinity(this@LoginActivity)
        }
        dialog.setNegativeButton("No") { text, listener -> }
        dialog.create().show()
    }

}