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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentGroupAddBinding
import com.example.chatapp.models.Group
import com.example.chatapp.models.MyMessage
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class GroupAdd : Fragment() {
    private val binding by lazy { FragmentGroupAddBinding.inflate(layoutInflater) }
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private var picturePath:String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("groups")

        binding.imageProfile.setOnClickListener {
            choosePhotoFromGallary()
        }

        binding.btnSave.setOnClickListener {
            addNewGroup(binding.name.text.toString())
        }

        return binding.root
    }
    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, 1)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = requireActivity().contentResolver.query(
                selectedImage!!,
                filePathColumn,
                null,
                null,
                null
            )
            cursor!!.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            picturePath = cursor.getString(columnIndex)
            cursor.close()
            binding.imageProfile.setImageBitmap(BitmapFactory.decodeFile(picturePath))
            Toast.makeText(context, picturePath, Toast.LENGTH_SHORT).show()
        }
    }
    fun addNewGroup(name:String){
        val key = reference.push().key
        val group = Group()
        group.id = key
        group.name = name
        group.listMessages = ArrayList<MyMessage>().toString()
        group.image = picturePath


        reference.child(key!!).setValue(group)
        findNavController().navigate(
            R.id.viewPager,
            null,
            NavOptions.Builder()
                .setPopUpTo(findNavController().currentDestination?.id ?: 0, true).build()
        )
    }
}