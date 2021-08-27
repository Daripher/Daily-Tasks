package daripher.dailytasks;

import daripher.dailytasks.common.capability.ITask;
import daripher.dailytasks.common.capability.ITasks;
import daripher.dailytasks.common.capability.Tasks;
import daripher.dailytasks.common.capability.TasksProvider;
import daripher.dailytasks.common.config.Config;
import daripher.dailytasks.common.network.ClaimRewardMessage;
import daripher.dailytasks.common.network.MakeProgressMessage;
import daripher.dailytasks.common.network.SyncTasksMessage;
import daripher.dailytasks.common.proxy.CommonProxy;
import daripher.dailytasks.common.tasks.TaskBlockArrows;
import daripher.dailytasks.common.tasks.TaskBlockDamage;
import daripher.dailytasks.common.tasks.TaskBreakBlocks;
import daripher.dailytasks.common.tasks.TaskBreedAnimals;
import daripher.dailytasks.common.tasks.TaskCraftItems;
import daripher.dailytasks.common.tasks.TaskDealDamage;
import daripher.dailytasks.common.tasks.TaskEatFood;
import daripher.dailytasks.common.tasks.TaskGatherItems;
import daripher.dailytasks.common.tasks.TaskKillMobs;
import daripher.dailytasks.common.tasks.TaskMilkCow;
import daripher.dailytasks.common.tasks.TaskMountTravelDistance;
import daripher.dailytasks.common.tasks.TaskSailDistance;
import daripher.dailytasks.common.tasks.TaskTakeDamage;
import daripher.dailytasks.common.tasks.TaskTravelDistance;
import daripher.dailytasks.common.tasks.TaskUseItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = DailyTasksMod.MODID, name = DailyTasksMod.NAME, version = DailyTasksMod.VERSION)
public class DailyTasksMod
{
	public static final String MODID = "dailytasks";
	public static final String NAME = "Daily Tasks";
	public static final String VERSION = "1.0";
	public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(DailyTasksMod.MODID);
	public static final boolean DEBUG = false;
	
	@SidedProxy(clientSide = "daripher.dailytasks.client.proxy.ClientProxy", serverSide = "daripher.dailytasks.common.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static ITasks playerTasks(EntityPlayer player)
	{
		return player.getCapability(TasksProvider.PLAYER_TASKS, null);
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ITask.TASK_TYPES.put("break_blocks", TaskBreakBlocks.class);
		ITask.TASK_TYPES.put("breed_animals", TaskBreedAnimals.class);
		ITask.TASK_TYPES.put("craft_items", TaskCraftItems.class);
		ITask.TASK_TYPES.put("deal_damage", TaskDealDamage.class);
		ITask.TASK_TYPES.put("gather_items", TaskGatherItems.class);
		ITask.TASK_TYPES.put("kill_mobs", TaskKillMobs.class);
		ITask.TASK_TYPES.put("milk_cow", TaskMilkCow.class);
		ITask.TASK_TYPES.put("take_damage", TaskTakeDamage.class);
		ITask.TASK_TYPES.put("travel_distance", TaskTravelDistance.class);
		ITask.TASK_TYPES.put("mount_travel_distance", TaskMountTravelDistance.class);
		ITask.TASK_TYPES.put("sail_distance", TaskSailDistance.class);
		ITask.TASK_TYPES.put("eat_food", TaskEatFood.class);
		ITask.TASK_TYPES.put("use_items", TaskUseItems.class);
		ITask.TASK_TYPES.put("block_damage", TaskBlockDamage.class);
		ITask.TASK_TYPES.put("block_arrows", TaskBlockArrows.class);
		Config.init(event);
		proxy.preInit();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		NETWORK_WRAPPER.registerMessage(SyncTasksMessage.Handler.class, SyncTasksMessage.class, 0, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(ClaimRewardMessage.Handler.class, ClaimRewardMessage.class, 1, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(MakeProgressMessage.Handler.class, MakeProgressMessage.class, 2, Side.SERVER);
		CapabilityManager.INSTANCE.register(ITasks.class, new Tasks.Storage(), Tasks::new);
	}
}
