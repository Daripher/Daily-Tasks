package daripher.dailytasks.common.capability;

import java.time.Instant;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface ITasks
{
	List<ITask> getPlayerTasks();

	int getCompletionStreak();

	NBTTagCompound write();

	void read(NBTTagCompound nbt);

	void makeProgress(ITask task, double amount, EntityPlayer player);
	
	void addTask(ITask task);

	double getProgress(ITask task);

	void clear();

	void generateTasksAndRewards();

	long getTimeSinceLastGeneration();

	Instant getLastCompletionTime();

	void setProgress(ITask task, double progress, EntityPlayer player);

	boolean tasksCompleted();

	int getCompletedTasks();

	void setCompletionStreak(int i);

	List<ItemStack> getRewards();

	void claimReward(EntityPlayerMP player, int index);
}
