package com.vladmarica.energymeters.datagen;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class BlockStates extends BlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, EnergyMetersMod.MODID, exFileHelper);
    }

    @Override
    @Nonnull
    public String getName() {
        return "EnergyMetersMod BlockStates";
    }
    @Override
    protected void registerStatesAndModels() {
        //this.horizontalBlock(Blocks.ENERGY_METER_FE);
        //this.getVariantBuilder(Blocks.ENERGY_METER_FE).partialState().with();
    }

    private void floatingCube(BlockModelBuilder builder, float fx, float fy, float fz, float tx, float ty, float tz) {
        builder.element().from(fx, fy, fz).to(tx, ty, tz).allFaces((direction, faceBuilder) -> faceBuilder.texture("#window")).end();
    }
}