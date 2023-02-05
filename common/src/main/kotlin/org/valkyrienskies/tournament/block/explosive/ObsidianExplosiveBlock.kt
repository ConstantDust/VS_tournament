package org.valkyrienskies.tournament.block.explosive

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import org.valkyrienskies.tournament.api.algo2d
import org.valkyrienskies.tournament.api.Helper2d
import org.valkyrienskies.tournament.api.Helper3d

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
            val o = algo2d.filledCircle( Helper2d.vec3to2(Helper3d.PositionToVec(pos)), 10.0 )
            o?.forEach {
                val pos = Helper3d.VecToPosition(Helper2d.vec2to3(it, pos.y-1.0))
                level.removeBlockEntity(pos)
                level.removeBlock(pos, false)
            }
        }
    }

}