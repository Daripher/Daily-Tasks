package daripher.dailytasks.common.tasks;

import java.util.Iterator;

import com.google.gson.JsonObject;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.client.utils.GuiUtils;
import daripher.dailytasks.common.capability.ITask;
import daripher.dailytasks.common.capability.ITasks;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class TaskMilkCow implements ITask
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
		return "milk_cow";
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
		GuiUtils.drawItemStack(new ItemStack(Items.MILK_BUCKET), iconX, iconY);
	}
	
	@SubscribeEvent
	public static void onPlayerMilkCow(PlayerInteractEvent.EntityInteract event)
	{
		if (event.getWorld().isRemote)
			return;
		
		if (event.getEntityPlayer().isCreative())
			return;
		
		if (event.getTarget().getClass() != EntityCow.class)
			return;
		
		if (event.getEntityPlayer().getHeldItem(event.getHand()).getItem() != Items.BUCKET)
			return;
		
		ITasks tasks = DailyTasksMod.playerTasks(event.getEntityPlayer());
		Iterator<ITask> tasksIterator = tasks.getPlayerTasks().iterator();
		
		while (tasksIterator.hasNext())
		{
			ITask task = tasksIterator.next();
			
			if (tasks.getProgress(task) == task.getMaxProgress())
				continue;
			
			if (task instanceof TaskMilkCow)
			{
				tasks.makeProgress(task, 1.0d, event.getEntityPlayer());
			}
		}
	}
}
