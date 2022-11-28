package com.example.adsdesk.frag

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.adsdesk.R
import com.example.adsdesk.act.EditAdsAct
import com.example.adsdesk.databinding.SelectImageFragItemBinding
import com.example.adsdesk.utils.AdapterCallback
import com.example.adsdesk.utils.ImageManager
import com.example.adsdesk.utils.ImagePicker
import com.example.adsdesk.utils.ItemTouchMoveCallback

class SelectImageRvAdapter(val adapterCallback: AdapterCallback): RecyclerView.Adapter<SelectImageRvAdapter.ImageHolder>(), ItemTouchMoveCallback.ItemTouchAdapter {

    val mainArray = ArrayList<Bitmap>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_frag_item,parent,false)
        val binding = SelectImageFragItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ImageHolder(binding, parent.context, this)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(mainArray[position])
    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

    override fun onMove(startPos: Int, targetPos: Int) {
        val targetItem = mainArray[targetPos]
        mainArray[targetPos] = mainArray[startPos]
        mainArray[startPos] = targetItem
        notifyItemMoved(startPos, targetPos)
    }

    override fun onClear() {
        notifyDataSetChanged()
    }

    class ImageHolder(private val binding: SelectImageFragItemBinding, val context: Context, val adapter: SelectImageRvAdapter) : RecyclerView.ViewHolder(binding.root) {

        fun setData(bitmap: Bitmap){
            binding.imEditImage.setOnClickListener{
                ImagePicker.launchSingleSelectImage(context as EditAdsAct, R.id.placeHolder)
                context.editImagePos = adapterPosition
            }
            binding.imDelete.setOnClickListener{
                adapter.mainArray.removeAt(adapterPosition)
                adapter.notifyItemRemoved(adapterPosition)
                for (i in 0 until adapter.mainArray.size) adapter.notifyItemChanged(i)
                adapter.adapterCallback.onItemDelete()
            }
            binding.tvTitle.text = context.resources.getStringArray(R.array.title_array)[adapterPosition]
            ImageManager.chooseScaleType(binding.imageContent, bitmap)
            binding.imageContent.setImageBitmap(bitmap)
        }
    }
    fun updateAdapter(newList: List<Bitmap>, needToClear: Boolean){
        if (needToClear == true){
            mainArray.clear()
        }
        mainArray.addAll(newList)
        notifyDataSetChanged()
    }



}