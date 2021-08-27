package daripher.dailytasks.client.keybind;

import org.lwjgl.input.Keyboard;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.client.gui.GuiTasks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

@EventBusSubscriber
public class KeyBindings
{
	public static final KeyBinding KEY_OPEN_GUI = new KeyBinding("gui", Keyboard.KEY_C, DailyTasksMod.NAME);
	
	@SubscribeEvent
	public static void onKeyDown(ClientTickEvent event)
	{
		if (KEY_OPEN_GUI.isPressed())
		{
			if (Minecraft.getMinecraft().currentScreen == null)
				Minecraft.getMinecraft().displayGuiScreen(new GuiTasks());
		}
	}
}
