package org.valkyrienskies.tournament.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.physics_api.ConstraintId
import org.valkyrienskies.tournament.api.Bresenham3D
import org.valkyrienskies.tournament.api.Helper3d
import org.valkyrienskies.tournament.ship.tournamentShipControl
import org.valkyrienskies.tournament.util.DirectionalShape
import org.valkyrienskies.tournament.util.RotShapes
import java.util.*
import kotlin.collections.ArrayList

class RopeHookBlock : DirectionalBlock(
    Properties.of(Material.STONE)
        .sound(SoundType.STONE).strength(1.0f, 2.0f)
) {

    val SHAPE = RotShapes.box(0.25, 0.0, 0.25, 15.75, 16.0, 15.75)
    val ROPEATTACH_SHAPE = DirectionalShape.north(SHAPE)

    private var ropeId: ConstraintId? = null
    private var MainPos:Vector3d? = null
    private var OtherPos:Vector3d? = null

    private var maxLen: Double = 0.0

    init {
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.POWER, 0))
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: Random) {
        super.animateTick(state, level, pos, random)
        if (OtherPos != null) {
            val p1 = Helper3d.MaybeShipToWorldspace(level, MainPos!!)
            val p2 = Helper3d.MaybeShipToWorldspace(level, OtherPos!!)

            Helper3d.drawQuadraticParticleCurve(p1, p2, maxLen, level, ParticleTypes.CLOUD)
        }
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return ROPEATTACH_SHAPE[state.getValue(BlockStateProperties.FACING)]
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(BlockStateProperties.POWER)
        super.createBlockStateDefinition(builder)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        val signal = level.getBestNeighborSignal(pos)
        level.setBlock(pos, state.setValue(BlockStateProperties.POWER, signal), 2)

    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        state.setValue(BlockStateProperties.POWER, 0)

        // delets any existing ropes
        ropeId?.let { level.shipObjectWorld.removeConstraint(it) }
        ropeId = null
        OtherPos = null
        MainPos = null
    }

    // sets the rope for deletion purposes
    fun SetRopeId(rope: ConstraintId, main:Vector3d?, other:Vector3d?) {
        println("Block>> " + rope)
        ropeId = rope
        OtherPos = other
        MainPos = main
        maxLen = other!!.distance(main)
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
            .setValue(FACING, ctx.nearestLookingDirection.opposite)
    }
}