package org.valkyrienskies.tournament.registry

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

interface RegistrySupplier<T> {

    val name: String
    fun get(): T

}