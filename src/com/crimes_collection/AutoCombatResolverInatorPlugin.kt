package com.crimes_collection

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global

class AutoCombatResolverInatorPlugin : BaseModPlugin() {
    override fun onGameLoad(newGame: Boolean) {
        Global.getSector().addTransientScript(AutoCombatResolverInator())
    }
}