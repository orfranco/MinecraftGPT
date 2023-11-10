package com.phazejeff.mcgpt.gptBuilders;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class TajMahalBuilder {

    public static void buildStructure(ServerWorld world, BlockPos startingPos) {
        // The subsequent parts will fill in the details of the build.
        buildMainBuilding(world, startingPos);
        buildMinarets(world, startingPos);
        buildPlatform(world, startingPos);
        buildGardens(world, startingPos);
    }

    private static void buildMainBuilding(ServerWorld world, BlockPos startingPos) {
        buildBase(world, startingPos);
        buildDomes(world, startingPos);
        addDecorativeElements(world, startingPos);
    }


    private static void buildBase(ServerWorld world, BlockPos startingPos) {
        // Example dimensions and block type for the base

        BlockState whiteBlock = Blocks.QUARTZ_BLOCK.getDefaultState();
        int baseWidth = 30; // width of the base
        int baseHeight = 20; // height of the walls
        for (int x = 0; x < baseWidth; x++) {
            for (int z = 0; z < baseWidth; z++) {
                for (int y = 0; y < baseHeight; y++) {
                    BlockPos pos = startingPos.add(x, y, z);
                    world.setBlockState(pos, whiteBlock, 3);
                }
            }
        }
    }

    private static void buildDomes(ServerWorld world, BlockPos startingPos) {
        int baseWidth = 30; // width of the base
        int baseHeight = 20; // height of the walls
        // Code for the central dome
        buildDome(world, startingPos.add(15, 20, 15), 10, Blocks.QUARTZ_STAIRS.getDefaultState(), Blocks.QUARTZ_SLAB.getDefaultState());

        // Four smaller domes at the corners
        int baseCorner = 5;
        int domeRadius = 5;
        for (int[] corner : new int[][]{{baseCorner, baseCorner}, {baseCorner, baseWidth - baseCorner}, {baseWidth - baseCorner, baseCorner}, {baseWidth - baseCorner, baseWidth - baseCorner}}) {
            buildDome(world, startingPos.add(corner[0], 20, corner[1]), domeRadius, Blocks.QUARTZ_STAIRS.getDefaultState(), Blocks.QUARTZ_SLAB.getDefaultState());
        }
    }

    private static void buildDome(ServerWorld world, BlockPos center, int radius, BlockState stairs, BlockState slab) {
        // Loop to create a half-sphere shape for the dome
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = 0; y <= radius; y++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance > radius - 0.5 && distance < radius + 0.5) {
                        BlockPos pos = center.add(x, y, z);
                        world.setBlockState(pos, stairs, 3); // Use stairs for the curved surface
                    }
                }
            }
        }
        // Add the top of the dome using slabs
        BlockPos top = center.add(0, radius, 0);
        world.setBlockState(top, slab, 3);
    }


    private static void addDecorativeElements(ServerWorld world, BlockPos startingPos) {
        int baseWidth = 30;
        int baseHeight = 20;
        BlockState goldBlock = Blocks.GOLD_BLOCK.getDefaultState(); // For ornamental touches
        BlockState lapisBlock = Blocks.LAPIS_BLOCK.getDefaultState(); // For blue inlays
        BlockState glazedTerracotta = Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.getDefaultState(); // For patterned details
        BlockState chiseledQuartzBlock = Blocks.CHISELED_QUARTZ_BLOCK.getDefaultState(); // For intricate details

        // Add detailed arches around doorways and windows
        createArches(world, startingPos, baseWidth, baseHeight, chiseledQuartzBlock);

        // Add gold and lapis inlays for decoration
        addInlays(world, startingPos, baseWidth, baseHeight, goldBlock, lapisBlock);

        // Create a patterned band using glazed terracotta across the facade
        for (int x = 0; x < baseWidth; x++) {
            BlockPos bandPos = startingPos.add(x, baseHeight / 2, 0);
            world.setBlockState(bandPos, glazedTerracotta, 3);
        }

        // Additional decorative elements can include flower beds, fountains, and sitting areas in the gardens
        createGardenFeatures(world, startingPos, baseWidth);
    }

    private static void createArches(ServerWorld world, BlockPos startingPos, int baseWidth, int baseHeight, BlockState chiseledQuartzBlock) {
        // Assuming an arch radius of 3 blocks
        int archRadius = 3;
        for (int x = 0; x < baseWidth; x += 10) {
            for (int y = baseHeight / 2; y <= baseHeight / 2 + archRadius; y++) {
                int archHeight = y - (baseHeight / 2);
                int width = (int) Math.sqrt(archRadius * archRadius - archHeight * archHeight);
                for (int xOffset = -width; xOffset <= width; xOffset++) {
                    BlockPos archPos = startingPos.add(x + xOffset, y, 0);
                    world.setBlockState(archPos, chiseledQuartzBlock, 3);
                }
            }
        }
    }

    private static void addInlays(ServerWorld world, BlockPos startingPos, int baseWidth, int baseHeight, BlockState goldBlock, BlockState lapisBlock) {
        // Create a checkerboard pattern of inlays
        for (int y = 4; y < baseHeight; y += 4) {
            for (int x = 1; x < baseWidth; x += 2) {
                BlockPos goldPos = startingPos.add(x, y, 0);
                BlockPos lapisPos = startingPos.add(x + 1, y, 0);
                world.setBlockState(goldPos, goldBlock, 3);
                world.setBlockState(lapisPos, lapisBlock, 3);
            }
        }
    }


    private static void createGardenFeatures(ServerWorld world, BlockPos startingPos, int baseWidth) {
        // Flower beds
        BlockState flowerBlock = Blocks.ROSE_BUSH.getDefaultState(); // Example flower block
        int gardenDepth = 10; // The depth of the garden space
        for (int x = 0; x < baseWidth; x += 5) {
            for (int z = baseWidth; z < baseWidth + gardenDepth; z += 5) {
                BlockPos flowerPos = startingPos.add(x, 1, z);
                world.setBlockState(flowerPos, flowerBlock, 3);
            }
        }

        // Fountains
        BlockState waterBlock = Blocks.WATER.getDefaultState();
        BlockPos fountainCenter = startingPos.add(baseWidth / 2, 1, baseWidth + gardenDepth / 2);
        world.setBlockState(fountainCenter, waterBlock, 3);

        // Pathways
        BlockState pathwayBlock = Blocks.GRAVEL.getDefaultState();
        for (int z = baseWidth; z < baseWidth + gardenDepth; z++) {
            BlockPos pathPos = startingPos.add(baseWidth / 2, 1, z);
            world.setBlockState(pathPos, pathwayBlock, 3);
        }
    }

    private static void buildMinarets(ServerWorld world, BlockPos startingPos) {
        int baseWidth = 30; // Assuming the baseWidth is the width of the main building
        int minaretHeight = 40; // Adjust as necessary for proper scale
        BlockState minaretBlock = Blocks.QUARTZ_BLOCK.getDefaultState();

        // Coordinates for the four corners of the base of the main building
        int[][] corners = {
                {startingPos.getX(), startingPos.getZ()},
                {startingPos.getX(), startingPos.getZ() + baseWidth},
                {startingPos.getX() + baseWidth, startingPos.getZ()},
                {startingPos.getX() + baseWidth, startingPos.getZ() + baseWidth}
        };

        for (int[] corner : corners) {
            BlockPos basePos = new BlockPos(corner[0], startingPos.getY(), corner[1]);
            for (int y = 0; y < minaretHeight; y++) {
                // Construct a simple cylindrical shape for the minaret
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x * x + z * z <= 1) { // Inside the cylinder radius
                            BlockPos pos = basePos.add(x, y, z);
                            world.setBlockState(pos, minaretBlock, 3);
                        }
                    }
                }
            }
        }
    }

    private static void buildPlatform(ServerWorld world, BlockPos startingPos) {
        int baseWidth = 30;
        int platformHeight = 5; // Height of the platform
        BlockState platformBlock = Blocks.SMOOTH_QUARTZ.getDefaultState();

        for (int x = -5; x <= baseWidth + 5; x++) {
            for (int z = -5; z <= baseWidth + 5; z++) {
                for (int y = 0; y < platformHeight; y++) {
                    BlockPos pos = startingPos.add(x, y, z);
                    world.setBlockState(pos, platformBlock, 3);
                }
            }
        }
    }

    private static void buildGardens(ServerWorld world, BlockPos startingPos) {
        int baseWidth = 30;
        int gardenSize = 50; // Total size of the gardens
        BlockState waterBlock = Blocks.WATER.getDefaultState();
        BlockState grassBlock = Blocks.GRASS_BLOCK.getDefaultState();
        BlockState pathBlock = Blocks.GRAVEL.getDefaultState();

        // Create the gardens around the Taj Mahal
        for (int x = -gardenSize; x <= gardenSize; x++) {
            for (int z = -gardenSize; z <= gardenSize; z++) {
                BlockPos pos = startingPos.add(x, 0, z);
                world.setBlockState(pos, grassBlock, 3); // Set grass for the garden base
            }
        }

        // Create the central reflecting pool
        for (int x = -2; x <= 2; x++) {
            for (int z = -gardenSize; z <= gardenSize; z++) {
                BlockPos pos = startingPos.add(x, 1, z);
                world.setBlockState(pos, waterBlock, 3); // Set water for the pool
            }
        }

        // Create pathways
        for (int x = -gardenSize; x <= gardenSize; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos pos = startingPos.add(x, 1, z);
                world.setBlockState(pos, pathBlock, 3); // Set gravel for the pathways
            }
        }
    }


}