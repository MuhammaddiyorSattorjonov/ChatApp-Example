package com.example.chatapp.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.databinding.ItemRvUsersBinding
import com.example.chatapp.models.Group
import com.example.chatapp.models.User

class GroupsAdapter(val context:Context,val list:ArrayList<Group> = ArrayList(), val rvAction:RvAction): RecyclerView.Adapter<GroupsAdapter.Vh>() {

    inner class Vh(val itemRvBinding:ItemRvUsersBinding): RecyclerView.ViewHolder(itemRvBinding.root){
        fun onBind(group: Group,position: Int){
            itemRvBinding.tvTxt.text = group.name
            itemRvBinding.itemImage.setImageBitmap(BitmapFactory.decodeFile(group.image))
            Glide.with(context).load(group.image).into(itemRvBinding.itemImage)
            itemRvBinding.root.setOnClickListener {
                rvAction.itemClick(group)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvUsersBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position],position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface RvAction{
        fun itemClick(group: Group)
    }
}