package com.example.chatapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.adapters.MyMessageAdapter
import com.example.chatapp.databinding.FragmentMessagesGroupsBinding
import com.example.chatapp.databinding.FragmentMyMessageBinding
import com.example.chatapp.databinding.ItemDialogBinding
import com.example.chatapp.models.Group
import com.example.chatapp.models.MyMessage
import com.example.chatapp.models.User
import com.example.chatapp.utils.MyData
import com.example.chatapp.utils.MyData.toUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MessagesGroups : Fragment(),MyMessageAdapter.RvActionMessage{
    private val binding by lazy { FragmentMessagesGroupsBinding.inflate(layoutInflater) }
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var group: Group
    private lateinit var imageLink: String
    lateinit var fireBaseStorage: FirebaseStorage
    lateinit var referenceStorage: StorageReference
    lateinit var list:ArrayList<MyMessage>
    lateinit var myMessageAdapter:MyMessageAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        group = arguments?.getSerializable("keyGroup") as Group
        database = FirebaseDatabase.getInstance()
        list = ArrayList()
        myMessageAdapter = MyMessageAdapter(requireContext(),list,this)
        reference = database.getReference("messagesGroups").child(MyData.group)

        fireBaseStorage = FirebaseStorage.getInstance()
        imageLink = ""
        referenceStorage = fireBaseStorage.getReference("group_images")

        binding.btnImageSend.setOnClickListener {
            getImageContent.launch("image/*")
        }
        MyData.groupImage = imageLink
        loadData()
        binding.apply {
            btnSend.setOnClickListener {
                val id = reference.push().key
                val myMessage = MyMessage(id,
                    edtMessage.text.toString(),
                    MyData.fromUser?.uid,
                    MyData.toUser?.uid, "")
                reference.child(id!!).setValue(myMessage)
                Toast.makeText(context, "Send", Toast.LENGTH_SHORT).show()
                edtMessage.text.clear()
            }
        }

        return binding.root
    }

    private fun loadData() {
        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<MyMessage>()
                val children = snapshot.children
                for (child in children) {
                    val myMessage = child.getValue(MyMessage::class.java)
                    if (myMessage != null) {
                        list.add(myMessage)
                    }
                }
                val myMessageAdapter = MyMessageAdapter(requireContext(),list,this@MessagesGroups)
                binding.rvMessage.scrollToPosition(list.size - 1)
                binding.rvMessage.adapter = myMessageAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private val getImageContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val simple = SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(Date())
            if (uri != null) {
                val task = referenceStorage.child(simple).putFile(uri)
                binding.progress.visibility = View.VISIBLE
                task.addOnSuccessListener {
                    binding.progress.visibility = View.GONE
                    it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                        println(it)
                        imageLink = it.toString()
                        val id = reference.push().key
                        val myMessage =
                            MyMessage(id, "", MyData.fromUser?.uid, toUser?.uid, imageLink)
                        reference.child(id!!).setValue(myMessage)
                        Toast.makeText(context, "Send", Toast.LENGTH_SHORT).show()

                        binding.rvMessage.scrollToPosition(list.size - 1)
                        binding.rvMessage.adapter = myMessageAdapter
                    }
                }
                task.addOnFailureListener {
                    binding.progress.visibility = View.GONE
                    Toast.makeText(requireContext(), "Yuklab bo'lmadi", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(requireContext(), "Rasm tanlanmadi", Toast.LENGTH_SHORT).show()
            }

        }

    override fun clickImage(image: ImageView) {
        val dialog = AlertDialog.Builder(requireContext()).create()
        val itemDialog = ItemDialogBinding.inflate(layoutInflater)

        dialog.setCancelable(false)
        itemDialog.back.setOnClickListener {
            dialog.cancel()
        }
        Glide.with(requireContext()).load(MyData.groupImage).into(itemDialog.image)

        dialog.setView(itemDialog.root)
        dialog.show()
    }
    }
