package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import org.valkyrienskies.tournament.block.*
import org.valkyrienskies.tournament.block.explosive.*
import org.valkyrienskies.tournament.registry.DeferredRegister

@Suppress("unused")
object tournamentBlocks {
    private val BLOCKS = DeferredRegister.create(tournamentMod.MOD_ID, Registry.BLOCK_REGISTRY)

    private var no_tab = ArrayList<String>()
    private var tier_sets = ArrayList<String>()

    val BALLAST             = BLOCKS.register("ballast", ::BallastBlock)
    val BALLOON             = BLOCKS.register("balloon", ::BalloonBlock)
    val THRUSTER            = BLOCKS.register("thruster", ::ThrusterBlock)
    val IONTHRUSTER         = BLOCKS.register("ionthruster", ::IonThrusterBlock)
    val SPINNER             = BLOCKS.register("spinner", ::SpinnerBlock)
    val SHIPIFIER           = BLOCKS.register("shipifier", ::ShipifierBlock)
    // commented out bc constantdust MESSED UP GRADLE!!!!!! TODO: uncomment when gradle fixed
    //val HINGE       = BLOCKS.register("hinge", ::HingeBlock)
    //val HINGE_TOP   = BLOCKS.register("hinge_top", ::HingeTopBlock)
    val SEAT                = BLOCKS.register("seat", ::SeatBlock)
    val ROPEHOOK            = BLOCKS.register("rope_hook", ::RopeHookBlock)
    val SENSOR              = BLOCKS.register("sensor", ::SensorBlock)
    val INSTANTEXPLOSIVE    = BLOCKS.register("instantexplosive", ::InstantExplosiveBlock)
    val BIGINSTANTEXPLOSIVE = BLOCKS.register("instantexplosive_big", ::BigInstantExplosiveBlock)
    val STAGEDEXPLOSIVE     = BLOCKS.register("stagedexplosive", ::StagedExplosiveBlock)
    val BIGSTAGEDEXPLOSIVE  = BLOCKS.register("stagedexplosive_big", ::BigStagedExplosiveBlock)
    val OBSIDIANEXPLOSIVE    = BLOCKS.register("obsidianexplosive", ::ObsidianExplosiveBlock)


    fun register() {
        no_tab.add("hinge_top")

        tier_sets.add("thruster")

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
