package com.phazejeff.mcgpt.gptBuilders;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class RedDragonBuilder {
    public static void buildStructure(ServerWorld world, BlockPos startingPos) {
        buildDragonHead(world, startingPos);
        buildDragonBody(world, startingPos.add(5, 0, 0));
        buildDragonLegs(world, startingPos.add(10, -3, 0));
        buildDragonTail(world, startingPos.add(35, 0, 0));
    }

    private static void buildDragonHead(ServerWorld world, BlockPos pos) {
        // Base of the dragon head
        setBlocksInArea(world, pos, 5, 4, 5, Blocks.RED_CONCRETE);

        // Recessed areas to give a curved snout look
        world.setBlockState(pos.add(0, 2, 0), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.add(0, 2, 4), Blocks.AIR.getDefaultState());

        // Eyes using black blocks with white surround
        world.setBlockState(pos.add(1, 3, 1), Blocks.WHITE_CONCRETE.getDefaultState());
        world.setBlockState(pos.add(1, 3, 3), Blocks.WHITE_CONCRETE.getDefaultState());
        world.setBlockState(pos.add(2, 3, 1), Blocks.BLACK_CONCRETE.getDefaultState());
        world.setBlockState(pos.add(2, 3, 3), Blocks.BLACK_CONCRETE.getDefaultState());

        // Nostrils
        world.setBlockState(pos.add(0, 2, 1), Blocks.BLACK_CONCRETE.getDefaultState());
        world.setBlockState(pos.add(0, 2, 3), Blocks.BLACK_CONCRETE.getDefaultState());

        // Horns using red terracotta for variance
        setBlocksInArea(world, pos.add(-1, 4, 1), 1, 2, 1, Blocks.RED_TERRACOTTA);
        setBlocksInArea(world, pos.add(-1, 4, 3), 1, 2, 1, Blocks.RED_TERRACOTTA);

        // Under-chin and neck: added curvature here
        setBlocksInArea(world, pos.add(0, 0, 1), 5, 1, 3, Blocks.RED_CONCRETE);
        setBlocksInArea(world, pos.add(0, 1, 1), 4, 1, 3, Blocks.RED_CONCRETE);

        // Top of the head with scales
        setBlocksInArea(world, pos.add(0, 4, 0), 5, 1, 5, Blocks.RED_TERRACOTTA);

        // Create a curvature for the back of the head
        world.setBlockState(pos.add(4, 3, 0), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.add(4, 3, 4), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.add(4, 2, 0), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.add(4, 2, 4), Blocks.AIR.getDefaultState());

        // Create a curvature for the sides of the head
        world.setBlockState(pos.add(0, 3, 0), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.add(0, 3, 4), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.add(0, 2, 4), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.add(4, 3, 4), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.add(4, 3, 0), Blocks.AIR.getDefaultState());

        // Modify the under-chin and neck for curvature
        world.setBlockState(pos.add(1, 0, 0), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.add(1, 0, 4), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.add(2, 0, 0), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.add(2, 0, 4), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.add(3, 0, 0), Blocks.AIR.getDefaultState());
        world.setBlockState(pos.add(3, 0, 4), Blocks.AIR.getDefaultState());
    }

    private static void buildDragonTail(ServerWorld world, BlockPos pos) {
        for (int i = 0; i < 20; i++) {
            int height = (i < 10) ? 2 : 1;
            int width = (i < 5) ? 3 : (i < 10) ? 2 : 1;  // Added curvature here
            setBlocksInArea(world, pos.add(i, 0, 2 - width/2), 1, height, width, Blocks.RED_CONCRETE);  // Centering the tail blocks

            // White details on the tail
            if (i % 5 == 0) {
                setBlocksInArea(world, pos.add(i, 0, 2 - width/2), 1, 1, 1, Blocks.WHITE_CONCRETE);  // Centering the white detail
            }
        }
    }

    private static void buildDragonBody(ServerWorld world, BlockPos pos) {
        for (int i = 0; i < 30; i++) {
            int height = 2 + (i % 6);
            int width = (i % 6 == 0 || i % 6 == 5) ? 3 : 4;  // Added curvature here

            // White underbelly
            if (i < 15) {
                world.setBlockState(pos.add(i, 0, (width-1)/2), Blocks.WHITE_CONCRETE.getDefaultState());  // Center the white belly
            }

            for (int h = 0; h < height; h++) {
                setBlocksInArea(world, pos.add(i, h, 0), 1, 1, width, Blocks.RED_CONCRETE);
            }

            // Scales on the body
            if (i % 6 == 0) {
                setBlocksInArea(world, pos.add(i, height, 1), 1, 1, 2, Blocks.RED_TERRACOTTA);
            }
        }
    }



    private static void buildDragonLegs(ServerWorld world, BlockPos pos) {
        // Front legs
        setBlocksInArea(world, pos, 2, 3, 2, Blocks.RED_CONCRETE);
        setBlocksInArea(world, pos.add(0, 0, 3), 2, 3, 2, Blocks.RED_CONCRETE);

        // White claws for front legs
        setBlocksInArea(world, pos.add(2, 0, 0), 1, 1, 1, Blocks.WHITE_CONCRETE);
        setBlocksInArea(world, pos.add(2, 0, 4), 1, 1, 1, Blocks.WHITE_CONCRETE);

        // Rear legs
        setBlocksInArea(world, pos.add(20, 0, 0), 2, 3, 2, Blocks.RED_CONCRETE);
        setBlocksInArea(world, pos.add(20, 0, 3), 2, 3, 2, Blocks.RED_CONCRETE);

        // White claws for rear legs
        setBlocksInArea(world, pos.add(22, 0, 0), 1, 1, 1, Blocks.WHITE_CONCRETE);
        setBlocksInArea(world, pos.add(22, 0, 4), 1, 1, 1, Blocks.WHITE_CONCRETE);
    }


    private static void setBlocksInArea(ServerWorld world, BlockPos start, int dx, int dy, int dz, Block blockType) {
        for (int x = 0; x < dx; x++) {
            for (int y = 0; y < dy; y++) {
                for (int z = 0; z < dz; z++) {
                    world.setBlockState(start.add(x, y, z), blockType.getDefaultState());
                }
            }
        }
    }
}
