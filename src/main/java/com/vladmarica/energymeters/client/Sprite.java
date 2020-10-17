package com.vladmarica.energymeters.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;

public class Sprite {
  private static final int DEFAULT_WIDTH = 16;
  private static final int DEFAULT_HEIGHT = 16;
  private static final int SPRITE_MAP_SIZE = 16;

  private final ResourceLocation texture;
  private final int index;
  private final int textureX;
  private final int textureY;

  public Sprite(ResourceLocation texture, int index) {
    this.texture = texture;
    this.index = index;
    this.textureX = this.index % SPRITE_MAP_SIZE * DEFAULT_WIDTH;
    this.textureY = this.index / SPRITE_MAP_SIZE * DEFAULT_HEIGHT;
  }

  public void render(MatrixStack matrixStack, Screen gui, int x, int y) {
    gui.getMinecraft().getTextureManager().bindTexture(this.texture);
    gui.blit(matrixStack, x, y, textureX, textureY, DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }
}
