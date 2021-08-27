package daripher.dailytasks.client.utils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;

public class GuiUtils
{
	protected static final Minecraft CLIENT = FMLClientHandler.instance().getClient();
	protected static final RenderItem ITEM_RENDER = CLIENT.getRenderItem();
	
	public static void drawItemStack(ItemStack stack, int x, int y, float scale)
	{
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		GL11.glPushMatrix();
		
		if (scale != 1.0F)
		{
			GL11.glTranslatef(x + 8.0F, y + 8.0F, 0.0F);
			GL11.glScalef(scale, scale, 1.0f);
			GL11.glTranslatef(-x - 8.0F, -y - 8.0F, 0.0F);
		}
		
		if (Block.getBlockFromItem(stack.getItem()) != Blocks.AIR)
		{
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
		else
		{
			RenderHelper.enableStandardItemLighting();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
		
		CLIENT.currentScreen.zLevel = 200.0F;
		ITEM_RENDER.zLevel = 200.0F;
		ITEM_RENDER.renderItemAndEffectIntoGUI(stack, x, y);
		ITEM_RENDER.renderItemOverlayIntoGUI(CLIENT.fontRenderer, stack, x, y, null);
		ITEM_RENDER.zLevel = 0.0F;
		CLIENT.currentScreen.zLevel = 0.0F;
		
		GL11.glPopMatrix();
	}
	
	public static void drawItemStack(ItemStack stack, int x, int y)
	{
		drawItemStack(stack, x, y, 1.0F);
	}
	
	public static void drawCenteredOutlineString(FontRenderer fontRenderer, String string, float x, float y, int fontColor, int fontOutlineColor)
	{
		drawCenteredOutlineString(fontRenderer, string, x, y, fontColor, fontOutlineColor, 1.0f);
	}
	
	public static void drawCenteredOutlineString(FontRenderer fontRenderer, String string, float x, float y, int fontColor, int fontOutlineColor, float scale)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(x - fontRenderer.getStringWidth(string) * scale / 2, y + fontRenderer.FONT_HEIGHT / 2, 0.0f);
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(0.0f, -fontRenderer.FONT_HEIGHT / 2, 0.0f);
		fontRenderer.drawString(string, -1, 0, fontOutlineColor);
		fontRenderer.drawString(string, 0, -1, fontOutlineColor);
		fontRenderer.drawString(string, 1, 0, fontOutlineColor);
		fontRenderer.drawString(string, 0, 1, fontOutlineColor);
		fontRenderer.drawString(string, 0, 0, fontColor);
		GlStateManager.popMatrix();
	}
	
	public static void drawTexturedRectWithTransparency(int x, int y, int u, int v, int xSize, int ySize)
	{
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableAlpha();
		CLIENT.currentScreen.drawTexturedModalRect(x, y, u, v, xSize, ySize);
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
	}
	
	public static boolean mouseOver(int mouseX, int mouseY, int x1, int y1, int x2, int y2)
	{
		return mouseX > x1 && mouseX < x2 && mouseY > y1 && mouseY < y2;
	}
	
	public static void drawCenteredString(FontRenderer fontRenderer, String string, int x, int y, int fontColor, float scale)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(x - fontRenderer.getStringWidth(string) * scale / 2, y + fontRenderer.FONT_HEIGHT / 2, 0.0f);
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(0.0f, -fontRenderer.FONT_HEIGHT / 2, 0.0f);
		fontRenderer.drawString(string, 0, 0, fontColor);
		GlStateManager.popMatrix();
	}
}
