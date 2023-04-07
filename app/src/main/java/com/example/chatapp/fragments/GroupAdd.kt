package com.example.chatapp.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentGroupAddBinding
import com.example.chatapp.models.Group
import com.example.chatapp.models.MyMessage
import com.example.chatapp.utils.MyData
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class GroupAdd : Fragment() {
    private val binding by lazy { FragmentGroupAddBinding.inflate(layoutInflater) }
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    lateinit var imageLink:String
    lateinit var fireBaseStorage: FirebaseStorage
    lateinit var referenceStorage: StorageReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("groups")

        imageLink = ""
        fireBaseStorage = FirebaseStorage.getInstance()
        referenceStorage = fireBaseStorage.getReference("group_profile_images")


        binding.imageProfile.setOnClickListener {
            getImageContent.launch("image/*")
        }


        binding.btnSave.setOnClickListener {
            addNewGroup(binding.name.text.toString())
        }

        return binding.root
    }
    private fun addNewGroup(name:String){
        val key = reference.push().key
        val group = Group()
        group.id = key
        group.name = name
        group.listMessages = ArrayList<MyMessage>().toString()
        group.image = imageLink
        Toast.makeText(context, "S&7a7", Toast.LENGTH_SHORT).show()
        reference.child(key!!).setValue(group)
        findNavController().navigate(
            R.id.viewPager,
            null,
            NavOptions.Builder()
                .setPopUpTo(findNavController().currentDestination?.id ?: 0, true).build()
        )
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
                        Toast.makeText(context, "Qo'yildi", Toast.LENGTH_SHORT).show()
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
}