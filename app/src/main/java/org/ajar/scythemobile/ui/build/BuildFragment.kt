package org.ajar.scythemobile.ui.build

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.ajar.scythemobile.R
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.turn.TurnHolder
import org.ajar.scythemobile.ui.ScytheTurnFragment

class BuildFragment : ScytheTurnFragment(R.id.nav_build) {

    private lateinit var buildViewModel: BuildViewModel
    private val navigationArgs: BuildFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        buildViewModel =
                ViewModelProvider(requireActivity()).get(BuildViewModel::class.java)

        if(buildViewModel.unitType == null && navigationArgs.deployFromUnit != -1) {
            buildViewModel.unitType = navigationArgs.deployFromUnit
        } else {
            buildViewModel.unitType = UnitType.WORKER.ordinal
        }
        if(buildViewModel.returnNav == null && navigationArgs.returnNav != -1) buildViewModel.returnNav = navigationArgs.returnNav

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override val redirect: Boolean
        get() {
            return scytheTurnViewModel.isBottomRowComplete()
        }

    override fun onResume() {
        super.onResume()

        if(buildViewModel.canBuildStructure(!navigationArgs.paid)) {
            if(!navigationArgs.paid && !TurnHolder.currentTurn.performedBottom) {
                requireActivity().let { activity ->
                    val builder = AlertDialog.Builder(activity)

                    builder.apply {
                        setPositiveButton(getString(R.string.button_perform_build)) { _, _ ->
                            setupChooseResources()
                        }

                        setNegativeButton(getString(R.string.button_skip_building)) { _, _ ->
                            showSnackbarMessage(R.string.msg_build_skipped)
                            navigateOut()
                        }

                        builder.setMessage(R.string.msg_build_or_skip)
                        builder.setTitle(R.string.title_build)
                    }

                    builder.create().show()
                }
            } else if(navigationArgs.paid) {
                setupChooseBuildSite()
            }
        }
    }

    override fun destinationDirections(): NavDirections {
        return BuildFragmentDirections.actionNavBuildToNavEnd()
    }

    private fun setupChooseResources() {
        val cost = if(navigationArgs.cost == null) {
            buildViewModel.cost.map { it.id }.toIntArray()
        } else {
            navigationArgs.cost
        }?: IntArray(0)
        findNavController().navigate(BuildFragmentDirections.actionNavBuildToNavResourcePaymentChoice(cost, R.id.action_nav_resource_payment_choice_to_nav_build))
    }

    private fun setupChooseBuildSite() {
        if(buildViewModel.selectedHex == null) {
            buildViewModel.setupSelectBuildSite(requireActivity(), ::completeBuild)?.show()
        } else {
            setupChooseStructure()
        }
    }

    private fun setupChooseStructure() {
        buildViewModel.setupSelectStructureObserver(requireActivity(), ::completeBuild)?.show()
    }

    private fun completeBuild(done: Boolean) {
        if(done) {
            showSnackbarMessage(getString(R.string.snackbar_finished, buildViewModel.selectedStructure!!.type.toString()))
            navigateOut()
        }
    }

    override fun navigateOut() {
        buildViewModel.returnNav?.also {
            findNavController().navigate(ActionOnlyNavDirections(it))
        }?: super.navigateOut()
    }
}