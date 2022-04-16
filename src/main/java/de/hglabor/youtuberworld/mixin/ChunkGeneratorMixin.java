package de.hglabor.youtuberworld.mixin;

import de.hglabor.youtuberworld.config.HeadConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {
    @Inject(method = "generateFeatures", at = @At("TAIL"))
    private void hiFox(ChunkRegion region, StructureAccessor accessor, CallbackInfo ci) {
        ChunkPos center = region.getCenterPos();

        var head = HeadConfig.getRandomHead();
        if (head == null) {
            return;
        }

        var blocks = HeadConfig.getBlocks(head);

        for (int x = center.getStartX(); x <= center.getEndX(); ++x) {
            for (int y = region.getBottomY(); y <= region.getTopY(); ++y) {
                for (int z = center.getStartZ(); z <= center.getEndZ(); ++z) {
                    Pair<Color, Integer> colorIntegerPair = head.get(x & 0xF).get(z & 0xF);
                    Block block = blocks.get(colorIntegerPair.getRight());
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState currentBlock = region.getBlockState(pos);
                    if (currentBlock.isAir() || currentBlock.getMaterial().isLiquid() || currentBlock.isOf(Blocks.CHEST) || currentBlock.isOf(Blocks.BEDROCK) || currentBlock.isOf(Blocks.END_PORTAL_FRAME))
                        continue;
                    region.setBlockState(pos, block.getDefaultState(), Block.NOTIFY_LISTENERS);
                }
            }
        }
    }
}
