package daripher.dailytasks.common.tasks;

import java.util.Iterator;

import com.google.gson.JsonObject;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.client.utils.GuiUtils;
import daripher.dailytasks.common.capability.ITask;
import daripher.dailytasks.common.capability.ITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class TaskDealDamage implements ITask
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
		return "deal_damage";
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
		GuiUtils.drawItemStack(new ItemStack(Items.IRON_SWORD), iconX, iconY);
	}
	
	@SubscribeEvent
	public static void onMobHurt(LivingHurtEvent event)
	{
		if (event.getSource().getTrueSource() == null)
			return;
		
		if (event.getEntityLiving().world.isRemote)
			return;
		
		if (!(event.getSource().getTrueSource() instanceof EntityPlayer))
			return;
		
		EntityPlayer attacker = (EntityPlayer) event.getSource().getTrueSource();
		ITasks tasks = DailyTasksMod.playerTasks(attacker);
		Iterator<ITask> tasksIterator = tasks.getPlayerTasks().iterator();
		
		while (tasksIterator.hasNext())
		{
			ITask task = tasksIterator.next();
			
			if (tasks.getProgress(task) == task.getMaxProgress())
				continue;
			
			if (task instanceof TaskDealDamage)
			{
				tasks.makeProgress(task, event.getAmount(), attacker);
			}
		}
	}
}
