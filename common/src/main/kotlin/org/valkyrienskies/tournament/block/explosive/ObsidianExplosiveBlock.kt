package org.valkyrienskies.tournament.block.explosive

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import org.valkyrienskies.tournament.api.Helper3d
import org.valkyrienskies.tournament.api.algo3d

class ObsidianExplosiveBlock  : Block(
    Properties.of(Material.METAL)
        .sound(SoundType.METAL).strength(1.0f, 2.0f)
) {

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving)

        if (level as? ServerLevel == null) return

        val signal = level.getBestNeighborSignal(pos)
        if (signal > 0) {
            var queue = algo3d.cone(Helper3d.PositionToVec(pos), 5.0, -5.0)

            queue.forEach {
                val pos = Helper3d.VecToPosition(it)
                level.removeBlock(pos, false)
            }
        }
    }

}