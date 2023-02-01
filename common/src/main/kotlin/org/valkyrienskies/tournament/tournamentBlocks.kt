package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FireBlock
import org.lwjgl.system.CallbackI
import org.valkyrienskies.mod.common.hooks.VSGameEvents
import org.valkyrienskies.tournament.block.*
import org.valkyrienskies.tournament.registry.DeferredRegister

@Suppress("unused")
object tournamentBlocks {
    private val BLOCKS = DeferredRegister.create(tournamentMod.MOD_ID, Registry.BLOCK_REGISTRY)
    private var no_tab = ArrayList<String>()

    val BALLAST     = BLOCKS.register("ballast", ::BallastBlock)
    val BALLOON     = BLOCKS.register("balloon", ::BalloonBlock)
    val THRUSTER    = BLOCKS.register("thruster", ::ThrusterBlock)
    val SPINNER     = BLOCKS.register("spinner", ::SpinnerBlock)
    val SHIPIFIER   = BLOCKS.register("shipifier", ::ShipifierBlock,)
    val HINGE   = BLOCKS.register("hinge", ::HingeBlock)
    val HINGE_TOP   = BLOCKS.register("hinge_top", ::HingeTopBlock)

    fun register() {
        no_tab.add("hinge_top")

        BLOCKS.applyAll()
    }

    fun registerItems(items: DeferredRegister<Item>) {
        BLOCKS.forEach {
            if (it.name in no_tab) {
                items.register(it.name) { BlockItem(it.get(), Item.Properties()) }
            } else {
                items.register(it.name) { BlockItem(it.get(), Item.Properties().tab(tournamentItems.TAB)) }
            }
        }
    }

}
