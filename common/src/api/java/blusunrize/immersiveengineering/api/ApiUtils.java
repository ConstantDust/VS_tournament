/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.Tournament.api;

import blusunrize.Tournament.api.utils.TagUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.JsonUtils;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static blusunrize.Tournament.api.IETags.getIngot;

public class ApiUtils
{
	/**
	 * Random instance for general use. The "usual" per-world random instance can have unexpected behavior with
	 * ghostloading (in some cases the seed is set directly), this instance does not have this problem.
	 */
	public static final Random RANDOM = new Random();

	public static JsonElement jsonSerializeFluidStack(FluidStack fluidStack)
	{
		if(fluidStack==null)
			return JsonNull.INSTANCE;
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("fluid", fluidStack.getFluid().getRegistryName().toString());
		jsonObject.addProperty("amount", fluidStack.getAmount());
		if(fluidStack.hasTag())
			jsonObject.addProperty("tag", fluidStack.getTag().toString());
		return jsonObject;
	}

	public static FluidStack jsonDeserializeFluidStack(JsonObject jsonObject)
	{
		Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(jsonObject, "fluid")));
		int amount = GsonHelper.getAsInt(jsonObject, "amount");
		FluidStack fluidStack = new FluidStack(fluid, amount);
		if(GsonHelper.isValidNode(jsonObject, "tag"))
			fluidStack.setTag(JsonUtils.readNBT(jsonObject, "tag"));
		return fluidStack;
	}

	public static Pair<ItemStack, Double> breakStackIntoPreciseIngots(RegistryAccess tags, ItemStack stack)
	{
		String[] keys = IEApi.prefixToIngotMap.keySet().toArray(new String[0]);
		String[] type = TagUtils.getMatchingPrefixAndRemaining(tags, stack, keys);
		if(type!=null)
		{
			Integer[] relation = IEApi.prefixToIngotMap.get(type[0]);
			if(relation!=null&&relation.length > 1)
			{
				double val = relation[0]/(double)relation[1];
				return Pair.of(IEApi.getPreferredTagStack(tags, TagUtils.createItemWrapper(getIngot(type[1]))), val);
			}
		}
		return null;
	}

	public static double getDim(Vec3 vec, int dim)
	{
		return dim==0?vec.x: (dim==1?vec.y: vec.z);
	}

	public static <T extends Comparable<T>> Map<T, Integer> sortMap(Map<T, Integer> map, boolean inverse)
	{
		TreeMap<T, Integer> sortedMap = new TreeMap<>(new ValueComparator<T>(map, inverse));
		sortedMap.putAll(map);
		return sortedMap;
	}

	//Based on net.minecraft.entity.EntityLivingBase.knockBack
	public static void knockbackNoSource(LivingEntity entity, double strength, double xRatio, double zRatio)
	{
		entity.hasImpulse = true;
		Vec3 motionOld = entity.getDeltaMovement();
		Vec3 toAdd = (new Vec3(xRatio, 0.0D, zRatio)).normalize().scale(strength);
		entity.setDeltaMovement(
				motionOld.x/2.0D-toAdd.x,
				entity.isOnGround()?Math.min(0.4D, motionOld.y/2.0D+strength): motionOld.y,
				motionOld.z/2.0D-toAdd.z);
	}

	public static void addFutureServerTask(Level world, Runnable task, boolean forceFuture)
	{
		LogicalSide side = world.isClientSide?LogicalSide.CLIENT: LogicalSide.SERVER;
		//TODO this sometimes causes NPEs?
		BlockableEventLoop<? super TickTask> tmp = LogicalSidedProvider.WORKQUEUE.get(side);
		if(forceFuture)
		{
			int tick;
			if(world.isClientSide)
				tick = 0;
			else
				tick = ((MinecraftServer)tmp).getTickCount();
			tmp.tell(new TickTask(tick, task));
		}
		else
			tmp.submitAsync(task);
	}

	public static void addFutureServerTask(Level world, Runnable task)
	{
		addFutureServerTask(world, task, false);
	}

	public static record ValueComparator<T extends Comparable<T>>(
			Map<T, Integer> base, boolean inverse
	) implements java.util.Comparator<T>
	{

		@Override
		public int compare(T s0, T s1)//Cant return equal to keys separate
		{
			int v0 = base.get(s0);
			int v1 = base.get(s1);
			int ret;
			if(v0 > v1)
				ret = -1;
			else if(v0 < v1)
				ret = 1;
			else
				ret = s0.compareTo(s1);
			return ret*(inverse?-1: 1);
		}
	}
}
