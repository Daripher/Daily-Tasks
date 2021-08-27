package daripher.dailytasks.common.network;

import javax.annotation.Nonnull;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.common.capability.ITasks;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncTasksMessage implements IMessage
{
	private NBTTagCompound nbt;
	
	public SyncTasksMessage()
	{
	}
	
	public SyncTasksMessage(@Nonnull NBTTagCompound nbt)
	{
		this.nbt = nbt;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		nbt = ByteBufUtils.readTag(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, nbt);
	}
	
	public static class Handler implements IMessageHandler<SyncTasksMessage, IMessage>
	{
		@Override
		public IMessage onMessage(SyncTasksMessage message, MessageContext ctx)
		{
			if (message.nbt != null && DailyTasksMod.proxy.getClientPlayer() != null)
			{
				ITasks tasks = DailyTasksMod.playerTasks(DailyTasksMod.proxy.getClientPlayer());
				tasks.read(message.nbt);
				DailyTasksMod.proxy.updateTasksGuiState();
			}
			
			return null;
		}
	}
}
