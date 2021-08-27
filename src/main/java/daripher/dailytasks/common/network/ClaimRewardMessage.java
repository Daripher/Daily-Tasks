package daripher.dailytasks.common.network;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.common.capability.ITasks;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClaimRewardMessage implements IMessage
{
	private int index;
	
	public ClaimRewardMessage()
	{
	}
	
	public ClaimRewardMessage(int index)
	{
		this.index = index;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		index = buf.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(index);
	}
	
	public static class Handler implements IMessageHandler<ClaimRewardMessage, IMessage>
	{
		@Override
		public IMessage onMessage(ClaimRewardMessage message, MessageContext ctx)
		{
			ITasks tasks = DailyTasksMod.playerTasks(ctx.getServerHandler().player);
			tasks.claimReward(ctx.getServerHandler().player, message.index);
			return null;
		}
	}
}
