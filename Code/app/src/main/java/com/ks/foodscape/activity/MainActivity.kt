package com.ks.foodscape.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.ks.foodscape.R
import com.ks.foodscape.adapters.MenuAdapter
import com.ks.foodscape.fragment.*
import com.ks.foodscape.fragment.MenuFragment.Companion.resId

class MainActivity : AppCompatActivity(){

    lateinit var drawerLayoutMain: DrawerLayout
    lateinit var frameLayoutMain: FrameLayout
    lateinit var coordinatorLayoutMain: CoordinatorLayout
    lateinit var toolbarMain: Toolbar
    lateinit var navigationViewMain: NavigationView
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    var previousMenuItem: MenuItem? = null

    lateinit var nameDrawerHeader: TextView
    lateinit var phnNoDrawerHeader: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayoutMain = findViewById(R.id.drawerLayoutMain)
        frameLayoutMain = findViewById(R.id.frameLayoutMain)
        coordinatorLayoutMain = findViewById(R.id.coordinatorLayoutMain)
        toolbarMain = findViewById(R.id.toolbarMain)
        navigationViewMain = findViewById(R.id.navigationViewMain)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)

        val view = navigationViewMain.getHeaderView(0)

        nameDrawerHeader = view.findViewById(R.id.nameDrawerHeader)
        phnNoDrawerHeader = view.findViewById(R.id.phnNoDrawerHeader)

        nameDrawerHeader.text = sharedPreferences.getString("Name", "Name")
        phnNoDrawerHeader.text = "+91-${sharedPreferences.getString("MobNo", "Mobile Number")}"

        setSupportActionBar(toolbarMain)
        supportActionBar?.title = "Home"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        openHomeFragment()

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayoutMain,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayoutMain.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationViewMain.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isChecked = true
            it.isCheckable = true
            previousMenuItem = it

            when (it.itemId) {

                R.id.home -> openHomeFragment()
                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutMain, FavouritesFragment()).commit()
                    supportActionBar?.title = "Favorite Restaurants"
                }
                R.id.orderhistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutMain, OrderHistoryFragment()).commit()
                    supportActionBar?.title = "Order History"
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutMain, ProfileFragment()).commit()
                    supportActionBar?.title = "My Profile"
                }
                R.id.faqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutMain, FaqsFragment()).commit()
                    supportActionBar?.title = "FAQs"
                }
                R.id.logout -> {
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Log Out")
                    dialog.setMessage("Are you sure you want to log out?")
                    dialog.setPositiveButton("Yes") { text, listener ->
                        sharedPreferences.edit().clear().apply()
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialog.setNegativeButton("No") { text, listener -> }
                    dialog.create().show()
                }

            }
            drawerLayoutMain.closeDrawers()
            return@setNavigationItemSelectedListener true
        }

    }

    fun openHomeFragment() {
        supportActionBar?.title = "Home"
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutMain, HomeFragment()).commit()
        navigationViewMain.setCheckedItem(R.id.home)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayoutMain.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        val frag = supportFragmentManager.findFragmentById(R.id.frameLayoutMain)
        when (frag) {
            is MenuFragment -> {
                if (!MenuAdapter.isCartEmpty) {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("Confirmation")
                        .setMessage("Going back will reset cart items. Do you still want to proceed?")
                        .setPositiveButton("Yes") { _, _ ->
                             val clearCart =
                               CartActivity.ClearDBAsync(applicationContext, resId.toString()).execute().get()
                            openHomeFragment()
                            MenuAdapter.isCartEmpty = true
                        }
                        .setNegativeButton("No") { _, _ ->

                        }
                        .create()
                        .show()
                }
                else
                    openHomeFragment()
            }
            !is HomeFragment -> openHomeFragment()
            else -> {
                Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                val dialog = AlertDialog.Builder(this@MainActivity)
                dialog.setTitle("Exit")
                dialog.setMessage("Are you sure you want to exit?")
                dialog.setPositiveButton("Yes") { text, listener ->
                    ActivityCompat.finishAffinity(this@MainActivity)
                }
                dialog.setNegativeButton("No") { text, listener -> }
                dialog.create().show()

            }
        }
    }
}