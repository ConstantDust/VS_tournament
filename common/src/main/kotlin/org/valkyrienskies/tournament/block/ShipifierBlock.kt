package org.valkyrienskies.tournament.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.tournament.tournamentConfig
import org.valkyrienskies.tournament.util.ShipAssembler

class ShipifierBlock : Block(
        Properties.of(Material.METAL)
                .sound(SoundType.METAL).strength(1.0f, 2.0f)
) {

    override fun use(state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult): InteractionResult {
        return asm(level, pos)
    }

    private fun asm(level: Level, pos: BlockPos): InteractionResult {
        if (level as? ServerLevel == null) return InteractionResult.PASS
        if(level.getShipManagingPos(pos) != null) return InteractionResult.PASS

        val level = level as ServerLevel
        ShipAssembler.collectBlocks(level,pos) {
            !it.isAir && !tournamentConfig.SERVER.blockBlacklist.contains(Registry.BLOCK.getKey(it.block).toString())
        }

        return InteractionResult.SUCCESS
    }

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
        asm(level, pos)
    }

}
