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
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class TaskKillMobs implements ITask
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
		return "kill_mobs";
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
	public static void onMobKill(LivingDeathEvent event)
	{
		if (event.getEntityLiving().getAttackingEntity() == null)
			return;
		
		if (event.getEntityLiving().world.isRemote)
			return;
		
		if (!(event.getEntityLiving().getAttackingEntity() instanceof EntityPlayer))
			return;
		
		EntityPlayer killer = (EntityPlayer) event.getEntityLiving().getAttackingEntity();
		ITasks tasks = DailyTasksMod.playerTasks(killer);
		Iterator<ITask> tasksIterator = tasks.getPlayerTasks().iterator();
		
		while (tasksIterator.hasNext())
		{
			ITask task = tasksIterator.next();
			
			if (tasks.getProgress(task) == task.getMaxProgress())
				continue;
			
			if (task instanceof TaskKillMobs)
			{
				tasks.makeProgress(task, 1.0d, killer);
			}
		}
	}
}
