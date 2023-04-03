package com.example.chatapp.fragments

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.adapters.ViewPagerAdapter
import com.example.chatapp.databinding.FragmentViewPagerBinding
import com.example.chatapp.models.User
import com.example.chatapp.utils.MyData
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.firebase.auth.FirebaseAuth


class ViewPager : Fragment() {
    private val binding by lazy { FragmentViewPagerBinding.inflate(layoutInflater) }
    lateinit var viewPagerAdapter: ViewPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val users = UsersFragment()
        val groups = GroupsFragment()
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        askPermision1()
        viewPagerAdapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        viewPagerAdapter.addFragment(users, "Users")
        viewPagerAdapter.addFragment(groups, "Groups")

        binding.menuBar.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        val head = binding.navView.getHeaderView(0)
        head.rootView.findViewById<TextView>(R.id.header_name).text = FirebaseAuth.getInstance().currentUser?.displayName
        Glide.with(head.rootView.context).load(FirebaseAuth.getInstance().currentUser?.photoUrl)
            .into(head.rootView.findViewById<ImageView>(R.id.header_image))

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.saved_messages -> {}
                R.id.settings -> {}
                R.id.log_out -> {
                    FirebaseAuth.getInstance().signOut()
                    findNavController().navigate(R.id.authFragment)
                }
            }

            true
        }


        binding.viewPager.adapter = viewPagerAdapter
        return binding.root
    }
    fun askPermision1() {
        askPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) {

        }.onDeclined { e ->
            if (e.hasDenied()) {

                AlertDialog.Builder(requireContext())
                    .setMessage("Ruxsat bermasangiz ilova ishlay olmaydi ruxsat bering...")
                    .setPositiveButton("Xa") { dialog, which ->
                        e.askAgain();
                    } //ask again
                    .setNegativeButton("Yo'q") { dialog, which ->
                        dialog.dismiss();
                    }
                    .show();
            }
        }
    }
}
