package com.phazejeff.mcgpt;

import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.block.enums.DoubleBlockHalf;

public class Build {

    public static void build(JsonObject build, int startX, int startY, int startZ, ServerWorld world) {
        List<JsonElement> blocks = build.get("blocks").getAsJsonArray().asList();

        for (JsonElement b : blocks) {
            JsonObject block = b.getAsJsonObject();
            int blockX = block.get("startX").getAsInt();
            int blockY = block.get("startY").getAsInt();
            int blockZ = block.get("startZ").getAsInt();
            String blockType = block.get("type").getAsString();

            boolean fill = false;
            try {
                fill = block.get("fill").getAsBoolean();
            } catch (NullPointerException e) {
                fill = false;
            }

            if (fill) {
                int endBlockX = block.get("endX").getAsInt();
                int endBlockY = block.get("endY").getAsInt();
                int endBlockZ = block.get("endZ").getAsInt();

                int lengthX = Math.abs(blockX - endBlockX);
                int lengthY = Math.abs(blockY - endBlockY);
                int lengthZ = Math.abs(blockZ - endBlockZ);

                fillArea(
                    startX + blockX, startY + blockY, startZ + blockZ, 
                    lengthX, lengthY, lengthZ, 
                    blockType, world
                );
            } else {
                placeBlock(
                    startX + blockX, startY + blockY, startZ + blockZ, 
                    blockType, world
                );
            } 
        }
    }

    private static void fillArea(
        int startX, int startY, int startZ, 
        int lengthX, int lengthY, int lengthZ, 
        String blockType, ServerWorld world
    ) {
        for (int x=0; x <= lengthX; x++) {
            for (int y=0; y <= lengthY; y++) {
                for (int z=0; z <= lengthZ; z++) {
                    placeBlock(
                        startX + x, startY + y, startZ + z, 
                        blockType, world
                    );
                }
            }
        }
    }

    private static void placeBlock(int x, int y, int z, String blockType, ServerWorld world) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState blockState = getBlockState(blockType);
        world.setBlockState(pos, blockState);
    }

    private static BlockState getBlockState(String blockType) {
        Identifier id = Identifier.tryParse(blockType);
        Block blockMC = Registries.BLOCK.get(id);
        if (blockMC == null) {
            System.out.println("Couldn't find block for " + blockType);
        }
        BlockState blockState = blockMC.getDefaultState();

        return blockState;
    }

    public static BlockPos getTargettedBlock(ServerCommandSource source) {
        HitResult blockHit = source.getPlayer().getCameraEntity().raycast(20.0D, 0.0f, true);
        if (blockHit.getType() != HitResult.Type.BLOCK) {
            source.sendError(Text.literal("Must be looking at a block!"));
            return new BlockPos(0, 0, 0);
        }

        BlockPos blockPos = ((BlockHitResult) blockHit).getBlockPos();
        return blockPos;
    }

    public static BuildItem makeBuildItem(List<String> messages, BlockPos zeroLocation) {
        Pos pos = new Pos(zeroLocation.getX(), zeroLocation.getY(), zeroLocation.getZ());
        Chat chat = new Chat(pos, messages);

        Identifier id = Identifier.of("mcgpt", "build");
        BuildItem buildItem = (BuildItem) Registries.ITEM.get(id);
        
        buildItem.setChat(chat);

        return buildItem;
    }

    public static void buildHouse(ServerWorld world, BlockPos startingPos) {
        // Dimensions
        int width = 15;
        int depth = 15;
        int height = 6;

        // Materials
        BlockState oakPlanks = Blocks.OAK_PLANKS.getDefaultState();
        BlockState birchPlanks = Blocks.BIRCH_PLANKS.getDefaultState();
        BlockState glassPane = Blocks.GLASS_PANE.getDefaultState();
        BlockState brick = Blocks.BRICKS.getDefaultState();
        BlockState bed = Blocks.RED_BED.getDefaultState();
        BlockState carpet = Blocks.RED_CARPET.getDefaultState();
        BlockState doorBottom = Blocks.OAK_DOOR.getDefaultState();
        BlockState doorTop = Blocks.OAK_DOOR.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER);

        // Base and Walls
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                // Base
                world.setBlockState(startingPos.add(x, 0, z), oakPlanks);

                // Walls
                for (int y = 1; y < height; y++) {
                    boolean isEdge = x == 0 || x == width - 1 || z == 0 || z == depth - 1;

                    if (isEdge) {
                        world.setBlockState(startingPos.add(x, y, z), birchPlanks);
                    }

                    // Windows
                    if ((y == 2 || y == 4) && !isEdge) {
                        world.setBlockState(startingPos.add(x, y, z), glassPane);
                    }
                }
            }
        }

        // Entrance door
        world.setBlockState(startingPos.add(7, 1, 0), doorBottom);
        world.setBlockState(startingPos.add(7, 2, 0), doorTop);

        // Fireplace in the living room
        for (int y = 1; y <= 3; y++) {
            world.setBlockState(startingPos.add(3, y, 3), brick);
            if (y == 3) {
                world.setBlockState(startingPos.add(3, y, 3), Blocks.FIRE.getDefaultState());
            }
        }

        // Bedroom setup
        world.setBlockState(startingPos.add(11, 1, 11), bed);
        world.setBlockState(startingPos.add(11, 1, 12), bed);
        for (int x = 10; x <= 12; x++) {
            for (int z = 10; z <= 12; z++) {
                world.setBlockState(startingPos.add(x, 1, z), carpet);
            }
        }

        // Kitchen counter using oak slabs
        for (int z = 2; z <= 4; z++) {
            world.setBlockState(startingPos.add(12, 1, z), Blocks.OAK_SLAB.getDefaultState());
        }

        // Storage chest in the storage room
        world.setBlockState(startingPos.add(2, 1, 12), Blocks.CHEST.getDefaultState());

        // Room partitions
        for (int y = 1; y < height; y++) {
            // Dividing living room and bedroom
            for (int x = 8; x < width; x++) {
                world.setBlockState(startingPos.add(x, y, 7), birchPlanks);
            }

            // Dividing bedroom and kitchen
            for (int z = 8; z < depth; z++) {
                world.setBlockState(startingPos.add(10, y, z), birchPlanks);
            }
        }
    }

}
