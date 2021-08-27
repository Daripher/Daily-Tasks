package daripher.dailytasks.client.gui;

import java.util.ArrayList;
import java.util.List;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.client.utils.GuiUtils;
import daripher.dailytasks.common.capability.ITask;
import daripher.dailytasks.common.capability.ITasks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiTaskList extends GuiListExtended
{
	private final static ResourceLocation TEXTURE = new ResourceLocation(DailyTasksMod.MODID, "textures/gui/tasks.png");
	private List<TaskEntry> tasks = new ArrayList<>();
	
	public GuiTaskList(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
	{
		super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
		ITasks playerTasks = DailyTasksMod.playerTasks(mc.player);
		
		for (int i = 0; i < playerTasks.getPlayerTasks().size(); i++)
		{
			tasks.add(new TaskEntry(playerTasks.getPlayerTasks().get(i)));
		}
	}
	
	@Override
	public IGuiListEntry getListEntry(int index)
	{
		return tasks.get(index);
	}
	
	@Override
	protected int getSize()
	{
		return tasks.size();
	}
	
	private class TaskEntry implements IGuiListEntry
	{
		ITask task;
		
		public TaskEntry(ITask task)
		{
			this.task = task;
		}
		
		@Override
		public void updatePosition(int slotIndex, int x, int y, float partialTicks)
		{
			
		}
		
		@Override
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks)
		{
			int completedTaskColor = 0x34c920;
			int progressBarWidth = 84;
			int progressBarHeight = 12;
			int progressBarX = (mc.currentScreen.width - progressBarWidth) / 2;
			int iconX = progressBarX - 25;
			int iconY = y + 4;
			double progress = DailyTasksMod.playerTasks(mc.player).getProgress(task);
			double maxProgress = task.getMaxProgress();
			int fontColor = 0xf7d38b;
			int fontOutlineColor = 0x211703;
			boolean completed = progress == maxProgress;
			long progressBarColor = completed ? completedTaskColor : task.getGuiColor();
			String progressString = "" + ((int) progress) + "/" + ((int) maxProgress);
			mc.renderEngine.bindTexture(TEXTURE);
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
					GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.color(1.0f, 1.0f, 1.0f);
			mc.currentScreen.drawTexturedModalRect(progressBarX, y + 16, 0, 0, progressBarWidth, progressBarHeight);
			applyTaskColor(progressBarColor);
			mc.currentScreen.drawTexturedModalRect(progressBarX, y + 16, 0, progressBarHeight, (int) (progressBarWidth * progress / maxProgress),
					progressBarHeight);
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			mc.currentScreen.drawCenteredString(mc.fontRenderer, I18n.format("task." + task.getType() + ".description"), mc.currentScreen.width / 2, y + 2,
					completed ? 0x34c920 : 0xffffff);
			GuiUtils.drawCenteredOutlineString(mc.fontRenderer, progressString, mc.currentScreen.width / 2, y + 18.5f, fontColor, fontOutlineColor, .9f);
			task.drawIcon(iconX, iconY);
		}
		
		private void applyTaskColor(long color)
		{
			float r = ((color >> 16) & 0xFF) / 255.0f;
			float g = ((color >> 8) & 0xFF) / 255.0f;
			float b = (color & 0xFF) / 255.0f;
			GlStateManager.color(r, g, b);
		}
		
		@Override
		public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY)
		{
			return false;
		}
		
		@Override
		public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
		{
			
		}
	}
}
