package com.vladmarica.energymeters.client.model;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.block.BlockEnergyMeter.MeterType;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnergyMetersMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TextureLocations {
  private static final Map<MeterType, ResourceLocation> SIDE_TEXTURES = new HashMap<>();
  private static final Map<MeterType, ResourceLocation> INPUT_TEXTURES = new HashMap<>();
  private static final Map<MeterType, ResourceLocation> OUTPUT_TEXTURES = new HashMap<>();
  private static final Map<MeterType, ResourceLocation> SCREEN_TEXTURES = new HashMap<>();

  static {
    for (MeterType meterType : MeterType.values()) {
      String suffix = meterType != MeterType.FE_METER ? "_" + meterType.getString() : "";
      SIDE_TEXTURES.put(meterType, new ResourceLocation(EnergyMetersMod.MODID, "block/meter" + suffix));
      INPUT_TEXTURES.put(meterType, new ResourceLocation(
          EnergyMetersMod.MODID, "block/meter_input" + suffix));
      OUTPUT_TEXTURES.put(meterType, new ResourceLocation(EnergyMetersMod.MODID, "block/meter_output" + suffix));
      SCREEN_TEXTURES.put(meterType, new ResourceLocation(EnergyMetersMod.MODID, "block/meter_screen" + suffix));
    }
  }

  @SubscribeEvent
  public static void registerSpecialModels(final ModelRegistryEvent registryEvent) {
    ModelLoader.addSpecialModel(new ResourceLocation(EnergyMetersMod.MODID, "block/meter"));
  }

  @SubscribeEvent
  public static void onTextureStitchEvent(TextureStitchEvent.Pre event) {
    if (event.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE)) {
      for (ResourceLocation texture: SIDE_TEXTURES.values()) {
        event.addSprite(texture);
      }
      for (ResourceLocation texture: INPUT_TEXTURES.values()) {
        event.addSprite(texture);
      }
      for (ResourceLocation texture: OUTPUT_TEXTURES.values()) {
        event.addSprite(texture);
      }
      for (ResourceLocation texture: SCREEN_TEXTURES.values()) {
        event.addSprite(texture);
      }

    }
  }
  public static ResourceLocation getSideTexture(MeterType type) {
    return SIDE_TEXTURES.get(type);
  }

  public static ResourceLocation getInputTexture(MeterType type) {
    return INPUT_TEXTURES.get(type);
  }

  public static ResourceLocation getOutputTexture(MeterType type) {
    return OUTPUT_TEXTURES.get(type);
  }

  public static ResourceLocation getScreenTexture(MeterType type) {
    return SCREEN_TEXTURES.get(type);
  }

  public static ResourceLocation getGuiResource(ResourceLocation location) {
    String path = location.getPath();
    return new ResourceLocation(location.getNamespace(), String.format("textures/%s.png", path));
  }
}
