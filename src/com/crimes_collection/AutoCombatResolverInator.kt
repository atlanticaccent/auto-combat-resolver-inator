package com.crimes_collection

import com.crimes_collection.luna.Settings
import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.fleet.FleetGoal
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl
import com.fs.starfarer.api.util.Misc
import kotlin.math.max

class AutoCombatResolverInator : EveryFrameScript {
    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }

    override fun advance(amount: Float) {
        val dialog = Global.getSector().campaignUI.currentInteractionDialog
        if (dialog != null && dialog.plugin is FleetInteractionDialogPluginImpl) {
            val minStrengthRatio = Settings.strengthRatio
            val plugin = dialog.plugin as FleetInteractionDialogPluginImpl
            val context = plugin.context as FleetEncounterContext
            val battle = context.battle

            // Ignore allies when considering whether your fleet is big enough for auto resolve.
            val playerFleet = Global.getSector().playerFleet
            val playerStrength: Float = getEffectiveStrength(playerFleet)
            val enemyStrength: Float = getEffectiveStrength(battle.nonPlayerCombined)
            val threshold: Float = enemyStrength * minStrengthRatio

            // If the other fleet is already escaping, then we already have an auto resolve option.

            val playerGoal = ReflectionUtils.get("playerGoal", plugin) as? FleetGoal
            val otherGoal = ReflectionUtils.get("otherFleet", plugin) as? FleetGoal
            // If the other fleet is already escaping, then we already have an auto resolve option.
            val force = playerGoal == FleetGoal.ATTACK && otherGoal != FleetGoal.ESCAPE && playerStrength > threshold
            if (!dialog.optionPanel.hasOption(FleetInteractionDialogPluginImpl.OptionId.AUTORESOLVE_PURSUE) && force) {
                dialog.optionPanel.addOption(
                    "Force the enemy fleet into an engagement without any risk to your own.", FleetInteractionDialogPluginImpl.OptionId.AUTORESOLVE_PURSUE
                )
            }
        }
    }

    private fun getEffectiveStrength(fleet: CampaignFleetAPI): Float {
        // getEffectiveStrength doesn't work for combined fleets (BattleAPI.getPlayerCombined() for example),
        // so we sum up their member strengths instead.
        var memberStrengths = 0f
        for (member in fleet.membersWithFightersCopy) {
            memberStrengths += Misc.getMemberStrength(member)
        }
        return max(fleet.effectiveStrength.toDouble(), memberStrengths.toDouble()).toFloat()
    }
}