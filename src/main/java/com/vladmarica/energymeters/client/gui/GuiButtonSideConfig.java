package com.vladmarica.energymeters.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class GuiButtonSideConfig extends AbstractGui implements IRenderable {
  private static final int WIDTH = 16;
  private static final int HEIGHT = 16;

  private ResourceLocation texture;
  private final RelativeBlockSide side;
  private final int x;
  private final int y;
  private boolean hover = false;
  private boolean disabled;
  private final Minecraft mc = Minecraft.getInstance();

  public GuiButtonSideConfig(int x, int y, RelativeBlockSide side, ResourceLocation texture) {
    this(x, y, side, texture, false);
  }

  public GuiButtonSideConfig(int x, int y, RelativeBlockSide side, ResourceLocation texture, boolean disabled) {
    this.texture = texture;
    this.side = side;
    this.x = x;
    this.y = y;
    this.disabled = disabled;
  }

  public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.hover = mouseX >= this.x && mouseX <= this.x + WIDTH
        && mouseY >= this.y && mouseY <= this.y + HEIGHT;

    if (this.hover && !this.disabled) {
      fill(matrixStack, this.x - 1, this.y - 1, this.x + WIDTH + 1, this.y + HEIGHT + 1, 0xFFFFFFFF);
    }

    this.mc.getTextureManager().bindTexture(this.texture);
    blit(matrixStack, this.x, this.y, 0, 0, WIDTH, HEIGHT, 16, 16);
  }

  public boolean isMouseHovered() {
    return this.hover;
  }

  public RelativeBlockSide getSide() {
    return this.side;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  public boolean isDisabled() {
    return this.disabled;
  }

  public void setTexture(ResourceLocation texture) {
    this.texture = texture;
  }
}
