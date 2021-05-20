package com.ks.foodscape.activity

import android.content.Intent
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
import com.ks.foodscape.util.ConnectionManager
import com.ks.foodscape.R
import org.json.JSONObject

class ForgotPassword1Activity : AppCompatActivity() {

    lateinit var etMobNoForgotPw: EditText
    lateinit var etEmailForgotPw: EditText
    lateinit var btnNextForgotPw: Button
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        toolbar = findViewById(R.id.toolbarForgotPw)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Forgot Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etMobNoForgotPw=findViewById(R.id.etMobNoForgotPw)
        etEmailForgotPw=findViewById(R.id.etEmailForgotPw)
        btnNextForgotPw=findViewById(R.id.btnNextForgotPw)

        btnNextForgotPw.setOnClickListener{

            val url= "http://13.235.250.119/v2/forgot_password/fetch_result"
            val queue= Volley.newRequestQueue(this@ForgotPassword1Activity)
            val jsonParams=JSONObject()
            jsonParams.put("mobile_number",etMobNoForgotPw.text.toString())
            jsonParams.put("email",etEmailForgotPw.text.toString())

            if(ConnectionManager().checkConnectivity(this@ForgotPassword1Activity)){

                val jsonObjectRequest=object : JsonObjectRequest(Request.Method.POST,url,jsonParams,
                    Response.Listener{
                        try{
                                val jsondata=it.getJSONObject("data")
                                val success= jsondata.getBoolean("success")
                            if(success){

//                                val first_try=jsondata.getBoolean("first_try")
//                                if(first_try)
//                                    Toast.makeText(this@ForgotPassword1Activity,"OTP has sent to your entered Email",Toast.LENGTH_SHORT)
//                                else
//                                    Toast.makeText(this@ForgotPassword1Activity,"OTP has already sent to you previously",Toast.LENGTH_SHORT)

                                val intent=Intent(this@ForgotPassword1Activity,OtpActivity::class.java)
                                intent.putExtra("mobile_number",etMobNoForgotPw.text.toString())
                                startActivity(intent)
                            }

                            else{
                                val data=jsondata.getString("errorMessage")
                                Toast.makeText(
                                    this@ForgotPassword1Activity,
                                    data,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                        catch(e: Exception){
                            Toast.makeText(
                                this@ForgotPassword1Activity,
                                "Some exception occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    },
                    Response.ErrorListener {
                        if (this@ForgotPassword1Activity != null)
                            Toast.makeText(
                                this@ForgotPassword1Activity,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                    }){

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

                val dialog = android.app.AlertDialog.Builder(this@ForgotPassword1Activity)
                dialog.setTitle("Error!")
                dialog.setMessage("Internet Connection not found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    this@ForgotPassword1Activity?.finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@ForgotPassword1Activity)
                }
                dialog.create().show()

            }


        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}