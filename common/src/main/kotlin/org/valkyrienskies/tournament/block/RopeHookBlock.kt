package org.valkyrienskies.tournament.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.joml.Vector3d
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.physics_api.ConstraintId
import org.valkyrienskies.tournament.api.DirectionalBlockEntityBlock
import org.valkyrienskies.tournament.api.Helper3d
import org.valkyrienskies.tournament.blockentity.RopeHookBlockEntity
import org.valkyrienskies.tournament.util.DirectionalShape
import org.valkyrienskies.tournament.util.RotShapes
import java.util.*
import kotlin.math.absoluteValue

class RopeHookBlock : DirectionalBlockEntityBlock(
    Properties.of(Material.STONE)
        .sound(SoundType.STONE).strength(1.0f, 2.0f)
) {

    val SHAPE = RotShapes.box(0.25, 0.0, 0.25, 15.75, 16.0, 15.75)
    val ROPEATTACH_SHAPE = DirectionalShape.north(SHAPE)

    init {
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.POWER, 0))
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: Random) {
        super.animateTick(state, level, pos, random)

        val be = level.getBlockEntity(pos) as RopeHookBlockEntity

        println("doing anim tick (pre is set)")

        println("is set: ${be.isSet()}")

        if (!be.isSet()) {return}

        println("doing anim tick (is set)")

        var (ropeId, MainPos, OtherPos, maxLen) = be.getStuff()

        if(maxLen == 0.0){
            maxLen = (Helper3d.MaybeShipToWorldspace(level, OtherPos!!).distance(Helper3d.MaybeShipToWorldspace(level, MainPos!!))).absoluteValue
        }

        val p1 = Helper3d.VecBlockMid(Helper3d.MaybeShipToWorldspace(level, MainPos!!))
        val p2 = Helper3d.VecBlockMid(Helper3d.MaybeShipToWorldspace(level, OtherPos!!))

        Helper3d.drawQuadraticParticleCurve(p1, p2, maxLen, 5.0, level, ParticleTypes.CLOUD)

        be.writeLen(maxLen)
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

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = RopeHookBlockEntity(pos, state)

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        val signal = level.getBestNeighborSignal(pos)
        level.setBlock(pos, state.setValue(BlockStateProperties.POWER, signal), 2)
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        val be = level.getBlockEntity(pos) as RopeHookBlockEntity
        val id = be.getId()
        be.writeStuff(null, null, null, 0.0)

        super.onRemove(state, level, pos, newState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        level.shipObjectWorld.removeConstraint(id)

        state.setValue(BlockStateProperties.POWER, 0)
    }

    // sets the rope for deletion purposes
    fun SetRopeId(rope: ConstraintId, level: Level, main:Vector3d?, other:Vector3d?) {
        println("Block>> " + rope)
        val be = level.getBlockEntity(Helper3d.VecToPosition(main!!)) as RopeHookBlockEntity

        be.writeAndSet(rope, other, main, main.distance(other))

        println("setting + is set: ${be.isSet()}")

        be.markUpdated()
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