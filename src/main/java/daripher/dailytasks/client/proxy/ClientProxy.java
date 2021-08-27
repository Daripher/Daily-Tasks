package daripher.dailytasks.client.proxy;

import daripher.dailytasks.client.gui.GuiTasks;
import daripher.dailytasks.client.keybind.KeyBindings;
import daripher.dailytasks.common.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit()
	{
		ClientRegistry.registerKeyBinding(KeyBindings.KEY_OPEN_GUI);
	}
	
	@Override
	public void updateTasksGuiState()
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		
		if (mc.currentScreen instanceof GuiTasks)
		{
			mc.currentScreen.initGui();
		}
	}
	
	@Override
	public EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft().player;
	}
}
