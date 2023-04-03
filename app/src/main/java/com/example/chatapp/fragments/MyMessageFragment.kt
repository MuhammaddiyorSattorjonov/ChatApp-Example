package com.example.chatapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.chatapp.R
import com.example.chatapp.adapters.MyMessageAdapter
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.databinding.FragmentMyMessageBinding
import com.example.chatapp.models.MyMessage
import com.example.chatapp.models.User
import com.example.chatapp.utils.MyData
import com.google.firebase.database.*


class MyMessageFragment : Fragment() {
    private var toUser:User? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private val binding by lazy { FragmentMyMessageBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        toUser = arguments?.getSerializable("keyUser") as User
        MyData.toUser = toUser
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("messagesUsers")

        loadData()

        binding.apply {
            btnSend.setOnClickListener{
                val id = reference.push().key
                val myMessage = MyMessage(id,edtMessage.text.toString(),MyData.fromUser?.uid,toUser?.uid)
                reference.child(id!!).setValue(myMessage)
                Toast.makeText(context, "Send", Toast.LENGTH_SHORT).show()
                edtMessage.text.clear()
            }
        }
        return binding.root
    }

    private fun loadData() {
        reference.addValueEventListener(object :ValueEventListener{
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<MyMessage>()
                val children = snapshot.children
                for (child in children) {
                    val myMessage = child.getValue(MyMessage::class.java)
                    if (myMessage!= null){
                        if ((myMessage.fromUid == MyData.fromUser?.uid && myMessage.toUid == toUser?.uid)||(myMessage.fromUid == toUser?.uid && myMessage.toUid == MyData.fromUser?.uid))
                        list.add(myMessage)
                    }
                }
                val myMessageAdapter = MyMessageAdapter(list)
                binding.rvMessage.scrollToPosition(list.size - 1)
                binding.rvMessage.adapter = myMessageAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}