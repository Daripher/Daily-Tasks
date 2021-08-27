package daripher.dailytasks.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import net.minecraft.nbt.NBTTagCompound;

public class NBTUtils
{
	public static NBTTagCompound writeDate(Instant instant)
	{
		NBTTagCompound dateTag = new NBTTagCompound();
		LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
		dateTag.setInteger("Year", date.getYear());
		dateTag.setInteger("Month", date.getMonthValue());
		dateTag.setInteger("Day", date.getDayOfMonth());
		dateTag.setInteger("Hour", date.getHour());
		dateTag.setInteger("Minute", date.getMinute());
		dateTag.setInteger("Second", date.getSecond());
		return dateTag;
	}
	
	public static Instant readDate(NBTTagCompound nbt)
	{
		int year = nbt.getInteger("Year");
		int month = nbt.getInteger("Month");
		int day = nbt.getInteger("Day");
		int hour = nbt.getInteger("Hour");
		int minute = nbt.getInteger("Minute");
		int second = nbt.getInteger("Second");
		LocalDateTime date = LocalDateTime.of(year, month, day, hour, minute, second);
		return date.toInstant(ZoneOffset.UTC);
	}
}
