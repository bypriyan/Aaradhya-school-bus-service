package com.bypriyan.sharemarketcourseinhindi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bypriyan.sharemarketcourseinhindi.model.ModelOnBordingScreen
import com.google.android.material.card.MaterialCardView
import com.bypriyan.aaradhyaschoolbusservice.R

class AdapterOnBordingScreen(val context: Context,val elementList: List<ModelOnBordingScreen>) :
    RecyclerView.Adapter<AdapterOnBordingScreen.HolderOnBordingScreen>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderOnBordingScreen {
        val view = LayoutInflater.from(context).inflate(R.layout.row_onbording_page, parent, false)
        return HolderOnBordingScreen(view)
    }

    override fun onBindViewHolder(holder: HolderOnBordingScreen, position: Int) {
        var modelOnBordingSceen = elementList[position]

        holder.title.text = modelOnBordingSceen.title
        holder.description.text = modelOnBordingSceen.description

        holder.imgOnBording.imageTintList = null

        holder.imgOnBording.load(modelOnBordingSceen.imgUrl) {
            crossfade(true)
        }

    }

    override fun getItemCount(): Int {
        return elementList.size
    }

    inner class HolderOnBordingScreen(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imgOnBording: ImageView = itemView.findViewById(R.id.imgOnBording)
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
    }

}
