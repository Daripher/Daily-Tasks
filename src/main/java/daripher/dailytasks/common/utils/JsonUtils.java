package daripher.dailytasks.common.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class JsonUtils
{	
	public static JsonObject writeNBTTag(NBTTagCompound nbt)
	{
		JsonParser parser = new JsonParser();
		return (JsonObject) parser.parse(nbt.toString());
	}
	
	public static NBTTagCompound readNBTTag(JsonObject jsonObj)
	{
		try
		{
			return JsonToNBT.getTagFromJson(jsonObj.toString());
		}
		catch (NBTException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static JsonObject writeItemStack(ItemStack stack)
	{
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("item", stack.getItem().getRegistryName().toString());
		jsonObj.addProperty("metadata", stack.getItem().getMetadata(stack));
		jsonObj.addProperty("amount", stack.getCount());
		
		if (stack.hasTagCompound())
			jsonObj.add("nbt", writeNBTTag(stack.getTagCompound()));
		
		return jsonObj;
	}
	
	public static ItemStack readItemStack(JsonObject jsonObj)
	{
		ResourceLocation itemId = new ResourceLocation(jsonObj.get("item").getAsString());
		Item item = ForgeRegistries.ITEMS.getValue(itemId);
		int metadata = 0;
		
		if (jsonObj.has("metadata"))
			metadata = jsonObj.get("metadata").getAsInt();
		
		int amount = 1;
		
		if (jsonObj.has("amount"))
			amount = jsonObj.get("amount").getAsInt();
		
		ItemStack stack = new ItemStack(item, amount, metadata);
		
		if (jsonObj.has("nbt"))
			stack.setTagCompound(readNBTTag((JsonObject) jsonObj.get("nbt")));
		
		return stack;
	}
	
	public static String formatJSONStr(String jsonString)
	{
		return formatJSONStr(jsonString, 4);
	}
	
	public static String formatJSONStr(String jsonString, int indentWidth)
	{
		final char[] chars = jsonString.toCharArray();
		final String newline = System.lineSeparator();
		
		String ret = "";
		boolean begin_quotes = false;
		
		for (int i = 0, indent = 0; i < chars.length; i++)
		{
			char c = chars[i];
			
			if (c == '\"')
			{
				ret += c;
				begin_quotes = !begin_quotes;
				continue;
			}
			
			if (!begin_quotes)
			{
				switch (c)
				{
					case '{':
					case '[':
						ret += c + newline + String.format("%" + (indent += indentWidth) + "s", "");
						continue;
					case '}':
					case ']':
						ret += newline + ((indent -= indentWidth) > 0 ? String.format("%" + indent + "s", "") : "") + c;
						continue;
					case ':':
						ret += c + " ";
						continue;
					case ',':
						ret += c + newline + (indent > 0 ? String.format("%" + indent + "s", "") : "");
						continue;
					default:
						if (Character.isWhitespace(c))
							continue;
				}
			}
			
			ret += c + (c == '\\' ? "" + chars[++i] : "");
		}
		
		return ret;
	}
}
