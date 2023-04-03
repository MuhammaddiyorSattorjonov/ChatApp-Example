package com.example.chatapp.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ItemRvUsersBinding
import com.example.chatapp.models.Group
import com.example.chatapp.models.User

class GroupsAdapter(val list:ArrayList<Group> = ArrayList(), val rvAction:RvAction): RecyclerView.Adapter<GroupsAdapter.Vh>() {

    inner class Vh(val itemRvBinding:ItemRvUsersBinding): RecyclerView.ViewHolder(itemRvBinding.root){
        fun onBind(group: Group,position: Int){
            itemRvBinding.tvTxt.text = group.name
            itemRvBinding.itemImage.setImageBitmap(BitmapFactory.decodeFile(group.image))
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