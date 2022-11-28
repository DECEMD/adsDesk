package com.example.adsdesk.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.adsdesk.MainActivity
import com.example.adsdesk.R
import com.example.adsdesk.act.DescriptionActivity
import com.example.adsdesk.act.EditAdsAct
import com.example.adsdesk.model.Ad
import com.example.adsdesk.databinding.AdListItemBinding
import com.example.adsdesk.dialogHelper.OnDeleteAdFromMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class AdRcAdapter(val context: MainActivity): RecyclerView.Adapter<AdRcAdapter.AdHolder>() {
    val adArray = ArrayList<Ad>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdHolder(binding, context)
    }

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        holder.setData(adArray[position])
    }

    override fun getItemCount(): Int {
        return adArray.size
    }

    fun updateAdapter(listToAdArray: List<Ad>){
        val tempArray = ArrayList<Ad>()
        tempArray.addAll(adArray)
        tempArray.addAll(listToAdArray)

        val diffResult = DiffUtil.calculateDiff(DiffUtilHelper(adArray, tempArray))
        diffResult.dispatchUpdatesTo(this)
        adArray.clear()
        adArray.addAll(tempArray)

    }

    fun updateAdapterWithClear(listToAdArray: List<Ad>){
        val diffResult = DiffUtil.calculateDiff(DiffUtilHelper(adArray, listToAdArray))
        diffResult.dispatchUpdatesTo(this)
        adArray.clear()
        adArray.addAll(listToAdArray)

    }

    class AdHolder(val binding: AdListItemBinding, val context: MainActivity) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(ad: Ad) = with(binding) {
            tvDescription.text = ad.description
            tvPrice.text = ad.price
            tvTitle.text = ad.title
            tvViewCounter.text = ad.viewsCounter
            tvFavCounter.text = ad.favCounter
            Picasso.get().load(ad.mainImage).into(mainImage)

            isFav(ad)
            showEditPanel(isOwner(ad))
            mainOnClick(ad)

        }

        private fun mainOnClick(ad: Ad) = with(binding){
            ibEditAd.setOnClickListener(onClickEdit(ad))
            ibFav.setOnClickListener {
                if (context.myAuth.currentUser?.isAnonymous == false) context.onFavClicked(ad)
            }

            ibDeleteAd.setOnClickListener{
                context.onDeleteItem(ad)
            }

            itemView.setOnClickListener {
                context.onAdViewed(ad)
            }
        }

        private fun isFav(ad: Ad){
            if (ad.isFav){
                binding.ibFav.setImageResource(R.drawable.ic_fav_pressed)
            } else {
                binding.ibFav.setImageResource(R.drawable.ic_fav_normal)
            }
        }

        private fun onClickEdit(ad: Ad): View.OnClickListener{
            return View.OnClickListener {
                val editIntent = Intent(context,EditAdsAct::class.java).apply {
                    putExtra(MainActivity.EDIT_STATE, true)
                    putExtra(MainActivity.ADS_DATA, ad)
                }
                context.startActivity(editIntent)
            }
        }

        private fun isOwner(ad: Ad): Boolean{
            return ad.uid == context.myAuth.uid
        }

        private fun showEditPanel(isOwnerEditPanel: Boolean){
            if(isOwnerEditPanel){
                binding.editPanel.visibility = View.VISIBLE
            } else {
                binding.editPanel.visibility = View.GONE
            }
        }
    }
    interface ItemListenerToOperate{
        fun onDeleteItem(ad: Ad)
        fun onAdViewed(ad: Ad)
        fun onFavClicked(ad: Ad)
    }
}