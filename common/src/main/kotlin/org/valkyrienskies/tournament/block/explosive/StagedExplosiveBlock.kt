package org.valkyrienskies.tournament.block.explosive

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material

class StagedExplosiveBlock : Block(
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
            level.explode(
                PrimedTnt(EntityType.TNT, level),
                pos.x + 0.5,
                pos.y + 0.5,
                pos.z + 0.5,
                8f,
                Explosion.BlockInteraction.BREAK
            )
            for (i in 1..10) {
                level.explode(
                    PrimedTnt(EntityType.TNT, level),
                    pos.x + 0.5 + (-20..20).random(),
                    pos.y + 0.5 + (-7..4).random(),
                    pos.z + 0.5 + (-20..20).random(),
                    (7..15).random().toFloat(),
                    Explosion.BlockInteraction.BREAK
                )
            }
        }
    }

}