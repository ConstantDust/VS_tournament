package org.valkyrienskies.tournament.block

import com.mojang.math.Vector3f
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.joml.Vector3d
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.tournament.api.TournamentBlockstateProperties
import org.valkyrienskies.tournament.ship.tournamentShipControl
import org.valkyrienskies.tournament.tournamentConfig
import org.valkyrienskies.tournament.tournamentItems
import org.valkyrienskies.tournament.util.DirectionalShape
import org.valkyrienskies.tournament.util.RotShapes
import org.valkyrienskies.tournament.util.ShipAssembler
import java.util.*

class ShipifierBlock : DirectionalBlock (
    Properties.of(Material.STONE)
        .sound(SoundType.STONE).strength(1.0f, 2.0f)
) {

    val SHAPE = RotShapes.box(0.01, 0.01, 0.01, 15.98, 15.98, 15.98)

    val Shipifier_SHAPE = DirectionalShape.south(SHAPE)

    init {
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.POWER, 0).setValue(TournamentBlockstateProperties.TIER, 1))
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shipifier_SHAPE[state.getValue(BlockStateProperties.FACING)]
    }

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

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(BlockStateProperties.POWER)
        builder.add(TournamentBlockstateProperties.TIER)
        super.createBlockStateDefinition(builder)
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
        level.setBlock(pos, state.setValue(BlockStateProperties.POWER, signal), 2)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        return defaultBlockState()
            .setValue(FACING, ctx.nearestLookingDirection)
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: Random) {
        super.animateTick(state, level, pos, random)
        if (state.getValue(BlockStateProperties.POWER) > 0) {
            val dir = state.getValue(FACING)

            val x = pos.x.toDouble() + (0.5 * (dir.stepX + 1));
            val y = pos.y.toDouble() + (0.5 * (dir.stepY + 1));
            val z = pos.z.toDouble() + (0.5 * (dir.stepZ + 1));
            val speedX = dir.stepX * -0.4
            val speedY = dir.stepY * -0.4
            val speedZ = dir.stepZ * -0.4

            level.addParticle(ParticleTypes.FIREWORK, x, y, z, speedX, speedY, speedZ)
        }
    }

}