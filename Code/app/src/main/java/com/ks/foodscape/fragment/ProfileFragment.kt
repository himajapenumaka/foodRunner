package com.ks.foodscape.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Gravity.apply
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.GravityCompat.apply
import com.ks.foodscape.R

class ProfileFragment : Fragment() {

    lateinit var txt1: TextView
    lateinit var txt2: TextView
    lateinit var txt3: TextView
    lateinit var txt4: TextView
    var sharedPreferences: SharedPreferences? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences=this.activity?.getSharedPreferences(getString(R.string.preferences_file),Context.MODE_PRIVATE)

        txt1 =view.findViewById(R.id.txt1)
        txt2=view.findViewById(R.id.txt2)
        txt3=view.findViewById(R.id.txt3)
        txt4=view.findViewById(R.id.txt4)

        txt1.text=sharedPreferences?.getString("Name","Name")
        txt2.text=sharedPreferences?.getString("Email","Email Id")
        txt3.text="+91-${sharedPreferences?.getString("MobNo","Mobile Number")}"
        txt4.text=sharedPreferences?.getString("Address","Address")

        return view
    }

}
