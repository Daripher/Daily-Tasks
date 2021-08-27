package daripher.dailytasks.common.tasks;

import java.util.Iterator;

import com.google.gson.JsonObject;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.client.utils.GuiUtils;
import daripher.dailytasks.common.capability.ITask;
import daripher.dailytasks.common.capability.ITasks;
import daripher.dailytasks.common.utils.JsonUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber
public class TaskGatherItems implements ITask
{
	public ItemStack item;
	private int amount;
	private long color;
	
	@Override
	public String getType()
	{
		return "gather_items";
	}
	
	@Override
	public double getMaxProgress()
	{
		return amount;
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
		ResourceLocation itemId = new ResourceLocation(element.get("item").getAsString());
		int metadata = 0;
		
		if (element.has("metadata"))
			metadata = element.get("metadata").getAsInt();
		
		ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(itemId), 1, metadata);
		
		if (element.has("nbt"))
			stack.setTagCompound(JsonUtils.readNBTTag((JsonObject) element.get("nbt")));
		
		this.amount = element.get("amount").getAsInt();
		this.color = Long.parseLong(colorString, 16);
		this.item = stack;
	}
	
	@Override
	public void drawIcon(int iconX, int iconY)
	{
		GuiUtils.drawItemStack(new ItemStack(item.getItem()), iconX, iconY);		
	}
	
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event)
	{
		if (event.side == Side.CLIENT)
			return;
		
		if (event.phase == Phase.END)
			return;
		
		ITasks tasks = DailyTasksMod.playerTasks(event.player);
		Iterator<ITask> tasksIterator = tasks.getPlayerTasks().iterator();
		
		while (tasksIterator.hasNext())
		{
			ITask task = tasksIterator.next();
			
			if (tasks.getProgress(task) == task.getMaxProgress())
				continue;
			
			if (task instanceof TaskGatherItems)
			{
				TaskGatherItems gatherItemsTask = (TaskGatherItems) task;
				int amountGathered = 0;
				
				for (int i = 0; i < event.player.inventory.getSizeInventory(); i++)
				{
					ItemStack inventoryStack = event.player.inventory.getStackInSlot(i).copy();
					ItemStack inventoryStackCopy = inventoryStack.copy();
					inventoryStackCopy.setCount(1);
					
					if (ItemStack.areItemStacksEqualUsingNBTShareTag(gatherItemsTask.item, inventoryStackCopy))
					{
						amountGathered += inventoryStack.getCount();
					}
				}
				
				tasks.setProgress(task, amountGathered, event.player);
			}
		}
	}
}