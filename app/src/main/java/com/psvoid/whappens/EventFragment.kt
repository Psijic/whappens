package com.psvoid.whappens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.psvoid.whappens.data.ClusterMarker
import com.psvoid.whappens.databinding.FragmentEventBottomSheetBinding
import com.psvoid.whappens.viewmodels.EventViewModel

class EventFragment : Fragment() {

    private val viewModel: EventViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val binding = FragmentEventBottomSheetBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentEventBottomSheetBinding>(
            inflater, R.layout.fragment_event_bottom_sheet, container, false
        )

        binding.event = ClusterMarker(name = "NEW ClusterMarker")

        return binding.root
//        return inflater.inflate(R.layout.fragment_event_bottom_sheet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
