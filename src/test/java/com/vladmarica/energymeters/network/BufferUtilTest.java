package com.vladmarica.energymeters.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BufferUtilTest {
  @Test
  public void testBlockPos() {
    ByteBuf buf = Unpooled.buffer();
    BlockPos pos = new BlockPos(10, -4, 32);

    BufferUtil.writeBlockPos(buf, pos);
    BlockPos newPos = BufferUtil.readBlockPos(buf);

    assertEquals(pos, newPos);
  }

  @Test
  public void testNullFace() {
    ByteBuf buf = Unpooled.buffer();
    Direction face = null;

    BufferUtil.writeNullableFace(buf, face);
    Direction newFace = BufferUtil.readNullableFace(buf);
    assertNull(newFace);
  }

  @Test
  public void testNonNullFace() {

    for (Direction face : Direction.values()) {
      ByteBuf buf = Unpooled.buffer();
      BufferUtil.writeNullableFace(buf, face);

      Direction newFace = BufferUtil.readNullableFace(buf);
      assertEquals(face, newFace);
    }
  }
}
