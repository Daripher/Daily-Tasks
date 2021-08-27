package daripher.dailytasks.common.network;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.common.capability.ITask;
import daripher.dailytasks.common.capability.ITasks;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MakeProgressMessage implements IMessage
{
	private ITask task;
	private double amount;
	
	public MakeProgressMessage()
	{
	}
	
	public MakeProgressMessage(ITask task, double amount)
	{
		this.task = task;
		this.amount = amount;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		task = ITask.getById(buf.readInt());
		amount = buf.readDouble();
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(ITask.getId(task));
		buf.writeDouble(amount);
	}
	
	public static class Handler implements IMessageHandler<MakeProgressMessage, IMessage>
	{
		@Override
		public IMessage onMessage(MakeProgressMessage message, MessageContext ctx)
		{
			ITasks tasks = DailyTasksMod.playerTasks(ctx.getServerHandler().player);
			tasks.makeProgress(message.task, message.amount, ctx.getServerHandler().player);
			return null;
		}
	}
}
