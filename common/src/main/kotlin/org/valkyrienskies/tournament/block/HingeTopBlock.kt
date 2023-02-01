package org.valkyrienskies.tournament.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.tournament.util.DirectionalShape
import org.valkyrienskies.tournament.util.RotShapes

class HingeTopBlock : Block(
        Properties.of(Material.METAL)
                .sound(SoundType.METAL).strength(1.0f, 2.0f)
) {

    protected val TOP_AABB: DirectionalShape = DirectionalShape.north(RotShapes.box(0.0, 0.0, 0.0, 16.0, 16.0, 8.0))

    init {
        registerDefaultState(defaultBlockState().setValue(DirectionalBlock.FACING, Direction.DOWN))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(DirectionalBlock.FACING)
        super.createBlockStateDefinition(builder)
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }
    override fun getShape(state: BlockState, level: BlockGetter?, pos: BlockPos?, context: CollisionContext?): VoxelShape? {
        return TOP_AABB[state.getValue(BlockStateProperties.FACING)]
    }

}