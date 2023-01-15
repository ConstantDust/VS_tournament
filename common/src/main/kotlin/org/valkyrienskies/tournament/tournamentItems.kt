package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.world.item.Item
import org.valkyrienskies.tournament.item.GrabGun
import org.valkyrienskies.tournament.item.GrabGunOG
import org.valkyrienskies.tournament.item.PulseGun
import org.valkyrienskies.tournament.registry.DeferredRegister

@Suppress("unused")
object tournamentItems {
    private val ITEMS = DeferredRegister.create(tournamentMod.MOD_ID, Registry.ITEM_REGISTRY)

    val GRABGUNOG = ITEMS.register("grabgun_old", ::GrabGunOG)
    val GRABGUN = ITEMS.register("grabgun", ::GrabGun)
    val PULSEGUN = ITEMS.register("pulse_gun", ::PulseGun)


//    val TAB: CreativeModeTab = CreativeTabs.create(
//        ResourceLocation(
//            tournamentMod.MOD_ID,
//            "tournament_tab"
//        )
//    ) { ItemStack(tournamentBlocks.BALLAST.get()) }

    fun register() {
        tournamentBlocks.registerItems(ITEMS)
        ITEMS.applyAll()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }
}
