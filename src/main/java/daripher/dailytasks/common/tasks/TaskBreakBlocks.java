package daripher.dailytasks.common.tasks;

import java.util.Iterator;

import com.google.gson.JsonObject;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.client.utils.GuiUtils;
import daripher.dailytasks.common.capability.ITask;
import daripher.dailytasks.common.capability.ITasks;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@EventBusSubscriber
public class TaskBreakBlocks implements ITask
{
	public Block blockType;
	public int metadata;
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
		return "break_blocks";
	}
	
	@Override
	public long getGuiColor()
	{
		return color;
	}
	
	@Override
	public void readFromJson(JsonObject element)
	{
		ResourceLocation blockId = new ResourceLocation(element.get("block").getAsString());
		String colorString = element.get("color").getAsString().substring(1).toLowerCase();
		this.blockType = ForgeRegistries.BLOCKS.getValue(blockId);
		this.amount = element.get("amount").getAsInt();
		this.color = Long.parseLong(colorString, 16);
		
		if (element.has("metadata"))
			this.metadata = element.get("metadata").getAsInt();
	}
	
	@Override
	public void drawIcon(int iconX, int iconY)
	{
		GuiUtils.drawItemStack(new ItemStack(blockType), iconX, iconY);
	}
	
	@SubscribeEvent
	public static void onPlayerBreakBlock(BlockEvent.BreakEvent event)
	{
		ITasks tasks = DailyTasksMod.playerTasks(event.getPlayer());
		Iterator<ITask> tasksIterator = tasks.getPlayerTasks().iterator();
		
		while (tasksIterator.hasNext())
		{
			ITask task = tasksIterator.next();
			
			if (tasks.getProgress(task) == task.getMaxProgress())
				continue;
			
			if (task instanceof TaskBreakBlocks)
			{
				TaskBreakBlocks breakBlockTask = (TaskBreakBlocks) task;
				
				if (breakBlockTask.blockType == event.getState().getBlock()
						&& breakBlockTask.metadata == event.getState().getBlock().getMetaFromState(event.getState()))
				{
					tasks.makeProgress(task, 1.0d, event.getPlayer());
				}
			}
		}
	}
}
