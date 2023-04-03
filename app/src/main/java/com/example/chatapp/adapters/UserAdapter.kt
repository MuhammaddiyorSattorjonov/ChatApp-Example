package com.example.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.databinding.ItemRvUsersBinding
import com.example.chatapp.models.User

class UserAdapter(val list:ArrayList<User> = ArrayList(), val rvAction: RvAction): RecyclerView.Adapter<UserAdapter.Vh>() {

    inner class Vh(val itemRvBinding:ItemRvUsersBinding): RecyclerView.ViewHolder(itemRvBinding.root){
        fun onBind(user: User,position: Int){
            itemRvBinding.tvTxt.text = user.name
            Glide.with(itemView.context).load(user.imageLink).into(itemRvBinding.itemImage)
            itemRvBinding.root.setOnClickListener {
                rvAction.itemClick(user)
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
        fun itemClick(user: User)
    }
}