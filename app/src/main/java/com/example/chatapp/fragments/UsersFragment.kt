package com.example.chatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.chatapp.adapters.UserAdapter
import com.google.firebase.database.*
import com.example.chatapp.databinding.FragmentUsersBinding
import com.example.chatapp.models.User
import com.example.chatapp.utils.MyData
import com.google.firebase.auth.FirebaseAuth

class UsersFragment : Fragment(),UserAdapter.RvAction {
    private val binding by lazy { FragmentUsersBinding.inflate(layoutInflater)}
    private lateinit var database:FirebaseDatabase
    private lateinit var adapter: UserAdapter
    private lateinit var reference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users")

        adapter = UserAdapter(rvAction = this)
        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<User>()
                val children = snapshot.children

                for (i in children){
                    val user = i.getValue(User::class.java)
                    if(user!=null){
                        list.add(user)
                        if (user?.uid==FirebaseAuth.getInstance().uid){
                            MyData.fromUser = user
                        }
                    }
                    if (user?.uid==FirebaseAuth.getInstance().uid){
                        user?.name = "Saved Messege"
                        user?.imageLink = "https://avatars.dzeninfra.ru/get-zen_doc/3473073/pub_5f2c67d1bd0fe709c88cab15_5f2c6801e8756b075dfc3d7e/scale_1200"
                    }
                }
                val userAdapter = UserAdapter(list,object :UserAdapter.RvAction{
                    override fun itemClick(user: User) {
                        findNavController().navigate(com.example.chatapp.R.id.myMessageFragment,
                            bundleOf("keyUser" to user))
                    }
                })
                binding.rvUsers.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        return binding.root
    }

    override fun itemClick(user: User) {
    }
}