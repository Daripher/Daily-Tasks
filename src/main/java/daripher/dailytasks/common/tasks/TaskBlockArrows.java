package daripher.dailytasks.common.tasks;

import java.util.Iterator;

import com.google.gson.JsonObject;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.client.utils.GuiUtils;
import daripher.dailytasks.common.capability.ITask;
import daripher.dailytasks.common.capability.ITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class TaskBlockArrows implements ITask
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
		return "block_arrows";
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
		GuiUtils.drawItemStack(new ItemStack(Items.SHIELD), iconX, iconY);
	}
	
	@SubscribeEvent
	public static void onShieldBlock(LivingAttackEvent event)
	{
		if (!(event.getEntity() instanceof EntityPlayer))
			return;
		
		if (event.getEntity().world.isRemote)
			return;
		
		if (!(event.getSource().getImmediateSource() instanceof EntityArrow))
			return;
		
		EntityPlayer player = (EntityPlayer) event.getEntity();
		
		if (!event.getSource().isUnblockable() && player.isActiveItemStackBlocking())
		{
			Vec3d vec3d = event.getSource().getDamageLocation();
			
			if (vec3d != null)
			{
				Vec3d vec3d1 = player.getLook(1.0F);
				Vec3d vec3d2 = vec3d.subtractReverse(new Vec3d(player.posX, player.posY, player.posZ)).normalize();
				vec3d2 = new Vec3d(vec3d2.x, 0.0D, vec3d2.z);
				
				if (vec3d2.dotProduct(vec3d1) >= 0.0D)
				{
					return;
				}
			}
		}
		
		ITasks tasks = DailyTasksMod.playerTasks(player);
		Iterator<ITask> tasksIterator = tasks.getPlayerTasks().iterator();
		
		while (tasksIterator.hasNext())
		{
			ITask task = tasksIterator.next();
			
			if (tasks.getProgress(task) == task.getMaxProgress())
				continue;
			
			if (task instanceof TaskBlockArrows)
			{
				tasks.makeProgress(task, 1, player);
			}
		}
	}
}
