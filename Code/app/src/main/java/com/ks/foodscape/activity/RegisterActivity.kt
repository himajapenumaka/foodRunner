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

class RegisterActivity : AppCompatActivity() {

    lateinit var etNameRegister: EditText
    lateinit var etEmailRegister: EditText
    lateinit var etMobNoRegister: EditText
    lateinit var etDeliveryAddRegister: EditText
    lateinit var etPwRegister: EditText
    lateinit var etConfirmPwRegister: EditText
    lateinit var btnRegister: Button
    lateinit var toolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        println("Id is ${R.id.toolbarRegister}")
        toolbar = findViewById(R.id.toolbarRegister)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences=getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        etNameRegister=findViewById(R.id.etNameRegister)
        etEmailRegister=findViewById(R.id.etEmailRegister)
        etConfirmPwRegister=findViewById(R.id.etConfirmPwRegister)
        etDeliveryAddRegister=findViewById(R.id.etDeliveryAddRegister)
        etMobNoRegister=findViewById(R.id.etMobNoRegister)
        etPwRegister=findViewById(R.id.etPwRegister)
        btnRegister=findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener{


            val index = etEmailRegister.text.toString().indexOf('@')
            val domain: String? = if (index == -1) null else etEmailRegister.text.toString().substring(index + 1)

            if(etMobNoRegister.text.toString().length!=10 || domain!="gmail.com" || domain==null || etConfirmPwRegister.text.toString()!=etPwRegister.text.toString()) {

                if (etMobNoRegister.text.toString().length != 10)
                    Toast.makeText(
                        this@RegisterActivity,
                        "Mobile Number should be of 10 digits",
                        Toast.LENGTH_SHORT
                    ).show()

                if (domain != "gmail.com" || domain == null)
                    Toast.makeText(
                        this@RegisterActivity,
                        "Email should be in correct format",
                        Toast.LENGTH_SHORT
                    ).show()

                if (etConfirmPwRegister.text.toString() != etPwRegister.text.toString())
                    Toast.makeText(
                        this@RegisterActivity,
                        "Passwords should match",
                        Toast.LENGTH_SHORT
                    ).show()

            }

            else{

                val url= "http://13.235.250.119/v2/register/fetch_result"
                val queue= Volley.newRequestQueue(this@RegisterActivity)
                val jsonParams=JSONObject()
                jsonParams.put("name",etNameRegister.text.toString())
                jsonParams.put("mobile_number",etMobNoRegister.text.toString())
                jsonParams.put("password",etPwRegister.text.toString())
                jsonParams.put("address",etDeliveryAddRegister.text.toString())
                jsonParams.put("email",etEmailRegister.text.toString())

                if(ConnectionManager().checkConnectivity(this@RegisterActivity)){

                    val jsonObjectRequest=object : JsonObjectRequest(Request.Method.POST,url,jsonParams,
                        Response.Listener {
                            try{

                                val jsondata=it.getJSONObject("data")
                                val success= jsondata.getBoolean("success")
                                if(success){

                                    val data=jsondata.getJSONObject("data")
                                    val intent=Intent(this@RegisterActivity,MainActivity::class.java)
                                    putInSharedPreferences()
                                    sharedPreferences.edit().putString("User_id",data.getString("user_id")).apply()
                                    startActivity(intent)
                                }
                                else{
                                    val data=jsondata.getString("errorMessage")
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        data,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                            catch(e: Exception){
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Some exception occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        },
                        Response.ErrorListener {
                            if (this@RegisterActivity != null)
                                Toast.makeText(
                                    this@RegisterActivity,
                                    it.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                        }
                    ) {
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

                    val dialog = android.app.AlertDialog.Builder(this@RegisterActivity)
                    dialog.setTitle("Error!")
                    dialog.setMessage("Internet Connection not found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        this@RegisterActivity?.finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@RegisterActivity)
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

      override fun onBackPressed() {
          super.onBackPressed()
       }

    fun putInSharedPreferences(){
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("Name",etNameRegister.text.toString()).apply()
        sharedPreferences.edit().putString("Email",etEmailRegister.text.toString()).apply()
        sharedPreferences.edit().putString("MobNo",etMobNoRegister.text.toString()).apply()
        sharedPreferences.edit().putString("Address",etDeliveryAddRegister.text.toString()).apply()
    }

}
