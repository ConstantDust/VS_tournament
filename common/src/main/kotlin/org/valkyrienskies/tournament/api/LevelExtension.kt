package org.valkyrienskies.tournament.api

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Explosion.BlockInteraction
import org.joml.Vector3d

object LevelExtension {

    fun ServerLevel.explodeShip(level : ServerLevel, x : Double, y: Double, z: Double, radius: Float, interaction: Explosion.BlockInteraction) {
        level.explodeShip(level, Vector3d(x,y,z), radius, interaction)
    }

    fun ServerLevel.explodeShip(level: ServerLevel, pos: Vector3d, radius: Float, interaction: BlockInteraction) {
        level.explode(level, Helper3d.MaybeShipToWorldspace(level, pos), radius, interaction)
    }

    fun ServerLevel.explode(level: ServerLevel,pos: Vector3d,radius: Float,interaction: BlockInteraction) {
        level.explode(PrimedTnt(EntityType.TNT, level), pos.x, pos.y, pos.z, radius, interaction)
    }

}