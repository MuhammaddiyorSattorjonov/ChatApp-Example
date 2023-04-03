package com.example.chatapp.adapters

import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chatapp.databinding.ItemFromBinding
import com.example.chatapp.databinding.ItemToBinding
import com.example.chatapp.models.MyMessage
import com.example.chatapp.utils.MyData

class MyMessageAdapter(val list:ArrayList<MyMessage>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class FromVh(var itemFromBinding: ItemFromBinding): RecyclerView.ViewHolder(itemFromBinding.root){
        fun onBind(myMessage: MyMessage,position: Int){
            itemFromBinding.tvMessage.text = myMessage.text
            itemFromBinding.tvDate.text = myMessage.date
        }
    }
    inner class ToVh(var itemToBinding: ItemToBinding): RecyclerView.ViewHolder(itemToBinding.root){
        fun onBind(myMessage: MyMessage,position: Int){
            itemToBinding.tvMessage.text = myMessage.text
            itemToBinding.tvDate.text = myMessage.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType==0){
            return FromVh(ItemFromBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }else{
            return ToVh(ItemToBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == 0){
            (holder as FromVh).onBind(list[position],position)
        }else{
            (holder as ToVh).onBind(list[position],position)

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        if (list[position].fromUid == MyData.fromUser?.uid){
            return 0
        }else{
            return 1
        }
    }
}