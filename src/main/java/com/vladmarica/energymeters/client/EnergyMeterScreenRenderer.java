package com.vladmarica.energymeters.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

public class EnergyMeterScreenRenderer extends TileEntityRenderer<TileEntityEnergyMeterBase> {
  private static final float PIXEL_WIDTH = 1 / 16F;
  private static final float[] FACE_TO_ANGLE = {0, 0, 180, 0, -90, 90};
  private static final int SCREEN_SIZE = 50;
  private static final boolean DRAW_DEBUG_SQUARE = false;
  private static final int WHITE = 0xFFFFFF;
  private static final String DISABLED_TEXT = TextFormatting.RED + "DISABLED";

  private static final int disabledTextWidth = -1;

  public EnergyMeterScreenRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(TileEntityEnergyMeterBase tile, float partialTicks, MatrixStack matrixStackIn,
                     IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
    /*super.render(tile, x, y, z, partialTicks, destroyStage);


    // The screen is "off" if the meter is not fully connected
    if(!tile.isFullyConnected()) {
      return;
    }

    if (disabledTextWidth == -1) {
      disabledTextWidth = this.getFontRenderer().getStringWidth(DISABLED_TEXT);
    }

    GlStateManager.pushMatrix();

    GlStateManager.translated(x + 0.5, y + 1, z + 0.5);
    GlStateManager.rotated(FACE_TO_ANGLE[tile.getScreenSide().getIndex()], 0, 1, 0);
    GlStateManager.translated(-0.5, 0, 0.5 + 0.001);

    GlStateManager.translated(PIXEL_WIDTH * 2, -PIXEL_WIDTH * 2, 0);

    GlStateManager.normal3f(1.0F, 1.0F, 1.0F);
    GlStateManager.scalef(0.015F, -0.015F, 0.015F);
    GlStateManager.disableLighting();
    GlStateManager.enableDepthTest();
    this.setLightmapDisabled(true);


    GlStateManager.enableBlend();
    GlStateManager.blendFuncSeparate(
        GlStateManager.SourceFactor.SRC_ALPHA,
        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
        GlStateManager.SourceFactor.ONE,
        GlStateManager.DestFactor.ZERO);

    FontRenderer fontRenderer = this.getFontRenderer();
    GL11.glColor4f(0.9F, 0.9F, 0.9F, 1F);

    if (DRAW_DEBUG_SQUARE) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos(0, 0, 0.0D).color(0.5F, 0.0F, 0.0F, 0.75F).endVertex();
      bufferbuilder.pos(0, SCREEN_SIZE, 0.0D).color(0.5F, 0.0F, 0.0F, 0.75F).endVertex();
      bufferbuilder.pos(SCREEN_SIZE, SCREEN_SIZE, 0.0D).color(0.5F, 0.0F, 0.0F, 0.75F).endVertex();
      bufferbuilder.pos(SCREEN_SIZE, 0, 0.0D).color(0.5F, 0.0F, 0.0F, 0.75F).endVertex();
      tessellator.draw();
    }

    if (tile.isDisabled()) {
      fontRenderer.drawString(DISABLED_TEXT, (SCREEN_SIZE - disabledTextWidth) / 2.0F, 20, WHITE);
    } else {
      String displayText = formatRate(tile.getTransferRate() / (float) tile.getEnergyScale());
      int displayTextWidth = fontRenderer.getStringWidth(displayText);
      fontRenderer.drawString(
          TextFormatting.WHITE + displayText,
          (SCREEN_SIZE - displayTextWidth) / 2.0F, 15, WHITE);
      fontRenderer.drawString(
          TextFormatting.WHITE + tile.getEnergyAlias().getDisplayName() + "/t",
          (SCREEN_SIZE - 22) / 2.0F, 25, WHITE);
    }

    this.setLightmapDisabled(false);
    GlStateManager.enableLighting();
    GlStateManager.disableBlend();
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.popMatrix();*/
  }

  private static String formatRate(float rate) {
    if (rate < 1000) return String.format("%.1f", rate);
    int exp = (int) (Math.log(rate) / Math.log(1000));
    return String.format("%.1f%c",
        rate / Math.pow(1000, exp),
        "KMBTQ".charAt(exp - 1));
  }
}
