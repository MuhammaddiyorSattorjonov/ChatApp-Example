package com.example.chatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.adapters.GroupsAdapter
import com.example.chatapp.adapters.UserAdapter
import com.example.chatapp.databinding.FragmentGroupsBinding
import com.example.chatapp.models.Group
import com.example.chatapp.models.User
import com.example.chatapp.utils.MyData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GroupsFragment : Fragment(), GroupsAdapter.RvAction {
    private val binding by lazy { FragmentGroupsBinding.inflate(layoutInflater) }
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var adapter: GroupsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("groups")
        adapter = GroupsAdapter(requireContext(),rvAction = this)

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.groupAdd)
        }

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<Group>()
                val children = snapshot.children

                for (i in children) {
                    val group = i.getValue(Group::class.java)
                    if (group != null) {
                        list.add(group)
                    }
                }
                val userAdapter = GroupsAdapter(requireContext(),list, object : GroupsAdapter.RvAction {
                    override fun itemClick(group: Group) {
                        MyData.group  = group.name!!
                        findNavController().navigate(R.id.messagesGroups, bundleOf("keyGroup" to group))
                    }
                })
                binding.rv.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        return binding.root
    }

    override fun itemClick(group: Group) {
        findNavController().navigate(R.id.messagesGroups)
    }
}