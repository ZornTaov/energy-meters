package com.vladmarica.energymeters.block;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.Registration;
import com.vladmarica.energymeters.energy.EnergyType;
import com.vladmarica.energymeters.energy.EnergyTypes;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterFE;
import com.vladmarica.energymeters.tile.TileEntityTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import net.minecraft.block.AbstractBlock;

public class BlockEnergyMeter extends Block {

  public static final DirectionProperty PROP_FACING = DirectionProperty.create("facing", Plane.HORIZONTAL);

  private final MeterType meterType;

  public BlockEnergyMeter(MeterType meterType) {
    super(
        AbstractBlock.Properties.create(Material.IRON)
            .harvestTool(ToolType.PICKAXE)
            .sound(SoundType.STONE)
            .hardnessAndResistance(3.5F));

    this.meterType = meterType;
    this.setDefaultState(this.stateContainer.getBaseState().with(PROP_FACING, Direction.NORTH));//.with(INPUT_FACING,Direction.WEST).with(OUTPUT_FACING, Direction.EAST));
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new TileEntityEnergyMeterFE();
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void neighborChanged(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos,
                              @Nonnull Block block, @Nonnull BlockPos fromPos, boolean isMoving) {
    super.neighborChanged(state, world, pos, block, fromPos, isMoving);

    TileEntityEnergyMeterBase tile = (TileEntityEnergyMeterBase) world.getTileEntity(pos);
    if (tile != null) {
      tile.onNeighborChanged(fromPos, state);
    }
  }

  @Nonnull
  @Override
  @SuppressWarnings("deprecation")
  public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos,
                                           @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult rayTraceResult) {
    return EnergyMetersMod.proxy.handleEnergyBlockActivation(world, pos, player);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    if (context.getPlayer() != null) {
      return this.getDefaultState()
          .with(PROP_FACING, getFacingFromEntity(context.getPos(), context.getPlayer()));
    }
    return super.getStateForPlacement(context);
  }

  @Override
  protected void fillStateContainer(Builder<Block, BlockState> builder) {
    builder.add(PROP_FACING);
  }

  public MeterType getMeterType() {
    return this.meterType;
  }

  private static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
    Direction facing =  Direction.getFacingFromVector(
        (float) (entity.getPosX() - clickedBlock.getX()),
        (float) (entity.getPosY() - clickedBlock.getY()),
        (float) (entity.getPosZ() - clickedBlock.getZ()));

    if (facing.getAxis() == Direction.Axis.Y) {
      facing = Direction.NORTH;
    }

    return facing;
  }

  public enum MeterType implements IStringSerializable {
    FE_METER(0, EnergyTypes.FE);

    private final int index;
    private final EnergyType type;

    MeterType(int index, EnergyType type) {
      this.index = index;
      this.type = type;
    }

    public int getIndex() {
      return this.index;
    }

    public EnergyType getEnergyType() {
      return this.type;
    }

    @Override
    public String getString() {
      return this.type.getName().toLowerCase();
    }
  }
}
