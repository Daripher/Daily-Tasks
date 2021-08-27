package daripher.dailytasks.common.capability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ITask
{
	static final List<ITask> TASK_LIST = new ArrayList<>();
	static final Map<String, Class<? extends ITask>> TASK_TYPES = new HashMap<>();
	
	String getType();
	
	double getMaxProgress();
	
	long getGuiColor();
	
	void readFromJson(JsonObject element);
	
	@SideOnly(Side.CLIENT)
	void drawIcon(int iconX, int iconY);
	
	default NBTTagCompound write(NBTTagCompound nbt)
	{
		nbt.setInteger("Id", ITask.getId(this));
		return nbt;
	}
	
	static ITask read(NBTTagCompound tag)
	{
		return TASK_LIST.get(tag.getInteger("Id"));
	}
	
	static int getId(ITask task)
	{
		return TASK_LIST.indexOf(task);
	}
	
	static ITask getById(int id)
	{
		return TASK_LIST.get(id);
	}
	
	static void registerTask(ITask task)
	{
		TASK_LIST.add(task);
	}
	
	static ITask readTask(JsonObject element) throws InstantiationException, IllegalAccessException
	{
		ITask task = TASK_TYPES.get(element.get("type").getAsString()).newInstance();
		task.readFromJson(element);
		return task;
	}
}
