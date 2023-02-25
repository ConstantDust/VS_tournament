// stolen from https://github.com/ThePlasticPotato/vs-takeoff

package org.valkyrienskies.tournament.block

// commented out bc constantdust MESSED UP GRADLE!!!!!! TODO: uncomment when gradle fixed

//import net.minecraft.core.BlockPos
//import net.minecraft.core.Direction
//import net.minecraft.server.level.ServerLevel
//import net.minecraft.world.item.context.BlockPlaceContext
//import net.minecraft.world.level.BlockGetter
//import net.minecraft.world.level.Level
//import net.minecraft.world.level.block.*
//import net.minecraft.world.level.block.entity.BlockEntity
//import net.minecraft.world.level.block.state.BlockState
//import net.minecraft.world.level.block.state.StateDefinition
//import net.minecraft.world.level.block.state.properties.BlockStateProperties
//import net.minecraft.world.level.material.Material
//import net.minecraft.world.phys.shapes.CollisionContext
//import net.minecraft.world.phys.shapes.VoxelShape
//import org.valkyrienskies.tournament.blockentity.HingeBlockEntity
//import org.valkyrienskies.tournament.util.DirectionalShape
//import org.valkyrienskies.tournament.util.RotShapes
//
//
//class HingeBlock : BaseEntityBlock(Properties.of(Material.BAMBOO)) {
//    val BEARING_FLAT = RotShapes.box(0.5, 0.25, 0.5, 15.5, 2.75, 15.5)
//    //val BEARING_SLOT = RotShapes.box(7.0, 2.0, 14.0, 9.0, 24.0, 16.0)
//
//    val BEARING_BASE_SHAPE = DirectionalShape.up(BEARING_FLAT) //RotShapes.or(BEARING_FLAT, BEARING_SLOT))
//
//    init {
//        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.FACING, Direction.NORTH))
//    }
//
//    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
//        builder.add(BlockStateProperties.FACING)
//    }
//
//    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = HingeBlockEntity(pos, state)
//
//    override fun getRenderShape(blockState: BlockState): RenderShape {
//        return RenderShape.MODEL
//    }
//
//    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
//        return BEARING_BASE_SHAPE[state.getValue(BlockStateProperties.FACING)]
//    }
//
//    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
//        super.onPlace(state, level, pos, oldState, isMoving)
//
//        if (level.isClientSide) return
//        level as ServerLevel
//
//        val be = level.getBlockEntity(pos) as HingeBlockEntity
//        be.makeTop()
//    }
//
//    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
//        super.onRemove(state, level, pos, newState, isMoving)
//
//        if (level.isClientSide) return
//        level as ServerLevel
//    }
//
//    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
//        return defaultBlockState()
//            .setValue(BlockStateProperties.FACING, ctx.nearestLookingDirection.opposite)
//    }
//}