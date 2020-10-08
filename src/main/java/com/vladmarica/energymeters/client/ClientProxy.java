package com.vladmarica.energymeters.client;

import com.vladmarica.energymeters.CommonProxy;
import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.block.BlockEnergyMeter;
import com.vladmarica.energymeters.block.Blocks;
import com.vladmarica.energymeters.client.gui.EnergyMeterScreen;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import java.util.HashMap;
import java.util.Map;

import com.vladmarica.energymeters.tile.TileEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy extends CommonProxy {

  @Override
  public void init(FMLCommonSetupEvent event) {
    super.init(event);
    FMLJavaModLoadingContext.get().getModEventBus().register(this);
    ClientRegistry.bindTileEntityRenderer(TileEntityTypes.get(BlockEnergyMeter.MeterType.FE_METER), EnergyMeterScreenRenderer::new);
    //ClientRegistry.bindTileEntityRenderer(Registration.TE_Energy_Meter_FE.get(), EnergyMeterScreenRenderer::new);
  }

  @Override
  public ActionResultType handleEnergyBlockActivation(World world, BlockPos pos, PlayerEntity player) {
    if (!world.isRemote) {
      return ActionResultType.SUCCESS;
    }

    TileEntity tile = world.getTileEntity(pos);
    if (tile instanceof TileEntityEnergyMeterBase) {
      Minecraft.getInstance().displayGuiScreen(new EnergyMeterScreen((TileEntityEnergyMeterBase) tile));
      return ActionResultType.SUCCESS;
    }

    return ActionResultType.FAIL;
  }

  @SubscribeEvent
  public static void registerSpecialModels(final ModelRegistryEvent registryEvent) {
    //ModelLoader.addSpecialModel(new ResourceLocation(EnergyMetersMod.MODID, "block/meter"));
  }

}
