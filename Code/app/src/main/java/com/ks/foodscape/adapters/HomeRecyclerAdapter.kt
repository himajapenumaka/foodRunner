package com.ks.foodscape.adapters

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ks.foodscape.R
import com.ks.foodscape.database.RestaurantDatabase
import com.ks.foodscape.database.RestaurantEntity
import com.ks.foodscape.fragment.MenuFragment
import com.ks.foodscape.model.Restaurant
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context,val itemList: ArrayList<Restaurant>): RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View): RecyclerView.ViewHolder(view){

        val linearLayoutHome: LinearLayout=view.findViewById(R.id.linearLayoutHome)
        val imgRestaurantHome: ImageView=view.findViewById(R.id.imgRestaurantHome)
        val txtResNameHome: TextView=view.findViewById(R.id.ResNameHome)
        val txtFoodPriceHome: TextView=view.findViewById(R.id.txtFoodPriceHome)
        val txtFoodRating: TextView=view.findViewById(R.id.txtFoodRating)
        val BtnfavHome: Button=view.findViewById(R.id.BtnfavHome)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_single_row,parent,false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

        val i = itemList[position]
        holder.txtResNameHome.text = i.name
        holder.txtFoodPriceHome.text = "Rs. ${i.cost_for_one}/person"
        holder.txtFoodRating.text = i.rating
        Picasso.get().load(i.image).error(R.drawable.default_food_image)
            .into(holder.imgRestaurantHome)

        val restaurantEntity = RestaurantEntity(
            i.res_id,
            i.name,
            i.rating,
            i.cost_for_one,
            i.image
        )

        val isFav = DbAsyncTask(context, restaurantEntity, 1).execute().get()
        if (isFav)
            holder.BtnfavHome.setBackgroundResource(R.drawable.ic_fav_on)
        else
            holder.BtnfavHome.setBackgroundResource(R.drawable.ic_fav_off)

        holder.BtnfavHome.setOnClickListener {
            if (!DbAsyncTask(context, restaurantEntity, 1).execute().get()) {

                val result = DbAsyncTask(context, restaurantEntity, 2).execute().get()
                if (result) {
                    Toast.makeText(
                        context, "Restaurant added to favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.BtnfavHome.setBackgroundResource(R.drawable.ic_fav_on)
                } else
                    Toast.makeText(
                        context, "Cannot add. Some error occurred",
                        Toast.LENGTH_SHORT
                    ).show()

            } else {

                val result = DbAsyncTask(context, restaurantEntity, 3).execute().get()
                if (result) {
                    Toast.makeText(
                        context, "Restaurant removed from favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.BtnfavHome.setBackgroundResource(R.drawable.ic_fav_off)
                } else
                    Toast.makeText(
                        context, "Cannot remove. Some error occurred",
                        Toast.LENGTH_SHORT
                    ).show()

            }
        }

        holder.linearLayoutHome.setOnClickListener {
            val fragment = MenuFragment()
            val args = Bundle()
            args.putInt("id", i.res_id.toInt())
            args.putString("name", i.name)
            fragment.arguments = args
            val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayoutMain, fragment)
            transaction.commit()
            (context as AppCompatActivity).supportActionBar?.title = holder.txtResNameHome.text.toString()
            (context as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(false)
            (context as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

    }

    class DbAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {

                1 -> {
                    //checking if the restaurant is already in favourites or not

                    val book: RestaurantEntity? =
                        db.restaurantDao().getResById(restaurantEntity.resId)
                    db.close()
                    return book != null
                }

                2 -> {
                    //inserting the restaurant into table

                    db.restaurantDao().insertRes(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    //deleting the restaurant from table

                    db.restaurantDao().deleteRes(restaurantEntity)
                    db.close()
                    return true
                }

            }

            return false
        }

    }

}