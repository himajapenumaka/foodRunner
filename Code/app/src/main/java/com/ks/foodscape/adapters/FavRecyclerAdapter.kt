package com.ks.foodscape.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ks.foodscape.R
import com.ks.foodscape.database.RestaurantEntity
import com.squareup.picasso.Picasso

class FavRecyclerAdapter(val context: Context, val itemList: List<RestaurantEntity>): RecyclerView.Adapter<FavRecyclerAdapter.FavViewHolder>() {

    class FavViewHolder(view: View): RecyclerView.ViewHolder(view){

        val linearLayout: LinearLayout =view.findViewById(R.id.linearLayoutHome)
        val imgRestaurant: ImageView =view.findViewById(R.id.imgRestaurantHome)
        val txtResName: TextView =view.findViewById(R.id.ResNameHome)
        val txtFoodPrice: TextView =view.findViewById(R.id.txtFoodPriceHome)
        val txtFoodRating: TextView =view.findViewById(R.id.txtFoodRating)
        val BtnfavHome: Button=view.findViewById(R.id.BtnfavHome)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_single_row,parent,false)
        return FavRecyclerAdapter.FavViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val i=itemList[position]
        holder.txtResName.text=i.resName
        holder.txtFoodPrice.text="Rs. ${i.resPrice}/person"
        holder.txtFoodRating.text=i.resRating
        Picasso.get().load(i.resImg).error(R.drawable.default_food_image).into(holder.imgRestaurant)
        holder.BtnfavHome.setBackgroundResource(R.drawable.ic_fav_on)

        holder.linearLayout.setOnClickListener {
            Toast.makeText(context,"Clicked on ${i.resName}",Toast.LENGTH_SHORT).show()
        }

        val restaurantEntity=RestaurantEntity(
            i.resId,
            i.resName,
            i.resRating,
            i.resPrice,
            i.resPrice
        )

        holder.BtnfavHome.setOnClickListener {
            if (!HomeRecyclerAdapter.DbAsyncTask(context, restaurantEntity, 1).execute().get()) {

                val result =
                    HomeRecyclerAdapter.DbAsyncTask(context, restaurantEntity, 2).execute().get()
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

                val result =
                    HomeRecyclerAdapter.DbAsyncTask(context, restaurantEntity, 3).execute().get()
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

    }

}