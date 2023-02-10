package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3d
import org.valkyrienskies.mod.util.getVector3d
import org.valkyrienskies.mod.util.putVector3d
import org.valkyrienskies.physics_api.ConstraintId
import org.valkyrienskies.tournament.api.Quadruple
import org.valkyrienskies.tournament.tournamentBlockEntities

class RopeHookBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(tournamentBlockEntities.ROPE_HOOK.get(), pos, state) {

    private var ropeId: ConstraintId? = null
    private var MainPos: Vector3d? = null
    private var OtherPos:Vector3d? = null

    private var maxLen: Double = 0.0

    private var set : Boolean = false

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    fun markUpdated() {
        this.setChanged()
        getLevel()!!.sendBlockUpdated(this.blockPos, blockState, blockState, 3)
    }

    override fun getUpdateTag(): CompoundTag {
        var tag = super.getUpdateTag()
        tag.putBoolean("set", set)
        tag.putDouble("maxLen", maxLen)
        tag.putInt("ropeID", ropeId!!)
        tag.putVector3d("mainPos", MainPos!!)
        tag.putVector3d("otherPos", OtherPos!!)
        return tag
    }

    override fun load(tag: CompoundTag) {
        if (tag.contains("set")) {
            set = tag.getBoolean("set")
            ropeId = tag.getInt("ropeID")
            MainPos = tag.getVector3d("mainPos")
            OtherPos = tag.getVector3d("otherPos")
            maxLen = tag.getDouble("maxLen")
        }
        else {
            println("Could not load NBT of RopeHook block entity! Will set to default")
        }
    }

    fun getStuff() : Quadruple<ConstraintId,Vector3d,Vector3d,Double> {
        return Quadruple(ropeId!!,MainPos!!,OtherPos!!,maxLen)
    }

    fun isSet() : Boolean {
        return this.set
    }

    fun writeAndSet(a : ConstraintId?,b : Vector3d?,c : Vector3d?,d : Double?) {
        writeStuff(a,b,c,d)
        this.set = true
    }

    fun writeStuff(a : ConstraintId?,b : Vector3d?,c : Vector3d?,d : Double?) {
        this.ropeId = a
        this.MainPos = b
        this.OtherPos = c
        this.maxLen = d!!
    }

    fun writeLen(d : Double) {
        this.maxLen = d
    }

    fun getId() : ConstraintId {
        return this.ropeId!!
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)

        if(!isSet()) {return}

        tag.putBoolean("set", set)
        tag.putInt("ropeID", ropeId!!)
        tag.putVector3d("mainPos", MainPos!!)
        tag.putVector3d("otherPos", OtherPos!!)
        tag.putDouble("maxLen", maxLen)
    }
}