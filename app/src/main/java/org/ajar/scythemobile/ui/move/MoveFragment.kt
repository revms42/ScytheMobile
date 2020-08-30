package org.ajar.scythemobile.ui.move

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.support.v4.app.Fragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import org.ajar.scythemobile.R

class MoveFragment : Fragment() {

    private lateinit var moveViewModel: MoveViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        moveViewModel =
                ViewModelProviders.of(this).get(MoveViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        moveViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}