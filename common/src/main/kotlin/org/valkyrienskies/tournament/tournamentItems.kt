package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.tournament.api.LoaderType
import org.valkyrienskies.tournament.item.*
import org.valkyrienskies.tournament.registry.DeferredRegister

@Suppress("unused")
object tournamentItems {
    private val ITEMS = DeferredRegister.create(tournamentMod.MOD_ID, Registry.ITEM_REGISTRY)

    var TAB: CreativeModeTab = CreativeModeTab.TAB_MISC // will be created in forge / fabric mod main

    var PULSEGUN        = ITEMS.register("pulse_gun", ::PulseGun)
    val DELETEGUN       = ITEMS.register("delete_gun", ::ShipDeleteGun)
    val GRABGUN         = ITEMS.register("grab_gun", ::GrabGun)
    val THRUSTERUPGRADE = ITEMS.register("upgrade_thruster", ::ThrusterUpgrade)

    fun getTab(): CreativeModeTab {
        return this.TAB
    }

    fun register(loader: LoaderType) {
        tournamentBlocks.registerItems(ITEMS)

        ITEMS.applyAll()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }

}