package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FireBlock
import org.valkyrienskies.mod.common.hooks.VSGameEvents
import org.valkyrienskies.tournament.block.*
import org.valkyrienskies.tournament.registry.DeferredRegister

@Suppress("unused")
object tournamentBlocks {
    private val BLOCKS = DeferredRegister.create(tournamentMod.MOD_ID, Registry.BLOCK_REGISTRY)

    val BALLAST     = BLOCKS.register("ballast", ::BallastBlock)
    val BALLOON     = BLOCKS.register("balloon", ::BalloonBlock)
    val THRUSTER    = BLOCKS.register("thruster", ::ThrusterBlock)
    val SPINNER     = BLOCKS.register("spinner", ::SpinnerBlock)


    // region Ship Helms


    // endregion

    fun register() {
        BLOCKS.applyAll()

        VSGameEvents.registriesCompleted.on { _, _ ->
            makeFlammables()
        }
    }

    // region Flammables
    // TODO make this part of the registration sequence
    fun flammableBlock(block: Block?, flameOdds: Int, burnOdds: Int) {
        val fire = Blocks.FIRE as FireBlock
        fire.setFlammable(block, flameOdds, burnOdds)
    }

    fun makeFlammables() {
        //flammableBlock(FLOATER.get(), 5, 20)
    }
    // endregion

    // Blocks should also be registered as items, if you want them to be able to be held
    // aka all blocks
    fun registerItems(items: DeferredRegister<Item>) {
        BLOCKS.forEach {
            items.register(it.name) { BlockItem(it.get(), Item.Properties().tab(CreativeModeTab.TAB_MISC)) }
        }
    }

}
