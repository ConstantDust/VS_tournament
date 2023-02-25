package org.valkyrienskies.tournament.item.blockitem

import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.data.BuiltinRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import org.valkyrienskies.tournament.api.TournamentBlockstateProperties
import org.valkyrienskies.tournament.tournamentItems

class ThrusterBlockItem(block: Block, properties: Properties) : BlockItem(block, properties.tab(tournamentItems.TAB)) {

    override fun getName(stack: ItemStack): Component {
        val tier = Registry.ITEM.getKey(stack.item).path.last().toString().toInt()
        return TextComponent("Thruster Tier: $tier")
    }

    override fun place(context: BlockPlaceContext): InteractionResult {
        val t = super.place(context)
        if (t == InteractionResult.FAIL) {
            return t
        }

        val blockPlaceContext = updatePlacementContext(context)

        val level: Level = blockPlaceContext!!.level

        if (level.isClientSide) return InteractionResult.SUCCESS
        level as ServerLevel

        val blockPos: BlockPos = blockPlaceContext!!.clickedPos
        val blockState2 = level.getBlockState(blockPos)
        val player = context.player

        val ti = Registry.ITEM.getKey(player?.mainHandItem?.item).path.last().toString().toInt()

        blockState2.setValue(TournamentBlockstateProperties.TIER, ti)

        println(blockState2.getValue(TournamentBlockstateProperties.TIER))

        level.setBlock(blockPos, blockState2, 1)

        println(level.getBlockState(blockPos).getValue(TournamentBlockstateProperties.TIER))

        return t
    }
}