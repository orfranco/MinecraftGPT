package com.phazejeff.mcgpt.gptBuilders;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
public class apollo11Builder {

    public static void buildStructure(ServerWorld world, BlockPos startingPos) {
        buildBaseEngine(world, startingPos);
        buildMainBody(world, startingPos.up(20));
        buildCommandModule(world, startingPos.up(80));
        buildEscapeRocket(world, startingPos.up(100));
        addEngineFire(world, startingPos);
        addUSFlag(world, startingPos.up(45).east(12));
    }

    private static void buildBaseEngine(ServerWorld world, BlockPos enginePos) {
        BlockState ironBlock = Blocks.IRON_BLOCK.getDefaultState();
        BlockState blackConcrete = Blocks.BLACK_CONCRETE.getDefaultState();

        for (int y = 0; y < 20; y++) {
            int currentRadius = y < 5 ? 10 : 10 - (y - 5) / 3;
            buildCircle(world, enginePos.up(y), currentRadius, ironBlock);
        }

        // Engine nozzle details
        for (int y = 0; y < 15; y++) {
            world.setBlockState(enginePos.add(0, y, 10), blackConcrete, 3);
            world.setBlockState(enginePos.add(10, y, 0), blackConcrete, 3);
            world.setBlockState(enginePos.add(0, y, -10), blackConcrete, 3);
            world.setBlockState(enginePos.add(-10, y, 0), blackConcrete, 3);
        }
    }

    private static void buildMainBody(ServerWorld world, BlockPos midPos) {
        BlockState whiteConcrete = Blocks.WHITE_CONCRETE.getDefaultState();
        BlockState lightGrayConcrete = Blocks.LIGHT_GRAY_CONCRETE.getDefaultState();
        BlockState redConcrete = Blocks.RED_CONCRETE.getDefaultState();

        for (int y = 0; y < 60; y++) {
            buildCircle(world, midPos.up(y), 10, whiteConcrete);

            // Banding and decals
            if (y % 10 == 0) {
                buildCircle(world, midPos.up(y), 10, lightGrayConcrete);
            }
            if (y == 20 || y == 40) {
                buildCircle(world, midPos.up(y), 10, redConcrete);
            }
        }
    }

    private static void buildCommandModule(ServerWorld world, BlockPos cmdPos) {
        BlockState grayConcrete = Blocks.GRAY_CONCRETE.getDefaultState();
        BlockState blackConcrete = Blocks.BLACK_CONCRETE.getDefaultState();

        for (int y = 0; y < 20; y++) {
            int currentRadius = 10 - y / 2;
            buildCircle(world, cmdPos.up(y), currentRadius, grayConcrete);

            if (y == 10) {
                buildCircle(world, cmdPos.up(y), currentRadius, blackConcrete);  // Command module window band
            }
        }
    }

    private static void buildEscapeRocket(ServerWorld world, BlockPos rocketPos) {
        BlockState lightGrayConcrete = Blocks.LIGHT_GRAY_CONCRETE.getDefaultState();

        for (int y = 0; y < 15; y++) {
            int currentRadius = y < 5 ? 5 : 5 - (y - 5) / 3;
            buildCircle(world, rocketPos.up(y), currentRadius, lightGrayConcrete);
        }
    }

    private static void addEngineFire(ServerWorld world, BlockPos enginePos) {
        BlockState fireBlock = Blocks.FIRE.getDefaultState();

        for (int y = 0; y < 10; y++) {
            int currentRadius = y < 3 ? 8 : 8 - y / 2;
            buildCircle(world, enginePos.down(y), currentRadius, fireBlock);
        }
    }

    private static void addUSFlag(ServerWorld world, BlockPos flagPos) {
        BlockState blueConcrete = Blocks.BLUE_CONCRETE.getDefaultState();
        BlockState whiteConcrete = Blocks.WHITE_CONCRETE.getDefaultState();
        BlockState redConcrete = Blocks.RED_CONCRETE.getDefaultState();
        BlockState flagPole = Blocks.BIRCH_FENCE.getDefaultState();

        // Flagpole
        for (int y = 0; y < 5; y++) {
            world.setBlockState(flagPos.up(y), flagPole, 3);
        }

        // Flag itself
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 5; y++) {
                BlockState currentBlock = ((y % 2) == 0) ? redConcrete : whiteConcrete;
                world.setBlockState(flagPos.add(x + 1, y, 0), currentBlock, 3);

                if (y < 3) {
                    world.setBlockState(flagPos.add(x + 1, y + 3, 0), blueConcrete, 3);
                }
            }
        }
    }

    // Utility method to create a circle of blocks
    private static void buildCircle(ServerWorld world, BlockPos center, int radius, BlockState blockState) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    world.setBlockState(center.add(x, 0, z), blockState, 3);
                }
            }
        }
    }
}