package daripher.dailytasks.common.tasks;

import java.util.Iterator;

import com.google.gson.JsonObject;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.common.capability.ITask;
import daripher.dailytasks.common.capability.ITasks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class TaskTakeDamage implements ITask
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
		return "take_damage";
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
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(DailyTasksMod.MODID, "textures/gui/icons/" + getType() + ".png"));
		Gui.drawScaledCustomSizeModalRect(iconX, iconY, 0f, 0f, 1, 1, 16, 16, 1f, 1f);
	}
	
	@SubscribeEvent
	public static void onMobHurt(LivingHurtEvent event)
	{
		if (event.getEntityLiving().world.isRemote)
			return;
		
		if (!(event.getEntity() instanceof EntityPlayer))
			return;
		
		EntityPlayer attacker = (EntityPlayer) event.getEntity();
		ITasks tasks = DailyTasksMod.playerTasks(attacker);
		Iterator<ITask> tasksIterator = tasks.getPlayerTasks().iterator();
		
		while (tasksIterator.hasNext())
		{
			ITask task = tasksIterator.next();
			
			if (tasks.getProgress(task) == task.getMaxProgress())
				continue;
			
			if (task instanceof TaskTakeDamage)
			{
				tasks.makeProgress(task, event.getAmount(), attacker);
			}
		}
	}
}
