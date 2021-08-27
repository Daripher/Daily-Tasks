package daripher.dailytasks.common.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.common.capability.ITask;
import daripher.dailytasks.common.utils.JsonUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config
{
	public static final List<ItemStack> POSSIBLE_REWARDS = new ArrayList<>();
	public static long tasksGenerationDelay;
	public static int tasksPerGeneration;
	private static File configFile;
	
	public static void init(FMLPreInitializationEvent event)
	{
		try
		{
			configFile = new File(event.getSuggestedConfigurationFile().getParentFile(), DailyTasksMod.MODID + ".json");
			
			if (!configFile.exists())
			{
				generateConfig();
			}
			
			readConfig();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void readConfig() throws Exception
	{
		JsonParser parser = new JsonParser();
		JsonObject jsonObj = (JsonObject) parser.parse(new JsonReader(new FileReader(configFile)));
		JsonArray jsonTasksArray = jsonObj.get("tasks").getAsJsonArray();
		JsonArray jsonRewardsArray = jsonObj.get("rewards").getAsJsonArray();
		jsonRewardsArray.forEach(element -> POSSIBLE_REWARDS.add(JsonUtils.readItemStack((JsonObject) element)));
		jsonTasksArray.forEach(element ->
		{
			try
			{
				ITask.registerTask(ITask.readTask((JsonObject) element));
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		});
		
		tasksPerGeneration = jsonObj.get("tasks_per_generation").getAsInt();
		tasksGenerationDelay = jsonObj.get("tasks_generation_delay").getAsLong();
	}
	
	private static void generateConfig() throws Exception
	{
		FileWriter fw = new FileWriter(configFile);
		fw.write("{\r\n" + 
				"   \"rewards\":[\r\n" + 
				"      {\r\n" + 
				"         \"_comment\":\"Example of enchanted item\",\r\n" + 
				"         \"item\":\"minecraft:diamond_helmet\",\r\n" + 
				"         \"metadata\":0,\r\n" + 
				"         \"amount\":1,\r\n" + 
				"         \"nbt\":{\r\n" + 
				"            \"ench\":[\r\n" + 
				"               {\r\n" + 
				"                  \"id\":1,\r\n" + 
				"                  \"lvl\":1\r\n" + 
				"               }\r\n" + 
				"            ]\r\n" + 
				"         }\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"_comment\":\"Example of spawn egg data\",\r\n" + 
				"         \"item\":\"minecraft:spawn_egg\",\r\n" + 
				"         \"metadata\":0,\r\n" + 
				"         \"amount\":10,\r\n" + 
				"         \"nbt\":{\r\n" + 
				"            \"EntityTag\":{\r\n" + 
				"               \"id\":\"minecraft:chicken\"\r\n" + 
				"            }\r\n" + 
				"         }\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"_comment\":\"Example of unbreakable tool\",\r\n" + 
				"         \"item\":\"minecraft:diamond_pickaxe\",\r\n" + 
				"         \"metadata\":0,\r\n" + 
				"         \"amount\":1,\r\n" + 
				"         \"nbt\":{\r\n" + 
				"            \"Unbreakable\":1\r\n" + 
				"         }\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"_comment\":\"Example of item with bonus attributes\",\r\n" + 
				"         \"item\":\"minecraft:diamond_chestplate\",\r\n" + 
				"         \"metadata\":0,\r\n" + 
				"         \"amount\":1,\r\n" + 
				"         \"nbt\":{\r\n" + 
				"            \"AttributeModifiers\":[\r\n" + 
				"               {\r\n" + 
				"                  \"AttributeName\":\"generic.maxHealth\",\r\n" + 
				"                  \"Name\":\"generic.maxHealth\",\r\n" + 
				"                  \"Slot\":\"chest\",\r\n" + 
				"                  \"Amount\":20,\r\n" + 
				"                  \"Operation\":0,\r\n" + 
				"                  \"UUIDMost\":71978,\r\n" + 
				"                  \"UUIDLeast\":164291\r\n" + 
				"               }\r\n" + 
				"            ]\r\n" + 
				"         }\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"tasks\":[\r\n" + 
				"      {\r\n" + 
				"         \"type\":\"deal_damage\",\r\n" + 
				"         \"amount\":20.0,\r\n" + 
				"         \"color\":\"#f78b4d\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"type\":\"break_blocks\",\r\n" + 
				"         \"amount\":7,\r\n" + 
				"         \"color\":\"#f78b4d\",\r\n" + 
				"         \"block\":\"grass\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"type\":\"gather_items\",\r\n" + 
				"         \"amount\":3,\r\n" + 
				"         \"color\":\"#f78b4d\",\r\n" + 
				"         \"item\":\"minecraft:apple\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"type\":\"breed_animals\",\r\n" + 
				"         \"color\":\"#f78b4d\",\r\n" + 
				"         \"amount\":5\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"type\":\"craft_items\",\r\n" + 
				"         \"color\":\"#f78b4d\",\r\n" + 
				"         \"amount\":10,\r\n" + 
				"         \"item\":\"stick\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"type\":\"kill_mobs\",\r\n" + 
				"         \"color\":\"#f78b4d\",\r\n" + 
				"         \"amount\":10\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"type\":\"milk_cow\",\r\n" + 
				"         \"color\":\"#f78b4d\",\r\n" + 
				"         \"amount\":4\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"type\":\"take_damage\",\r\n" + 
				"         \"amount\":10.0,\r\n" + 
				"         \"color\":\"#f78b4d\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"type\":\"travel_distance\",\r\n" + 
				"         \"amount\":100.0,\r\n" + 
				"         \"color\":\"#f78b4d\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"type\":\"mount_travel_distance\",\r\n" + 
				"         \"amount\":100.0,\r\n" + 
				"         \"color\":\"#f78b4d\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"type\":\"sail_distance\",\r\n" + 
				"         \"amount\":100.0,\r\n" + 
				"         \"color\":\"#f78b4d\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"type\":\"eat_food\",\r\n" + 
				"         \"amount\":2,\r\n" + 
				"         \"color\":\"#f78b4d\",\r\n" + 
				"         \"item\":\"minecraft:apple\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"type\":\"use_items\",\r\n" + 
				"         \"amount\":5,\r\n" + 
				"         \"color\":\"#f78b4d\",\r\n" + 
				"         \"item\":\"minecraft:bow\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"type\":\"block_damage\",\r\n" + 
				"         \"amount\":25,\r\n" + 
				"         \"color\":\"#f78b4d\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"type\":\"block_arrows\",\r\n" + 
				"         \"amount\":5,\r\n" + 
				"         \"color\":\"#f78b4d\"\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"tasks_per_generation\":4,\r\n" + 
				"   \"tasks_generation_delay\":86400\r\n" + 
				"}");
		fw.close();
	}
}
