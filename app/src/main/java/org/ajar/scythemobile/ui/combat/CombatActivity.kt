package org.ajar.scythemobile.ui.combat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.ajar.scythemobile.R

class CombatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.combat_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, StartCombatFragment.newInstance())
                    .commitNow()
        }
    }

}
