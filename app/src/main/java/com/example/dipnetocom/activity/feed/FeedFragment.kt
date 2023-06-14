//package com.example.dipnetocom.activity.feed
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.example.dipnetocom.R
//import com.example.dipnetocom.databinding.FragmentFeedBinding
//import com.example.dipnetocom.databinding.FragmentPostBinding
//
//class FeedFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val binding = FragmentFeedBinding.inflate(inflater, container, false)
//
//        loadFragment(PostFragment())
//        binding.bottomNav.menu.findItem(R.id.posts_menu).isChecked = true
//
//        binding.bottomNav.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.posts_menu -> {
//                    loadFragment(PostFragment())
//                    true
//                }
//                R.id.events_menu -> {
//                    loadFragment(EventFragment())
//                    true
//                }
//                else -> false
//            }
//        }
//
//        return binding.root
//    }
//
//    private  fun loadFragment(fragment: Fragment){
//        val transaction = childFragmentManager.beginTransaction()
//        transaction.replace(R.id.container,fragment)
//        transaction.commit()
//    }
//
//
//}