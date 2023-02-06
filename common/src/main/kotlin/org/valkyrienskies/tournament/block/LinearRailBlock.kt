package org.valkyrienskies.tournament.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.apigame.constraints.VSSlideConstraint
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.tournament.api.Helper3d

//TODO: some day

class LinearRailBlock : DirectionalBlock(
    Properties.of(Material.STONE)
        .sound(SoundType.STONE).strength(1.0f, 2.0f)
) {

    lateinit var ship: ServerShip
    lateinit var constrain: VSSlideConstraint

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        ship = level.shipObjectWorld.createNewShipAtBlock(
            Helper3d.VecDtoI(Helper3d.PositionToVec(pos).add(0.0, 1.0, 0.0)),
            false,
            1.0,
            level.dimensionId
        )

        level.setBlock(Helper3d.VecToPosition(Helper3d.VecDCtoD(ship.transform.positionInShip)), Blocks.IRON_BLOCK.defaultBlockState(), 1 )

        //constrain = VSSlideConstraint()

    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)
    }

}