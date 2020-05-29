package com.psvoid.whappens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.psvoid.whappens.databinding.FragmentEventBottomSheetBinding
import com.psvoid.whappens.viewmodels.EventViewModel

class EventFragment : Fragment() {

    private val viewModel: EventViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentEventBottomSheetBinding.inflate(inflater, container, false)

        return inflater.inflate(R.layout.fragment_event_bottom_sheet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
