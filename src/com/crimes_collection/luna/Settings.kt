package com.crimes_collection.luna

import lunalib.lunaSettings.LunaSettings

class Settings {
    companion object {
        val strengthRatio: Float
            get() = LunaSettings.getFloat("auto_combat_resolver_inator", "strengthRatio") ?: 3.5f
    }
}