package org.valkyrienskies.tournament.blockentity

import net.minecraft.client.renderer.texture.Tickable
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BlockStateProperties.LEVEL
import net.minecraft.world.level.block.state.properties.BlockStateProperties.POWER
import org.joml.Vector3d
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips
import org.valkyrienskies.tournament.tournamentBlockEntities

class SensorBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(tournamentBlockEntities.SENSOR.get(), pos, state), Tickable {

    private var otherPos: BlockPos? = null

    override fun load(tag: CompoundTag) {
        super.load(tag)

        if (tag.contains("otherPos")) {

        }
    }

    private fun basePoint(): Vector3d = blockPos.toJOMLD()
        .add(0.5, 0.5,0.5)
        .add(blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD().mul(0.5))

    fun getResult(level: ServerLevel) {
        val level = level

        val lookingTowards = blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD()

        val ship = level.getShipObjectManagingPos(blockPos)

        val clipResult = level.clipIncludeShips(
            ClipContext(
                (Vector3d(basePoint()).let {
                    ship?.shipToWorld?.transformPosition(it) ?: it
                }).toMinecraft(),
                (blockPos.toJOMLD()
                    .add(0.5, 0.5,0.5)
                    .add(Vector3d(lookingTowards).mul(5.0))
                    .let {
                        ship?.shipToWorld?.transformPosition(it) ?: it
                    }).toMinecraft(),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null), false)

        println(clipResult)

        if(clipResult != null){
            blockState.setValue(POWER, 0)
            println("15")
        } else {
            blockState.setValue(POWER, 0)
            println("0")
        }
    }


    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
//        tag.putBoolean("isBase", isBase)
//
//        if (otherPos != null) {
//            tag.putLong("otherPos", otherPos!!.asLong())
//        }
    }

    override fun tick() {
        println("Sensor Tick!")
        getResult(super.level as ServerLevel)
    }
}