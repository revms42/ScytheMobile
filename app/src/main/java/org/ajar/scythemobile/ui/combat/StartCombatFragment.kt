package org.ajar.scythemobile.ui.combat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.combat.CombatCard

class StartCombatFragment : Fragment() {

    companion object {
        fun newInstance() = StartCombatFragment()
    }

    private lateinit var viewModel: CombatViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.start_combat_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(CombatViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.let {
            val picker = it.findViewById<NumberPicker>(R.id.combatPowerPicker)?.also { picker ->
                picker.minValue = 0
                picker.maxValue = viewModel.playerCombatPower
            }

            it.findViewById<RecyclerView>(R.id.combatCardList)?.let { rv ->
                rv.adapter = CombatCardListAdapter(this@StartCombatFragment, viewModel.playerCombatCards, viewModel::setSelected)
                rv.layoutManager = LinearLayoutManager(requireContext())
            }

            it.findViewById<Button>(R.id.startCombatOk)?.let { start ->
                start.setOnClickListener {
                    viewModel.setPower(picker?.value?: 0)
                }
                findNavController().navigate(R.id.action_start_to_send_combat_request)
            }

            it.findViewById<Button>(R.id.startCombatCancel)?.let { cancel ->
                requireActivity().finish()
            }
        }
    }

    class CombatCardListAdapter(
            private val fragment: Fragment,
            private val combatCards: MutableList<CombatCard>,
            private val setSelected: (CombatCard) -> Unit
    ) : RecyclerView.Adapter<CombatCardViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CombatCardViewHolder {
            val inflater = fragment.requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            return CombatCardViewHolder(inflater.inflate(R.layout.card_list_item, parent, false), setSelected)
        }

        override fun getItemCount(): Int = combatCards.size

        override fun onBindViewHolder(holder: CombatCardViewHolder, position: Int) {
            combatCards[position].also {
                val text = "${it.power} Power Combat Card"
                holder.textField.text = text
                holder.combatCard = it
            }
        }

    }

    class CombatCardViewHolder(view: View, select: (CombatCard) -> Unit) : RecyclerView.ViewHolder(view) {
        val textField = view.findViewById<TextView>(R.id.cardText)
        val icon = view.findViewById<ImageView>(R.id.cardIcon)

        lateinit var combatCard: CombatCard

        init {
            view.setOnClickListener {
                select.invoke(combatCard)
            }
        }
    }
}
