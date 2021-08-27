package daripher.dailytasks.common.capability;

import javax.annotation.Nullable;

import daripher.dailytasks.DailyTasksMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class TasksProvider implements ICapabilitySerializable<NBTBase>
{
	@CapabilityInject(ITasks.class)
	public static final Capability<ITasks> PLAYER_TASKS = null;
	private final ITasks data;
	
	public TasksProvider()
	{
		this.data = new Tasks();
	}
	
	@Override
	public <T> T getCapability(@Nullable Capability<T> capability, EnumFacing facing)
	{
		return capability == PLAYER_TASKS ? PLAYER_TASKS.<T>cast(this.data) : null;
	}
	
	@Override
	public NBTBase serializeNBT()
	{
		return PLAYER_TASKS.writeNBT(data, null);
	}
	
	@Override
	public void deserializeNBT(NBTBase par0)
	{
		PLAYER_TASKS.readNBT(data, null, par0);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == PLAYER_TASKS;
	}
	
	@SubscribeEvent
	public static void onCapabilityAttachEntity(AttachCapabilitiesEvent<Entity> event)
	{
		if (event.getObject() instanceof EntityPlayer)
		{
			event.addCapability(new ResourceLocation(DailyTasksMod.MODID, "tasks"), new TasksProvider());
		}
	}
}
