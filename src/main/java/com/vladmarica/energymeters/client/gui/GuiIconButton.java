package com.vladmarica.energymeters.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.vladmarica.energymeters.client.Sprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;

import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.util.text.StringTextComponent;

public class GuiIconButton extends Button {
  public static final int SIZE = 20;

  private Sprite icon;

  public GuiIconButton(int x, int y, Sprite icon, IPressable onPress) {
    super(x, y, SIZE, SIZE, StringTextComponent.EMPTY, onPress);
    this.icon = icon;
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    if (icon != null) {
      icon.render(matrixStack, Minecraft.getInstance().currentScreen, this.x + 2, this.y + 2);
    }
  }

  public void setIcon(Sprite icon) {
    this.icon = icon;
  }
}