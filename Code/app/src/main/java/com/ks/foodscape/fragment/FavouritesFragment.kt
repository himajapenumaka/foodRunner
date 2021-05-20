package com.ks.foodscape.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ks.foodscape.R
import com.ks.foodscape.adapters.FavRecyclerAdapter
import com.ks.foodscape.database.RestaurantDatabase
import com.ks.foodscape.database.RestaurantEntity


class FavouritesFragment : Fragment() {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerFav: RecyclerView
    lateinit var progressLayoutFav: RelativeLayout
    lateinit var progressBarFav: ProgressBar
    lateinit var favAdapter: FavRecyclerAdapter
    var resList= listOf<RestaurantEntity>()
    lateinit var rlNoFavorites: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        layoutManager = LinearLayoutManager(activity)
        recyclerFav = view.findViewById(R.id.recyclerFav)
        progressBarFav = view.findViewById(R.id.progressBarFav)
        progressLayoutFav = view.findViewById(R.id.progressLayoutFav)
        progressLayoutFav.visibility = View.VISIBLE
        progressBarFav.visibility = View.VISIBLE
        rlNoFavorites=view.findViewById(R.id.rlNoFavorites)

        resList= RetrieveFav(activity as Context).execute().get()

        if(resList.isEmpty()){

            progressLayoutFav.visibility=View.GONE
            rlNoFavorites.visibility=View.VISIBLE

        }

        if (activity != null) {

            progressLayoutFav.visibility=View.GONE
            favAdapter=FavRecyclerAdapter(activity as Context,resList)
            recyclerFav.adapter=favAdapter
            recyclerFav.layoutManager=layoutManager

        }

        return view
    }

    class RetrieveFav(val context: Context) : AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()
            return db.restaurantDao().getAllRes()
        }

    }

}
