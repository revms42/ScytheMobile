package org.ajar.scythemobile.ui.combat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import org.ajar.scythemobile.R

class SendCombatRequest : Fragment() {

    companion object {
        fun newInstance() = SendCombatRequest()
    }

    private lateinit var viewModel: CombatViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.send_combat_request_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(CombatViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.sendCombatRequestButton)?.let {
            it.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_combat_defend))
                intent.putExtra(Intent.EXTRA_TEXT, viewModel.generateCombatRequest())

                startActivity(Intent.createChooser(intent, "Send Combat Request to ${viewModel.opponent}"))
            }
        }
    }
}
