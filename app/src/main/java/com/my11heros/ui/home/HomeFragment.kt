package com.my11heros.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.my11heros.MainActivity
import com.my11heros.R
import com.my11heros.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var adapter: ViewPagerAdapter
    private var mBinding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home, container, false
        )
        return mBinding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showToolbar()
        setupViewPager(mBinding!!.viewpager)
        mBinding!!.tabs.setupWithViewPager(mBinding!!.viewpager)
    }


    private fun setupViewPager(viewPager: ViewPager) {
         adapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        adapter.addFragment(FixtureCricketFragment(),getString(R.string.game_type_cricket))
        adapter.addFragment(FixtureFootBallFragment(), getString(R.string.game_type_football))
//        adapter.addFragment(FixtureBasketballFragment(), getString(R.string.game_type_basketball))
//        adapter.addFragment(FixtureHockeyFragment(), getString(R.string.game_type_hockey))
//        adapter.addFragment(FixtureVolleyBallFragment(), getString(R.string.game_type_volleyball))
        viewPager.adapter = adapter
    }

//    fun onRefresh(){
//
//        var cricketFragment = adapter.getItem(mBinding!!.viewpager.getCurrentItem()) as FixtureCricketFragment
//        cricketFragment.getAllMatches()
//
//
//    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager,FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }



}