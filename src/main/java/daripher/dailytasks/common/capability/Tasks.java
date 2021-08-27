package daripher.dailytasks.common.capability;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.common.config.Config;
import daripher.dailytasks.common.network.SyncTasksMessage;
import daripher.dailytasks.common.utils.NBTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@EventBusSubscriber
public class Tasks implements ITasks
{
	private Map<ITask, Double> progress = new HashMap<>();
	private List<ITask> tasks = new ArrayList<>();
	private List<ItemStack> rewards = new ArrayList<>();
	private Instant lastCompletionDate = Instant.now().minusSeconds(Config.tasksGenerationDelay);
	private int completionStreak;
	
	@Override
	public List<ITask> getPlayerTasks()
	{
		return tasks;
	}
	
	@Override
	public int getCompletionStreak()
	{
		return completionStreak;
	}
	
	@Override
	public void setCompletionStreak(int streak)
	{
		completionStreak = streak;
	}
	
	@Override
	public void makeProgress(ITask task, double amount, EntityPlayer player)
	{
		if (amount == 0)
			return;
		
		if (getProgress(task) == task.getMaxProgress())
			return;
		
		progress.put(task, Math.min(getProgress(task) + amount, task.getMaxProgress()));
		sync(player);
	}
	
	@Override
	public boolean tasksCompleted()
	{
		if (tasks.isEmpty())
			return false;
		
		for (int i = 0; i < tasks.size(); i++)
		{
			if (getProgress(tasks.get(i)) < tasks.get(i).getMaxProgress())
			{
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public int getCompletedTasks()
	{
		int result = 0;
		
		for (int i = 0; i < tasks.size(); i++)
		{
			if (getProgress(tasks.get(i)) == tasks.get(i).getMaxProgress())
			{
				result++;
			}
		}
		
		return result;
	}
	
	@Override
	public void clear()
	{
		tasks.clear();
		progress.clear();
		rewards.clear();
	}
	
	@Override
	public void addTask(ITask task)
	{
		tasks.add(task);
	}
	
	@Override
	public double getProgress(ITask task)
	{
		return progress.containsKey(task) ? progress.get(task) : 0.0d;
	}
	
	@Override
	public void generateTasksAndRewards()
	{
		List<ITask> tempTaskList = new ArrayList<>();
		ITask.TASK_LIST.forEach(taskType -> tempTaskList.add(taskType));
		List<ItemStack> tempRewardsList = new ArrayList<>();
		Config.POSSIBLE_REWARDS.forEach(stack -> tempRewardsList.add(stack.copy()));
		Random rng = new Random();
		
		while (!tempTaskList.isEmpty() && ITask.TASK_LIST.size() - tempTaskList.size() < Config.tasksPerGeneration)
		{
			addTask(tempTaskList.remove(rng.nextInt(tempTaskList.size())));
		}
		
		while (!tempRewardsList.isEmpty() && Config.POSSIBLE_REWARDS.size() - tempRewardsList.size() < 3)
		{
			rewards.add(tempRewardsList.remove(rng.nextInt(tempRewardsList.size())));
		}
	}
	
	@Override
	public long getTimeSinceLastGeneration()
	{
		return Duration.between(Instant.now(), lastCompletionDate).abs().getSeconds();
	}
	
	@Override
	public Instant getLastCompletionTime()
	{
		return lastCompletionDate;
	}
	
	@Override
	public void setProgress(ITask task, double progress, EntityPlayer player)
	{
		if (progress == getProgress(task))
			return;
		
		this.progress.put(task, 0.0d);
		makeProgress(task, progress, player);
	}
	
	@Override
	public List<ItemStack> getRewards()
	{
		return rewards;
	}
	
	@Override
	public void claimReward(EntityPlayerMP player, int index)
	{
		completionStreak++;
		
		for (int i = 0; i < completionStreak; i++)
		{
			player.addItemStackToInventory(rewards.get(index));
		}
		
		clear();
		sync(player);
	}
	
	@Override
	public NBTTagCompound write()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList tasksTagList = new NBTTagList();
		NBTTagList progressTagList = new NBTTagList();
		NBTTagList rewardsTagList = new NBTTagList();
		tasks.forEach(task -> tasksTagList.appendTag(task.write(new NBTTagCompound())));
		progress.forEach((task, progress) ->
		{
			NBTTagCompound progressTag = new NBTTagCompound();
			progressTag.setInteger("Id", ITask.getId(task));
			progressTag.setDouble("Progress", progress);
			progressTagList.appendTag(progressTag);
		});
		rewards.forEach(stack ->
		{
			rewardsTagList.appendTag(stack.writeToNBT(new NBTTagCompound()));
		});
		nbt.setTag("Tasks", tasksTagList);
		nbt.setTag("Progress", progressTagList);
		nbt.setInteger("Streak", completionStreak);
		nbt.setTag("CompletionDate", NBTUtils.writeDate(lastCompletionDate));
		nbt.setTag("Rewards", rewardsTagList);
		return nbt;
	}
	
	@Override
	public void read(NBTTagCompound nbt)
	{
		tasks.clear();
		rewards.clear();
		
		try
		{
			NBTTagList tasksTagList = nbt.getTagList("Tasks", new NBTTagCompound().getId());
			NBTTagList progressTagList = nbt.getTagList("Progress", new NBTTagCompound().getId());
			NBTTagList rewardsTagList = nbt.getTagList("Rewards", new NBTTagCompound().getId());
			tasksTagList.forEach(tag ->
			{
				ITask task = ITask.read((NBTTagCompound) tag);
				
				if (task != null)
					tasks.add(task);
			});
			progressTagList.forEach(tag ->
			{
				ITask task = ITask.getById(((NBTTagCompound) tag).getInteger("Id"));
				
				if (task != null)
					progress.put(task, ((NBTTagCompound) tag).getDouble("Progress"));
			});
			rewardsTagList.forEach(tag ->
			{
				rewards.add(new ItemStack((NBTTagCompound) tag));
			});
			completionStreak = nbt.getInteger("Streak");
			lastCompletionDate = NBTUtils.readDate((NBTTagCompound) nbt.getTag("CompletionDate"));
		}
		catch (Exception e)
		{
			if (DailyTasksMod.DEBUG)
			{
				e.printStackTrace();
			}
			
			clear();
		}
	}
	
	@SubscribeEvent
	public static void onEntityPlayerCloned(PlayerEvent.Clone event)
	{
		if (event.getEntityPlayer().world.isRemote)
			return;
		
		EntityPlayer clone = event.getEntityPlayer();
		EntityPlayer original = event.getOriginal();
		
		try
		{
			ITasks tasks1 = DailyTasksMod.playerTasks(clone);
			ITasks tasks2 = DailyTasksMod.playerTasks(original);
			tasks1.read(tasks2.write());
			sync(clone);
		}
		catch (Exception parE)
		{
		}
	}
	
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event)
	{
		if (event.player.world.isRemote)
			return;
		
		ITasks tasks = DailyTasksMod.playerTasks(event.player);
		
		if (tasks.getTimeSinceLastGeneration() >= Config.tasksGenerationDelay)
		{
			if (!tasks.getPlayerTasks().isEmpty() || tasks.getTimeSinceLastGeneration() >= Config.tasksGenerationDelay * 2)
			{
				tasks.setCompletionStreak(0);
			}
			
			tasks.clear();
			((Tasks) tasks).lastCompletionDate = Instant.now();
			tasks.generateTasksAndRewards();
			sync(event.player);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		if (event.player.world.isRemote)
			return;
		
		sync(event.player);
	}
	
	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event)
	{
		if (event.player.world.isRemote)
			return;
		
		sync(event.player);
	}
	
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if (event.player.world.isRemote)
			return;
		
		sync(event.player);
	}
	
	@SubscribeEvent
	public static void onPlayerJoinedWorld(EntityJoinWorldEvent event)
	{
		if (event.getEntity().world.isRemote)
			return;
		
		if (!(event.getEntity() instanceof EntityPlayer))
			return;
		
		sync((EntityPlayer) event.getEntity());
	}
	
	private static void sync(EntityPlayer player)
	{
		DailyTasksMod.NETWORK_WRAPPER.sendTo(new SyncTasksMessage(DailyTasksMod.playerTasks(player).write()), (EntityPlayerMP) player);
	}
	
	public static class Storage implements IStorage<ITasks>
	{
		@Override
		public NBTBase writeNBT(Capability<ITasks> tasksCapability, ITasks tasks, EnumFacing facing)
		{
			return tasks.write();
		}
		
		@Override
		public void readNBT(Capability<ITasks> tasksCapability, ITasks tasks, EnumFacing par2, NBTBase nbt)
		{
			tasks.read((NBTTagCompound) nbt);
		}
	}
}
