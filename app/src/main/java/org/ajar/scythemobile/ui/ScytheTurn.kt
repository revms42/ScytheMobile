package org.ajar.scythemobile.ui

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import org.ajar.scythemobile.model.NaturalResourceType
import org.ajar.scythemobile.R
import org.ajar.scythemobile.ScytheMobile
import org.ajar.scythemobile.data.ResourceData
import org.ajar.scythemobile.data.ScytheDatabase
import org.ajar.scythemobile.data.UnitData
import org.ajar.scythemobile.model.PlayerInstance
import org.ajar.scythemobile.model.combat.CombatCardDeck
import org.ajar.scythemobile.model.entity.UnitType
import org.ajar.scythemobile.model.faction.RiverWalk
import org.ajar.scythemobile.model.faction.Speed
import org.ajar.scythemobile.model.faction.StandardFactionMat
import org.ajar.scythemobile.model.player.StandardPlayerMat
import org.ajar.scythemobile.turn.TurnHolder

class ScytheTurn : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var viewModel: ScytheTurnViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TurnHolder.debugDatabase()
        ScytheDatabase.reset()

        setContentView(R.layout.activity_scythe_turn)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Temporary testing for movement", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        viewModel = ViewModelProvider(this).get(ScytheTurnViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

        //TODO: **** Temporary for testing ****
        ScytheDatabase.init(applicationContext)
        ScytheDatabase.reset()
        ScytheMobile.loadLocalizedNames(this)
        val playerInstance = PlayerInstance.makePlayer("testPlayer", StandardPlayerMat.MECHANICAL.id, StandardFactionMat.CRIMEA.id)
        ScytheDatabase.playerDao()?.addPlayer(playerInstance.playerData)
        ScytheDatabase.unitDao()?.addUnit(UnitData(1, 0, 21, UnitType.CHARACTER.ordinal))
        ScytheDatabase.unitDao()?.addUnit(UnitData(2, 0, 15, UnitType.MECH.ordinal))
        ScytheDatabase.unitDao()?.addUnit(UnitData(3, 0, -1, UnitType.MINE.ordinal))
        ScytheDatabase.unitDao()?.addUnit(UnitData(4, 0, -1, UnitType.ARMORY.ordinal))
        ScytheDatabase.unitDao()?.addUnit(UnitData(5, 0, -1, UnitType.MILL.ordinal))
        ScytheDatabase.unitDao()?.addUnit(UnitData(6, 0, -1, UnitType.MONUMENT.ordinal))
        playerInstance.factionMat.lockedFactionAbilities.forEach { playerInstance.factionMat.unlockFactionAbility(it) }
        TurnHolder.updatePlayer(playerInstance.playerData)
        ScytheDatabase.unitDao()?.addUnit(UnitData(7, 0, 21, UnitType.WORKER.ordinal))
//        ScytheDatabase.resourceDao()?.addResource(ResourceData(0, 21, -1, NaturalResourceType.FOOD.id))
//        ScytheDatabase.resourceDao()?.addResource(ResourceData(1, 21, -1, NaturalResourceType.FOOD.id))
        ScytheDatabase.resourceDao()?.addResource(ResourceData(2, 21, -1, NaturalResourceType.WOOD.id))
        ScytheDatabase.resourceDao()?.addResource(ResourceData(3, 21, -1, NaturalResourceType.WOOD.id))
        ScytheDatabase.resourceDao()?.addResource(ResourceData(4, 21, -1, NaturalResourceType.WOOD.id))
        ScytheDatabase.resourceDao()?.addResource(ResourceData(5, 21, -1, NaturalResourceType.WOOD.id))
//        ScytheDatabase.resourceDao()?.addResource(ResourceData(6, 21, -1, NaturalResourceType.OIL.id))
//        ScytheDatabase.resourceDao()?.addResource(ResourceData(7, 21, -1, NaturalResourceType.OIL.id))

        val enemyPlayer = PlayerInstance.makePlayer("enemyPlayer", StandardPlayerMat.INDUSTRIAL.id, StandardFactionMat.NORDIC.id)
        ScytheDatabase.playerDao()?.addPlayer(enemyPlayer.playerData)
        ScytheDatabase.unitDao()?.addUnit(UnitData(8, 1, 22, UnitType.MECH.ordinal))
        ScytheDatabase.unitDao()?.addUnit(UnitData(9, 1, 23, UnitType.WORKER.ordinal))
        TurnHolder.updatePlayer(enemyPlayer.playerData)
        CombatCardDeck.currentDeck.drawCard(playerInstance)?.resourceData?.id = 10
        TurnHolder.commitChanges()
        //TODO: **** --------------------- ****

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.nav_start,
                        *viewModel.selectableSections.map { it.topRowAction.fragmentNav }.toTypedArray()
                )
                , drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //navController.navigate(viewModel.currentNav)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.scythe_turn, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onPause() {
        super.onPause()
        //TODO: **** Temporary for testing ****
        ScytheDatabase.reset()
        //TODO: **** --------------------- ****
    }
}