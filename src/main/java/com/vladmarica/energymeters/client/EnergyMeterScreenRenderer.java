package com.vladmarica.energymeters.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class EnergyMeterScreenRenderer extends TileEntityRenderer<TileEntityEnergyMeterBase> {
  private static final float PIXEL_WIDTH = 1 / 16F;
  private static final float[] FACE_TO_ANGLE = {0, 0, 180, 0, -90, 90};
  private static final int SCREEN_SIZE = 50;
  private static final boolean DRAW_DEBUG_SQUARE = true;
  private static final int WHITE = 0xFFFFFF;
  private static final String DISABLED_TEXT = TextFormatting.RED + "DISABLED";

  private static int disabledTextWidth = -1;

  public static class EMSRenderType extends RenderType {
    public EMSRenderType(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
      super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType texttype(ResourceLocation resourceLocation) {
      RenderType.State state = RenderType.State.getBuilder()
              .texture(new RenderState.TextureState(resourceLocation, false, false))//Texture state
              .depthTest(DepthTestState.DEPTH_ALWAYS)
              .lightmap(LightmapState.LIGHTMAP_DISABLED)
              .build(true);
      return makeType("text_type", DefaultVertexFormats.ENTITY, GL11.GL_QUADS, 256, true, false, state);
    }
  }


  public EnergyMeterScreenRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(TileEntityEnergyMeterBase tile, float partialTicks, @Nonnull MatrixStack mx,
                     @Nonnull IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
    //super.render(tile, x, y, z, partialTicks, destroyStage);


    // The screen is "off" if the meter is not fully connected
    if(!tile.isFullyConnected()) {
      return;
    }

    if (disabledTextWidth == -1) {
      disabledTextWidth = this.renderDispatcher.getFontRenderer().getStringWidth(DISABLED_TEXT);
    }
    mx.push();
    BlockPos pos = tile.getPos();
    double x = pos.getX();
    double y = pos.getY();
    double z = pos.getZ();
    mx.translate(x + 0.5, y + 1, z + 0.5);
    mx.rotate(new Quaternion(FACE_TO_ANGLE[tile.getScreenSide().getIndex()], 0, 1, 0));
    mx.translate(-0.5, 0, 0.5 + 0.001);

    mx.translate(PIXEL_WIDTH * 2, -PIXEL_WIDTH * 2, 0);

    //mx.normal3f(1.0F, 1.0F, 1.0F);
    mx.scale(0.015F, -0.015F, 0.015F);
    //GlStateManager.disableLighting();
    //GlStateManager.enableDepthTest();
    //this.renderDispatcher.setLightmapDisabled(true);


    //GlStateManager.enableBlend();
    //GlStateManager.blendFuncSeparate(
    //    GlStateManager.SourceFactor.SRC_ALPHA.param,
    //    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param,
    //    GlStateManager.SourceFactor.ONE.param,
    //    GlStateManager.DestFactor.ZERO.param);

    FontRenderer fontRenderer = this.renderDispatcher.getFontRenderer();
    //GL11.glColor4f(0.9F, 0.9F, 0.9F, 1F);

    if (DRAW_DEBUG_SQUARE) {
      IVertexBuilder builder = bufferIn.getBuffer(RenderType.getLines());
      builder.pos(mx.getLast().getMatrix(), 0, 0, 0.0f).color(0.5F, 0.0F, 0.0F, 0.75F).endVertex();
      builder.pos(mx.getLast().getMatrix(), 0, SCREEN_SIZE, 0.0f).color(0.5F, 0.0F, 0.0F, 0.75F).endVertex();
      builder.pos(mx.getLast().getMatrix(), SCREEN_SIZE, SCREEN_SIZE, 0.0f).color(0.5F, 0.0F, 0.0F, 0.75F).endVertex();
      builder.pos(mx.getLast().getMatrix(), SCREEN_SIZE, 0, 0.0f).color(0.5F, 0.0F, 0.0F, 0.75F).endVertex();
    }
/*
    if (tile.isDisabled()) {
      fontRenderer.drawString(mx, DISABLED_TEXT, (SCREEN_SIZE - disabledTextWidth) / 2.0F, 20, WHITE);
    } else {
      String displayText = formatRate(tile.getTransferRate() / (float) tile.getEnergyScale());
      int displayTextWidth = fontRenderer.getStringWidth(displayText);
      fontRenderer.drawString(mx,
          TextFormatting.WHITE + displayText,
          (SCREEN_SIZE - displayTextWidth) / 2.0F, 15, WHITE);
      fontRenderer.drawString(mx,
          TextFormatting.WHITE + tile.getEnergyAlias().getDisplayName() + "/t",
          (SCREEN_SIZE - 22) / 2.0F, 25, WHITE);
    }
*/
    //this.setLightmapDisabled(false);
    //GlStateManager.enableLighting();
    //GlStateManager.disableBlend();
    //GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    mx.pop();
  }

  private static String formatRate(float rate) {
    if (rate < 1000) return String.format("%.1f", rate);
    int exp = (int) (Math.log(rate) / Math.log(1000));
    return String.format("%.1f%c",
        rate / Math.pow(1000, exp),
        "KMBTQ".charAt(exp - 1));
  }
}
