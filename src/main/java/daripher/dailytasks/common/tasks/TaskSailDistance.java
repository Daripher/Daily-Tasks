package daripher.dailytasks.common.tasks;

import java.util.Iterator;

import com.google.gson.JsonObject;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.client.utils.GuiUtils;
import daripher.dailytasks.common.capability.ITask;
import daripher.dailytasks.common.capability.ITasks;
import daripher.dailytasks.common.network.MakeProgressMessage;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber
public class TaskSailDistance implements ITask
{
	private int amount;
	private long color;
	
	@Override
	public double getMaxProgress()
	{
		return amount;
	}
	
	@Override
	public String getType()
	{
		return "sail_distance";
	}
	
	@Override
	public long getGuiColor()
	{
		return color;
	}
	
	@Override
	public void readFromJson(JsonObject element)
	{
		String colorString = element.get("color").getAsString().substring(1).toLowerCase();
		this.amount = element.get("amount").getAsInt();
		this.color = Long.parseLong(colorString, 16);
	}
	
	@Override
	public void drawIcon(int iconX, int iconY)
	{
		GuiUtils.drawItemStack(new ItemStack(Items.BOAT), iconX, iconY);
	}
	
	@SubscribeEvent
	public static void onPlayerPostTick(PlayerTickEvent event)
	{
		if (event.side == Side.SERVER)
			return;
		
		if (event.phase == Phase.END)
			return;
		
		if (!event.player.isRiding())
			return;
		
		if (!(event.player.getRidingEntity() instanceof EntityBoat))
			return;
		
		ITasks tasks = DailyTasksMod.playerTasks(event.player);
		Iterator<ITask> tasksIterator = tasks.getPlayerTasks().iterator();
		
		while (tasksIterator.hasNext())
		{
			ITask task = tasksIterator.next();
			
			if (tasks.getProgress(task) == task.getMaxProgress())
				continue;
			
			if (task instanceof TaskMountTravelDistance)
			{
				double deltaX = event.player.posX - event.player.prevPosX;
				double deltaY = event.player.posY - event.player.prevPosY;
				double deltaZ = event.player.posZ - event.player.prevPosZ;
				double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2) + Math.pow(deltaZ, 2));
				DailyTasksMod.NETWORK_WRAPPER.sendToServer(new MakeProgressMessage(task, distance));
			}
		}
	}
}
