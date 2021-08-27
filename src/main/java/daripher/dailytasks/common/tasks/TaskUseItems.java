package daripher.dailytasks.common.tasks;

import java.util.Iterator;

import com.google.gson.JsonObject;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.client.utils.GuiUtils;
import daripher.dailytasks.common.capability.ITask;
import daripher.dailytasks.common.capability.ITasks;
import daripher.dailytasks.common.utils.JsonUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@EventBusSubscriber
public class TaskUseItems implements ITask
{
	public ItemStack item;
	private int amount;
	private long color;
	
	@Override
	public String getType()
	{
		return "use_items";
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
	public static void onEntityUseItem(LivingEntityUseItemEvent.Finish event)
	{
		if (!(event.getEntity() instanceof EntityPlayer))
			return;
				
		EntityPlayer player = (EntityPlayer) event.getEntity();
		ITasks tasks = DailyTasksMod.playerTasks(player);
		Iterator<ITask> tasksIterator = tasks.getPlayerTasks().iterator();
		
		while (tasksIterator.hasNext())
		{
			ITask task = tasksIterator.next();
			
			if (tasks.getProgress(task) == task.getMaxProgress())
				continue;
			
			if (task instanceof TaskUseItems)
			{
				ItemStack result = event.getItem().copy();
				result.setCount(1);
				
				if (ItemStack.areItemStacksEqualUsingNBTShareTag(((TaskUseItems) task).item, result))
				{
					tasks.makeProgress(task, 1, player);
				}
			}
		}
	}
}
