package com.vladmarica.energymeters;

import com.vladmarica.energymeters.block.Blocks;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterFE;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registration {
    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, EnergyMetersMod.MODID);

    public static final RegistryObject<TileEntityType<TileEntityEnergyMeterFE>> TE_Energy_Meter_FE = TILE_ENTITIES.register("energy_meter", () ->
            TileEntityType.Builder
                    .create(TileEntityEnergyMeterFE::new, Blocks.ENERGY_METER_FE)
                    .build(null));
    // ================================================================================================================
    //   INITIALIZATION
    // ================================================================================================================
    public static void init() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //BLOCKS.register(eventBus);
        //ITEMS.register(eventBus);
        TILE_ENTITIES.register(eventBus);
    }
}
