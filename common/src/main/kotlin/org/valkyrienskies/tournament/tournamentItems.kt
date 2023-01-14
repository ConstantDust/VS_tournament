package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.tournament.registry.CreativeTabs
import org.valkyrienskies.tournament.registry.DeferredRegister

@Suppress("unused")
object tournamentItems {
    private val ITEMS = DeferredRegister.create(tournamentMod.MOD_ID, Registry.ITEM_REGISTRY)
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
