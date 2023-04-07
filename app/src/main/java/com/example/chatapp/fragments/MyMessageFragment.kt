package com.example.chatapp.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.databinding.FragmentMyMessageBinding
import com.example.chatapp.databinding.ItemDialogBinding
import com.example.chatapp.models.MyMessage
import com.example.chatapp.models.User
import com.example.chatapp.utils.MyData
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MyMessageFragment : Fragment(),MyMessageAdapter.RvActionMessage {
    private var toUser: User? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var imageLink: String
    lateinit var fireBaseStorage: FirebaseStorage
    lateinit var referenceStorage: StorageReference
    lateinit var list:ArrayList<MyMessage>
    lateinit var myMessageAdapter:MyMessageAdapter
    private val binding by lazy { FragmentMyMessageBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        toUser = arguments?.getSerializable("keyUser") as User
        MyData.toUser = toUser
        list = ArrayList()
        database = FirebaseDatabase.getInstance()

        fireBaseStorage = FirebaseStorage.getInstance()
        imageLink = ""
        referenceStorage = fireBaseStorage.getReference("chat_images")

        reference = database.getReference("messagesUsers")

        loadData()

        binding.btnImageSend.setOnClickListener {
            getImageContent.launch("image/*")
        }
        MyData.imagelink = imageLink
        binding.apply {
            btnSend.setOnClickListener {
                val id = reference.push().key
                val myMessage =
                    MyMessage(id, edtMessage.text.toString(), MyData.fromUser?.uid, toUser?.uid, "")
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
                 list = ArrayList<MyMessage>()
                val children = snapshot.children
                for (child in children) {
                    val myMessage = child.getValue(MyMessage::class.java)
                    if (myMessage != null) {
                        if ((myMessage.fromUid == MyData.fromUser?.uid && myMessage.toUid == toUser?.uid) || (myMessage.fromUid == toUser?.uid && myMessage.toUid == MyData.fromUser?.uid))
                            list.add(myMessage)
                    }
                }
                myMessageAdapter = MyMessageAdapter(requireContext(),list,this@MyMessageFragment)
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
        Glide.with(requireContext()).load(MyData.imagelink).into(itemDialog.image)

        dialog.setView(itemDialog.root)
        dialog.show()
    }
}
