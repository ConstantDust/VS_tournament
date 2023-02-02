package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.tournament.item.*
import org.valkyrienskies.tournament.registry.DeferredRegister

@Suppress("unused")
object tournamentItems {
    private val ITEMS = DeferredRegister.create(tournamentMod.MOD_ID, Registry.ITEM_REGISTRY)

    val PULSEGUN        = ITEMS.register("pulse_gun", ::PulseGun)
    val DELETEGUN       = ITEMS.register("delete_gun", ::ShipDeleteGun)
    val GRABGUN         = ITEMS.register("grab_gun", ::GrabGun)
    val THRUSTERUPGRADE = ITEMS.register("upgrade_thruster", ::ThrusterUpgrade)

    val TAB: CreativeModeTab = object : CreativeModeTab(CreativeModeTab.TABS.size,"vs_tournament.tournament_tab") {
        override fun makeIcon(): ItemStack? {
            return ItemStack(tournamentBlocks.SHIPIFIER.get())
        }
    }

    fun register() {
        // TODO: mixin or something AT
        // CreativeModeTab.TABS.size += 1

        tournamentBlocks.registerItems(ITEMS)
        ITEMS.applyAll()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }

}