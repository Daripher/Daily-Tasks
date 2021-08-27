package daripher.dailytasks.common.tasks;

import java.util.Iterator;

import com.google.gson.JsonObject;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.common.capability.ITask;
import daripher.dailytasks.common.capability.ITasks;
import daripher.dailytasks.common.network.MakeProgressMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber
public class TaskTravelDistance implements ITask
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
		return "travel_distance";
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
	public static void onPlayerPostTick(PlayerTickEvent event)
	{
		if (event.side == Side.SERVER)
			return;
		
		if (event.phase == Phase.END)
			return;
		
		if (event.player.isRiding())
			return;
		
		ITasks tasks = DailyTasksMod.playerTasks(event.player);
		Iterator<ITask> tasksIterator = tasks.getPlayerTasks().iterator();
		
		while (tasksIterator.hasNext())
		{
			ITask task = tasksIterator.next();
			
			if (tasks.getProgress(task) == task.getMaxProgress())
				continue;
			
			if (task instanceof TaskTravelDistance)
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
