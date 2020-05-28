package com.psvoid.whappens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.psvoid.whappens.databinding.EventFragmentBinding
import com.psvoid.whappens.viewmodels.EventViewModel

class EventFragment : Fragment() {

    private val viewModel: EventViewModel by viewModels()

    private lateinit var binding: EventFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.event_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
