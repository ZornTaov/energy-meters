package com.vladmarica.energymeters.client.gui;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.Util;
import com.vladmarica.energymeters.block.BlockEnergyMeter;
import com.vladmarica.energymeters.block.BlockEnergyMeter.MeterType;
import com.vladmarica.energymeters.client.Sprites;
import com.vladmarica.energymeters.client.model.TextureLocations;
import com.vladmarica.energymeters.energy.EnergyType;
import com.vladmarica.energymeters.network.PacketUpdateMeterConfig;
import com.vladmarica.energymeters.network.PacketUpdateMeterSides;
import com.vladmarica.energymeters.network.PacketUpdateRateLimit;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import com.vladmarica.energymeters.tile.config.EnumRedstoneControlState;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class EnergyMeterScreen extends Screen implements IPressable {

  private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(
      EnergyMetersMod.MODID, "textures/gui/energymeter.png");

  private static final NumberFormat RATE_FORMAT = NumberFormat.getNumberInstance(Locale.US);
  private static final NumberFormat TOTAL_FORMAT = NumberFormat.getNumberInstance(Locale.US);
  static {
    RATE_FORMAT.setMinimumFractionDigits(1);
    RATE_FORMAT.setMaximumFractionDigits(1);
    TOTAL_FORMAT.setMaximumFractionDigits(0);
  }

  private static final int TEXTURE_WIDTH = 256;
  private static final int TEXTURE_HEIGHT = 148;
  private static final int COLOR_GREY = 4210752;
  private static final int COLOR_WHITE = 0xFFFFFF;

  private static final int CONFIG_BUTTONS_OFFSET_X = 168;
  private static final int CONFIG_BUTTONS_OFFSET_Y = 24;

  private GuiIconButton rateLimitButton;
  private GuiButtonEnergyAlias energyAliasButton;
  private GuiButtonConfigEnum<EnumRedstoneControlState> redstoneControlButton;
  private Button setRateLimitButton;

  private final TileEntityEnergyMeterBase tile;
  private BiMap<RelativeBlockSide, Direction> sideToFaceMap;
  private Map<RelativeBlockSide, GuiButtonSideConfig> sideToButtonMap = new HashMap<>();

  private final ResourceLocation sideTexture;
  private final ResourceLocation inputTexture;
  private final ResourceLocation outputTexture;
  private final ResourceLocation screenTexture;

  private TextFieldWidget rateLimitTextField;
  private boolean isEditingLimit = false;

  public EnergyMeterScreen(TileEntityEnergyMeterBase tile) {
    super(new StringTextComponent("Energy Meter"));

    this.tile = tile;
    this.updateSideMapping();

    BlockState state = tile.getWorld().getBlockState(tile.getPos());
    MeterType type = ((BlockEnergyMeter) state.getBlock()).getMeterType();
    this.sideTexture = TextureLocations.getGuiResource(TextureLocations.getSideTexture(type));
    this.inputTexture = TextureLocations.getGuiResource(TextureLocations.getInputTexture(type));
    this.outputTexture = TextureLocations.getGuiResource(TextureLocations.getOutputTexture(type));
    this.screenTexture = TextureLocations.getGuiResource(TextureLocations.getScreenTexture(type));
  }

  private void updateSideMapping() {
    Direction screenFace = this.tile.getScreenSide();
    this.sideToFaceMap = HashBiMap.create();
    this.sideToFaceMap.put(RelativeBlockSide.TOP, Direction.UP);
    this.sideToFaceMap.put(RelativeBlockSide.BOTTOM, Direction.DOWN);
    this.sideToFaceMap.put(RelativeBlockSide.FRONT, screenFace);
    this.sideToFaceMap.put(RelativeBlockSide.LEFT, Util.getLeftFace(screenFace));
    this.sideToFaceMap.put(RelativeBlockSide.RIGHT, Util.getRightFace(screenFace));
    this.sideToFaceMap.put(RelativeBlockSide.BACK, Util.getBackFace(screenFace));
  }

  @Override
  protected void init() {
    super.init();


    int x = (this.width - TEXTURE_WIDTH) / 2;
    int y = (this.height - TEXTURE_HEIGHT) / 2;

    this.buttons.add(this.rateLimitButton = new GuiIconButton(
        x + CONFIG_BUTTONS_OFFSET_X,
        y + CONFIG_BUTTONS_OFFSET_Y,
        Sprites.SPECTRAL_ARROW, this));

    this.buttons.add(this.energyAliasButton = new GuiButtonEnergyAlias(
        x + CONFIG_BUTTONS_OFFSET_X + GuiIconButton.SIZE + 5,
        y + CONFIG_BUTTONS_OFFSET_Y,
        this.tile.getEnergyAlias(), this));

    this.buttons.add(this.redstoneControlButton = new GuiButtonConfigEnum<>(
        "Redstone Control",
        x + CONFIG_BUTTONS_OFFSET_X + (GuiIconButton.SIZE * 2) + 10,
        y + CONFIG_BUTTONS_OFFSET_Y,
        EnumRedstoneControlState.class,
        tile.getRedstoneControlState(), this));

    int startX = 195;
    int startY = 87;

    this.sideToButtonMap = new HashMap<>();
    this.sideToButtonMap.put(RelativeBlockSide.FRONT,
        new GuiButtonSideConfig(x + startX, y + startY, RelativeBlockSide.FRONT, screenTexture, true));
    this.sideToButtonMap.put(RelativeBlockSide.BACK,
        new GuiButtonSideConfig(x + startX + GuiIconButton.SIZE, y + startY + GuiIconButton.SIZE, RelativeBlockSide.BACK, screenTexture));
    this.sideToButtonMap.put(RelativeBlockSide.TOP,
        new GuiButtonSideConfig(x + startX, y + startY - GuiIconButton.SIZE, RelativeBlockSide.TOP, screenTexture));
    this.sideToButtonMap.put(RelativeBlockSide.BOTTOM,
        new GuiButtonSideConfig(x + startX, y + startY + GuiIconButton.SIZE, RelativeBlockSide.BOTTOM, screenTexture));
    this.sideToButtonMap.put(RelativeBlockSide.LEFT,
        new GuiButtonSideConfig(x + startX - GuiIconButton.SIZE, y + startY, RelativeBlockSide.LEFT, screenTexture));
    this.sideToButtonMap.put(RelativeBlockSide.RIGHT,
        new GuiButtonSideConfig(x + startX + GuiIconButton.SIZE, y + startY, RelativeBlockSide.RIGHT, screenTexture));

    this.updateConfigButtonTextures();

    this.rateLimitTextField = new TextFieldWidget(this.font, x + 18, y + 67, 70, 12, StringTextComponent.EMPTY);
    this.rateLimitTextField.setValidator(Util::isValidRateLimitString);
    this.rateLimitTextField.setVisible(false);
    this.buttons.add(this.setRateLimitButton = new ExtendedButton(x + 117, y + 65, 25, 16, new StringTextComponent("Set"), this));
    this.setRateLimitButton.visible = false;

    // Disable rate limit button if the energy type is not limitable
    if (!tile.getEnergyType().isLimitable()) {
      this.rateLimitButton.active = false;
    }
  }

  private void updateConfigButtonTextures() {
    for (GuiButtonSideConfig button: this.sideToButtonMap.values()) {
      button.setTexture(this.getTextureForSide(button.getSide()));
    }
  }

  private ResourceLocation getTextureForSide(RelativeBlockSide side) {
    if (side == RelativeBlockSide.FRONT) {
      return screenTexture;
    }

    Direction face = this.sideToFaceMap.get(side);
    if (face == tile.getInputSide()) {
      return inputTexture;
    } else if (face == tile.getOutputSide()) {
      return outputTexture;
    }

    return sideTexture;
  }

  @Override
  public void tick() {
    if (this.tile.getEnergyType().isLimitable()) {
      this.rateLimitTextField.tick();
      this.rateLimitTextField.setVisible(isEditingLimit);
      this.rateLimitButton.active = !isEditingLimit;
      this.setRateLimitButton.visible = isEditingLimit;
    }

    super.tick();
  }

  private String getStatusString() {
    if (!this.tile.isFullyConnected()) {
      return TextFormatting.GOLD + "Not Connected";
    }
    if (this.tile.isDisabled()) {
      return TextFormatting.RED + "Disabled";
    }
    return TextFormatting.GREEN + "Active";
  }

  private String getRateLimitString() {
    return this.tile.getRateLimit() == TileEntityEnergyMeterBase.UNLIMITED_RATE
        ? "Unlimited"
        : Integer.toString(this.tile.getRateLimit());
  }


  @Override
  public void render(@Nonnull MatrixStack mx, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(mx);


    int x = (this.width - TEXTURE_WIDTH) / 2;
    int y = (this.height - TEXTURE_HEIGHT) / 2;

    this.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
    this.blit(mx, x, y, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);

    int titleWidth = this.font.getStringWidth("Energy Meter");
    int titleX = (TEXTURE_WIDTH - titleWidth) / 2;
    this.font.drawString(mx, "Energy Meter", x + titleX, y + 7, COLOR_GREY);

    int leftPanelOffset = 18;
    int statYIncr = 28;
    int statY = y + 28;

    String units = this.tile.getEnergyAlias().getDisplayName();
    this.font.drawString(mx, TextFormatting.GRAY + "Transfer Rate", x  + leftPanelOffset, statY, COLOR_WHITE);
    this.font.drawString(mx, RATE_FORMAT.format(tile.getTransferRate() / tile.getEnergyScale()) + " " + units + "/t", x  + leftPanelOffset, statY + 11, COLOR_WHITE);
    statY += statYIncr;

    this.font.drawString(mx, TextFormatting.GRAY + "Transfer Rate Limit", x  + leftPanelOffset, statY, COLOR_WHITE);
    if (!this.isEditingLimit) {
      this.font.drawString(mx, getRateLimitString() + " " + units + "/t", x + leftPanelOffset, statY + 11, COLOR_WHITE);
    }
    statY += statYIncr;

    this.font.drawString(mx, TextFormatting.GRAY + "Total Transferred", x  + leftPanelOffset, statY, COLOR_WHITE);
    this.font.drawString(mx, TOTAL_FORMAT.format(tile.getTotalEnergyTransferred() / tile.getEnergyScale()) + " " + units, x  + leftPanelOffset, statY + 11, COLOR_WHITE);
    statY += statYIncr;

    this.font.drawString(mx, TextFormatting.GRAY + "Status", x  + leftPanelOffset, statY, COLOR_WHITE);
    this.font.drawString(mx, getStatusString(), x  + leftPanelOffset, statY + 11, COLOR_WHITE);

    this.updateConfigButtonTextures();
    for (GuiButtonSideConfig sideConfigButton : this.sideToButtonMap.values()) {
      //GlStateManager.color4f(1, 1, 1, 1);
      sideConfigButton.render(mx, mouseX, mouseY, partialTicks);
    }

    this.rateLimitTextField.render(mx, mouseX, mouseY, partialTicks);
    if (this.isEditingLimit) {
      this.font.drawString(mx,
              this.energyAliasButton.getAlias().getDisplayName() + "/t",
          rateLimitTextField.x + rateLimitTextField.getWidth() + 4,
          rateLimitTextField.y + 2,
          COLOR_WHITE);
    }

    super.render(mx, mouseX, mouseY, partialTicks);

    for (GuiButtonSideConfig sideConfigButton : this.sideToButtonMap.values()) {
      if (sideConfigButton.isMouseHovered()) {
        List<String> lines = new ArrayList<>(1);
        lines.add(sideConfigButton.getSide().getLabel());
        if (sideConfigButton.getSide() == RelativeBlockSide.FRONT) {
          lines.add(TextFormatting.GRAY + "Screen");
        }
        if (this.sideToFaceMap.get(sideConfigButton.getSide()) == tile.getInputSide()) {
          lines.add(TextFormatting.GRAY + "Input");
        }
        if (this.sideToFaceMap.get(sideConfigButton.getSide()) == tile.getOutputSide()) {
          lines.add(TextFormatting.GRAY + "Output");
        }
        //TODO: Tooltip
        //this.renderTooltip(mx, lines, mouseX, mouseY);
        break;
      }
    }

    for (Widget button : this.buttons) {
      if (button instanceof IHasTooltip && button.isMouseOver(mouseX, mouseY)) {
        //TODO: Tooltip
        //this.renderTooltip(mx, ((IHasTooltip) button).getTooltipLines(), mouseX, mouseY);
      }
    }

    if (this.rateLimitButton.isMouseOver(mouseX, mouseY)) {
      List<String> lines = new ArrayList<>();
      lines.add("Transfer Rate Limit");
      lines.add(TextFormatting.GRAY + getRateLimitString() + " " + units + "/t");

      if (!this.tile.getEnergyType().isLimitable()) {
        lines.add(TextFormatting.RED + this.tile.getEnergyType().getName() + " meters cannot be limited");
      }
      //TODO: Tooltip
      //this.renderTooltip(lines,  mouseX, mouseY);
    }
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    boolean result = this.rateLimitTextField.keyPressed(keyCode, scanCode, modifiers);
    return result || super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public void onPress(@Nonnull Button button) {

    boolean sendUpdatePacket = false;

    if (button == this.redstoneControlButton) {
      EnumRedstoneControlState newState = this.redstoneControlButton.cycle();
      this.tile.setRedstoneControlState(newState);
      sendUpdatePacket = true;
    }

    if (button == this.energyAliasButton) {
      EnergyType.EnergyAlias newAlias = this.energyAliasButton.cycle();
      this.tile.setEnergyAlias(newAlias);
      sendUpdatePacket = true;
    }

    if (sendUpdatePacket) {
      EnergyMetersMod.NETWORK.sendToServer(
              new PacketUpdateMeterConfig(
                      this.tile.getPos(),
                      this.tile.getRedstoneControlState(),
                      this.tile.getEnergyAlias().getIndex()));
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    // Left click
    if (mouseButton == 0) {
      for (GuiButtonSideConfig button : this.sideToButtonMap.values()) {
        if (!button.isDisabled() && button.isMouseHovered()) {
          this.sideConfigButtonClicked(button);
          break;
        }
      }
      if (this.energyAliasButton.isMouseOver(mouseX, mouseY)) this.energyAliasButton.onClick(mouseX, mouseY);
      if (this.redstoneControlButton.isMouseOver(mouseX, mouseY)) this.redstoneControlButton.onClick(mouseX, mouseY);
      if (this.setRateLimitButton.isMouseOver(mouseX, mouseY) && this.setRateLimitButton.visible && this.tile.getEnergyType().isLimitable() && this.isEditingLimit) {
        String str = this.rateLimitTextField.getText();
        if (Util.isValidRateLimitString(str)) {
          int rateLimit;
          if (str.isEmpty()) {
            rateLimit = TileEntityEnergyMeterBase.UNLIMITED_RATE;
          } else {
            rateLimit = Integer.parseInt(str);
          }

          this.isEditingLimit = false;
          this.tile.setRateLimit(rateLimit);
          EnergyMetersMod.NETWORK.sendToServer(new PacketUpdateRateLimit(this.tile.getPos(), rateLimit));
        }
      }

      if (this.rateLimitButton.isMouseOver(mouseX, mouseY) && this.tile.getEnergyType().isLimitable() && !this.isEditingLimit) {
        this.isEditingLimit = true;
        this.rateLimitTextField.setText(
                this.tile.getRateLimit() == TileEntityEnergyMeterBase.UNLIMITED_RATE
                        ? ""
                        : Integer.toString(this.tile.getRateLimit()));
      }

      this.rateLimitTextField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }
  private void sideConfigButtonClicked(GuiButtonSideConfig button) {
    this.minecraft.getSoundHandler().play(
            SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

    Direction face = this.sideToFaceMap.get(button.getSide());
    if (face == this.tile.getInputSide()) {
      this.tile.setOutputSide(face);
      this.tile.setInputSide(null);
    } else if (face == this.tile.getOutputSide()) {
      this.tile.setOutputSide(null);
    } else {
      if (this.tile.getInputSide() != null && this.tile.getOutputSide() == null) {
        this.tile.setOutputSide(face);
      } else {
        this.tile.setInputSide(face);
      }
    }

    EnergyMetersMod.NETWORK.sendToServer(new PacketUpdateMeterSides(
            this.tile.getPos(),
            this.tile.getInputSide(),
            this.tile.getOutputSide()));
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }
}
