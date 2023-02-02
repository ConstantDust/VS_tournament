package org.valkyrienskies.tournament.api

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block

class PropertyBlockItem(block: Block, properties: Properties, tier: Int) : BlockItem(block, properties) {

    private var blocktier: Int = tier

    override fun place(context: BlockPlaceContext): InteractionResult {
        val t = super.place(context)
        if (t == InteractionResult.FAIL) {
            return t
        }

        val blockPlaceContext = updatePlacementContext(context)

        val blockState = getPlacementState(blockPlaceContext)
        val blockPos: BlockPos = blockPlaceContext!!.clickedPos
        val level: Level = blockPlaceContext!!.level
        val blockState2 = level.getBlockState(blockPos)

        blockState2.setValue(TournamentBlockstateProperties.TIER, blocktier)

        level.sendBlockUpdated(blockPos, blockState, blockState2, 0)

        return t
    }
}