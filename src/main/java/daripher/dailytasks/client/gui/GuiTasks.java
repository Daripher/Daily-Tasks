package daripher.dailytasks.client.gui;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import org.lwjgl.opengl.GL11;

import daripher.dailytasks.DailyTasksMod;
import daripher.dailytasks.client.utils.GuiUtils;
import daripher.dailytasks.common.capability.ITasks;
import daripher.dailytasks.common.config.Config;
import daripher.dailytasks.common.network.ClaimRewardMessage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiTasks extends GuiScreen
{
	private final static ResourceLocation ICON_TEXTURE = new ResourceLocation(DailyTasksMod.MODID, "textures/gui/tasks.png");
	private final static ResourceLocation POPUP_TEXTURE = new ResourceLocation(DailyTasksMod.MODID, "textures/gui/tasks_popup.png");
	private GuiTaskList tasksList;
	private String title = I18n.format("gui.tasks.title");
	private boolean showPopup;
	private int selectedReward = -1;
	private int lightingSize = 52;
	private int rewardSlotY;
	
	@Override
	public void initGui()
	{
		GuiTaskList prevList = tasksList;
		tasksList = new GuiTaskList(mc, width + 45, height, 63, height - 32, 40);
		
		if (prevList != null)
			tasksList.scrollBy(prevList.getAmountScrolled());
		
		buttonList.clear();
		buttonList.add(new GuiButton(0, (int) (width * .33 - 60), height - 26, 120, 20, I18n.format("gui.tasks.close")));
		buttonList.add(new GuiButton(1, (int) (width * .66 - 60), height - 26, 120, 20, ""));
		buttonList.add(new GuiButton(2, (int) width / 2 - 40, height / 2 + 26, 80, 20, I18n.format("gui.tasks.claim1")));
		ITasks tasks = DailyTasksMod.playerTasks(mc.player);
		
		if (showPopup && !tasks.tasksCompleted())
		{
			showPopup = false;
		}
		
		if (!showPopup)
		{
			selectedReward = -1;
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		if (!showPopup)
		{
			selectedReward = -1;
		}
		
		ITasks tasks = DailyTasksMod.playerTasks(mc.player);
		buttonList.get(1).displayString = I18n.format("gui.tasks.claim");
		buttonList.get(1).enabled = tasks.tasksCompleted();
		buttonList.get(2).visible = showPopup;
		buttonList.get(2).enabled = selectedReward >= 0;
		String timeLeft = formatDuration(Duration.between(tasks.getLastCompletionTime().plusSeconds(Config.tasksGenerationDelay), Instant.now()));
		String timeSubtitle = I18n.format("gui.tasks.timer", timeLeft);
		String completedTasksSubtitle = I18n.format("gui.tasks.completed", tasks.getCompletedTasks(), tasks.getPlayerTasks().size());
		String streakSubtitle = I18n.format("gui.tasks.streak", DailyTasksMod.playerTasks(mc.player).getCompletionStreak());
		tasksList.drawScreen(mouseX, mouseY, partialTicks);
		mc.renderEngine.bindTexture(ICON_TEXTURE);
		drawTexturedModalRect(width / 2 + fontRenderer.getStringWidth(title) - 8, 13, -20, 0, 20, 20);
		GuiUtils.drawCenteredOutlineString(fontRenderer, title, width / 2 - 10, 20, 0xd4cfc3, 0x242322, 2.0f);
		drawCenteredString(fontRenderer, completedTasksSubtitle, width / 2, 42, 0xffffff);
		drawCenteredString(fontRenderer, streakSubtitle, (int) (width * .2), 27, 0xffffff);
		drawCenteredString(fontRenderer, timeSubtitle, (int) (width * .8), 27, 0xffffff);
		
		if (showPopup)
		{
			int popupMenuX = width / 2 - 100;
			int popupMenuY = height / 2 - 68;
			int popupMenuXSize = 200;
			int popupMenuYSize = 135;
			rewardSlotY = popupMenuY + popupMenuYSize / 2 - 13;
			String popupTitle = I18n.format("gui.tasks.popup");
			GlStateManager.color(1.0f, 1.0f, 1.0f);
			mc.renderEngine.bindTexture(POPUP_TEXTURE);
			GuiUtils.drawTexturedRectWithTransparency(popupMenuX, popupMenuY, 0, 0, popupMenuXSize, popupMenuYSize);
			
			if (selectedReward != -1)
			{
				GuiUtils.drawTexturedRectWithTransparency(popupMenuX + (int) (popupMenuXSize * 0.25f * (selectedReward + 1)) - lightingSize / 2,
						rewardSlotY + 8 - lightingSize / 2, 0, -lightingSize, lightingSize, lightingSize);
			}
			
			GuiUtils.drawCenteredString(fontRenderer, popupTitle, width / 2, popupMenuY + 20, 0xffffff, 1.15f);
			
			for (int i = 0; i < tasks.getRewards().size(); i++)
			{
				drawItemStack(tasks.getRewards().get(i), popupMenuX - 8 + (int) (popupMenuXSize * 0.25f * (i + 1)), rewardSlotY, 1.9f);
			}
		}
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		if (showPopup)
		{
			int popupMenuX = width / 2 - 100;
			int popupMenuXSize = 200;
			int slotY = rewardSlotY - 8;
			
			for (int i = 0; i < tasks.getRewards().size(); i++)
			{
				int slotX = popupMenuX - 8 + (int) (popupMenuXSize * 0.25f * (i + 1));
				
				if (GuiUtils.mouseOver(mouseX, mouseY, slotX, slotY, slotX + 32, slotY + 32))
				{
					renderToolTip(tasks.getRewards().get(i), mouseX, mouseY);
				}
			}
		}
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button.id == 0)
		{
			mc.displayGuiScreen(null);
		}
		
		if (button.id == 1)
		{
			showPopup ^= true;
		}
		
		if (button.id == 2)
		{
			DailyTasksMod.NETWORK_WRAPPER.sendToServer(new ClaimRewardMessage(selectedReward));
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		ITasks tasks = DailyTasksMod.playerTasks(mc.player);
		int popupMenuX = width / 2 - 100;
		int popupMenuXSize = 200;
		
		for (int i = 0; i < Math.min(tasks.getRewards().size(), 3); i++)
		{
			int slotX = popupMenuX - 17 + (int) (popupMenuXSize * 0.25f * (i + 1));
			int slotY = rewardSlotY - 9;
			
			if (GuiUtils.mouseOver(mouseX, mouseY, slotX, slotY, slotX + 34, slotY + 34))
			{
				selectedReward = i;
			}
		}
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void handleMouseInput() throws IOException
	{
		tasksList.handleMouseInput();
		super.handleMouseInput();
	}
	
	private void drawItemStack(ItemStack stack, int x, int y, float scale)
	{
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		this.itemRender.zLevel = 200.0F;
		net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
		GL11.glPushMatrix();
		
		if (font == null)
			font = fontRenderer;
		
		if (scale != 1.0F)
		{
			GL11.glTranslatef(x + 8.0F, y + 8.0F, 0.0F);
			GL11.glScalef(scale, scale, 1.0f);
			GL11.glTranslatef(-x - 8.0F, -y - 8.0F, 0.0F);
		}
		
		this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, null);
		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0.0F;
		GL11.glPopMatrix();
	}
	
	private static String formatDuration(Duration duration)
	{
		long seconds = Math.abs(duration.getSeconds());
		return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
	}
}
