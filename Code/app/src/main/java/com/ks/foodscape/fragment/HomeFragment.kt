package com.ks.foodscape.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ks.foodscape.R
import com.ks.foodscape.adapters.HomeRecyclerAdapter
import com.ks.foodscape.model.Restaurant
import com.ks.foodscape.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayoutHome: RelativeLayout
    lateinit var progressBarHome: ProgressBar
    lateinit var recyclerHomeAdapter: HomeRecyclerAdapter

    val RestaurantInfoList = arrayListOf<Restaurant>()

    var ratingComparator1 = Comparator<Restaurant> { res1, res2 ->
        res1.name.compareTo(res2.name, true)
    }

    var ratingComparator2 = Comparator<Restaurant> { res1, res2 ->
        if (res1.cost_for_one.compareTo(res2.cost_for_one, true) == 0)
            res1.name.compareTo(res2.name, true)
        else
            res1.cost_for_one.compareTo(res2.cost_for_one, true)
    }

    var ratingComparator3 = Comparator<Restaurant> { res1, res2 ->
        if (res1.rating.compareTo(res2.rating, true) == 0)
            res1.name.compareTo(res2.name, true)
        else
            res1.rating.compareTo(res2.rating, true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        (context as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        (context as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        progressBarHome = view.findViewById(R.id.progressBarHome)
        progressLayoutHome = view.findViewById(R.id.progressLayoutHome)
        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)

        progressLayoutHome.visibility = View.VISIBLE
        progressBarHome.visibility = View.VISIBLE

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        val queue = Volley.newRequestQueue(activity as Context)

        if (ConnectionManager().checkConnectivity(activity as Context)) {


            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener {
                    try {
                        val d = it.getJSONObject("data")
                        val success = d.getBoolean("success")
                        if (success) {
                            progressLayoutHome.visibility = View.GONE
                            progressBarHome.visibility = View.GONE
                            val data = d.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val jsonRestaurantObject = data.getJSONObject(i)
                                val RestaurantObject = Restaurant(
                                    jsonRestaurantObject.getString("id"),
                                    jsonRestaurantObject.getString("name"),
                                    jsonRestaurantObject.getString("rating"),
                                    jsonRestaurantObject.getString("cost_for_one"),
                                    jsonRestaurantObject.getString("image_url")
                                )

                                RestaurantInfoList.add(RestaurantObject)
                                recyclerHomeAdapter =
                                    HomeRecyclerAdapter(activity as Context, RestaurantInfoList)
                                recyclerHome.layoutManager = layoutManager
                                recyclerHome.adapter = recyclerHomeAdapter
                            }


                        } else {
                            if (activity != null)
                                Toast.makeText(
                                    activity as Context,
                                    "Some error has occurred ",
                                    Toast.LENGTH_LONG
                                ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some exception occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                Response.ErrorListener {

                    if (activity != null)
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred",
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

        } else {

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error!")
            dialog.setMessage("Internet Connection not found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create().show()

        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId

        when (id) {
            R.id.sort_az -> {
                Collections.sort(RestaurantInfoList, ratingComparator1)
            }
            R.id.sort_za -> {
                Collections.sort(RestaurantInfoList, ratingComparator1)
                RestaurantInfoList.reverse()
            }
            R.id.sort_hl -> {
                Collections.sort(RestaurantInfoList, ratingComparator2)
                RestaurantInfoList.reverse()
            }
            R.id.sort_lh -> {
                Collections.sort(RestaurantInfoList, ratingComparator2)
            }
            R.id.sort_rating -> {
                Collections.sort(RestaurantInfoList, ratingComparator3)
                RestaurantInfoList.reverse()
            }

        }
        recyclerHomeAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)

    }

}
