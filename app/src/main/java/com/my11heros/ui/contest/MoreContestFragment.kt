package com.my11heros.ui.contest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edify.atrist.listener.OnContestEvents
import com.edify.atrist.listener.OnContestLoadedListener
import com.my11heros.ContestActivity
import com.my11heros.LeadersBoardActivity
import com.my11heros.MoreContestActivity
import com.my11heros.R
import com.my11heros.databinding.FragmentMoreContestBinding
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.ui.contest.adaptors.ContestListAdapter
import com.my11heros.ui.contest.models.ContestModelLists
import com.my11heros.utils.CustomeProgressDialog


class MoreContestFragment : Fragment() {
    var mListenerContestEvents: OnContestLoadedListener? = null
    var mListener: OnContestLoadedListener? = null

    private lateinit var contestList: java.util.ArrayList<ContestModelLists>
    var objectMatches: UpcomingMatchesModel? = null
    private lateinit var customeProgressDialog: CustomeProgressDialog
    private var mBinding: FragmentMoreContestBinding? = null
    lateinit var adapter: ContestListAdapter

    companion object {
        const val CONTEST_LIST: String = "contestlist"
        var TAG: String = MoreContestFragment::class.java.simpleName

        fun newInstance(bundle: Bundle): MoreContestFragment {
            val fragment = MoreContestFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate")

        objectMatches =
            requireArguments().get(ContestActivity.SERIALIZABLE_KEY_MATCH_OBJECT) as UpcomingMatchesModel
        contestList = requireArguments().get(CONTEST_LIST) as ArrayList<ContestModelLists>

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e(TAG, "onAttach")

        if (context is OnContestLoadedListener) {
            mListener = context
        } else {
            throw RuntimeException(
                "$context must implement OnContestLoadedListener"
            )
        }

        if (context is OnContestEvents) {
            mListenerContestEvents = context
        } else {
            throw RuntimeException(
                "$context must implement OnContestLoadedListener"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_more_contest, container, false
        )
        Log.e(TAG, "onCreateView")

        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated")

        customeProgressDialog = CustomeProgressDialog(activity)

        mBinding!!.recyclerMyContest.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        val colorCode = requireActivity().resources.getColor(R.color.white)

        adapter = ContestListAdapter(
            requireActivity(),
            contestList,
            objectMatches!!,
            mListenerContestEvents as OnContestEvents,
            colorCode
        )
        mBinding!!.recyclerMyContest.adapter = adapter

        adapter.onItemClick = { objects ->
            val intent = Intent(context, LeadersBoardActivity::class.java)
            intent.putExtra(LeadersBoardActivity.SERIALIZABLE_MATCH_KEY, objectMatches)
            intent.putExtra(LeadersBoardActivity.SERIALIZABLE_CONTEST_KEY, objects)
            requireActivity().startActivityForResult(
                intent,
                LeadersBoardActivity.CREATETEAM_REQUESTCODE
            )
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
        Log.e(TAG, "selected_position ========> ${(activity as MoreContestActivity).selected_position}")

        for (i in (activity as MoreContestActivity).allContestList!!.indices) {
            val contObject = (activity as MoreContestActivity).allContestList!![i]

            if ((activity as MoreContestActivity).selected_position == (i)) {
                Log.e(TAG, "selected_position ========> ${(activity as MoreContestActivity).selected_position}")
                Log.e(TAG, "i ========> $i")
                Log.e(TAG, "title ========> ${contObject.contestTitle}")
                contestList = contObject.allContestsRunning!!
            }
        }

        mBinding!!.recyclerMyContest.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        val colorCode = requireActivity().resources.getColor(R.color.white)

        adapter = ContestListAdapter(
            requireActivity(),
            contestList,
            objectMatches!!,
            mListenerContestEvents as OnContestEvents,
            colorCode
        )
        mBinding!!.recyclerMyContest.adapter = adapter
    }
}