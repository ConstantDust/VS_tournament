package org.valkyrienskies.tournament

import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.tournament.registry.DeferredRegister
import org.valkyrienskies.tournament.registry.RegistrySupplier

@Suppress("unused")
object tournamentBlockEntities {
    private val BLOCKENTITIES = DeferredRegister.create(tournamentMod.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY)

    //val SENSOR = tournamentBlocks.SENSOR withBE ::SensorBlockEntity byName "sensor"
    // commented out bc constantdust MESSED UP GRADLE!!!!!! TODO: uncomment when gradle fixed
    //val HINGE = tournamentBlocks.HINGE withBE ::HingeBlockEntity byName "sensor"

    fun register() {
        BLOCKENTITIES.applyAll()
    }

    private infix fun <T : BlockEntity> Set<RegistrySupplier<out Block>>.withBE(blockEntity: (BlockPos, BlockState) -> T) =
        Pair(this, blockEntity)

    private infix fun <T : BlockEntity> RegistrySupplier<out Block>.withBE(blockEntity: (BlockPos, BlockState) -> T) =
        Pair(setOf(this), blockEntity)

    private infix fun <T : BlockEntity> Block.withBE(blockEntity: (BlockPos, BlockState) -> T) = Pair(this, blockEntity)
}
