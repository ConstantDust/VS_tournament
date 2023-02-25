package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.DoubleBlockCombiner
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.valkyrienskies.core.game.VSBlockType
import org.valkyrienskies.mod.common.BlockStateInfo
import org.valkyrienskies.mod.common.BlockStateInfoProvider

object tournamentWeights : BlockStateInfoProvider {
    override val priority: Int
        get() = 200

    override fun getBlockStateMass(blockState: BlockState): Double? {
        if (blockState.block == tournamentBlocks.BALLAST.get()) {
            return tournamentConfig.SERVER.ballastWeight + (tournamentConfig.SERVER.ballastNoWeight - tournamentConfig.SERVER.ballastWeight) * (
                    (
                            blockState.getValue(
                                BlockStateProperties.POWER
                            ) + 1
                            ) / 16.0
                    )
        }

        return null
    }

    override fun getBlockStateType(blockState: BlockState): VSBlockType? {
        // commented out bc constantdust MESSED UP GRADLE!!!!!! TODO: uncomment when gradle fixed

        //if (blockState.block == tournamentBlocks.HINGE_TOP.get())
        //    return vsCore.blockTypes.air
        return null
    }

    fun register() {
        Registry.register(BlockStateInfo.REGISTRY, ResourceLocation(tournamentMod.MOD_ID, "ballast"), tournamentWeights)
        //TODO: uncomment + fix to fix hinges (after gradle fixed)
        //Registry.register(BlockStateInfo.REGISTRY, ResourceLocation(tournamentMod.MOD_ID, "hinge_top"), tournamentWeights)
    }
}
