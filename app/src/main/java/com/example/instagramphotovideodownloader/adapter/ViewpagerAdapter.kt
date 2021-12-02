package com.example.instagramphotovideodownloader.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.instagramphotovideodownloader.fragments.PhotoFragment
import com.example.instagramphotovideodownloader.fragments.ProfileFragment
import com.example.instagramphotovideodownloader.fragments.ReelFragment
import com.example.instagramphotovideodownloader.fragments.VideoFragment

class ViewpagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 4
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                PhotoFragment()
            }
            1 -> {
                ReelFragment()
            }
            2 -> {
                VideoFragment()
            }
            3 -> {
                ProfileFragment()
            }

            else -> {
                PhotoFragment()
            }
        }

    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return "Photo"
            }
            1 -> {
                return "Reel"
            }
            2 -> {
                return "Video"
            }
            3 -> {
                return "Profile"
            }

        }

        return super.getPageTitle(position)
    }
}