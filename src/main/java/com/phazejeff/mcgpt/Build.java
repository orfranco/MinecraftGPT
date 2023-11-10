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


import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;



public class Build {

    public static void build(JsonObject build, int startX, int startY, int startZ, ServerWorld world) {
        List<JsonElement> blocks = build.get("blocks").getAsJsonArray().asList();

        for (JsonElement b : blocks) {
            JsonObject block = b.getAsJsonObject();
            int blockX = block.get("x1").getAsInt();
            int blockY = block.get("y1").getAsInt();
            int blockZ = block.get("z1").getAsInt();
            String blockType = block.get("type").getAsString();

            boolean fill = false;
            try {
                fill = block.get("fill").getAsBoolean();
            } catch (NullPointerException e) {
                fill = false;
            }

            if (fill) {
                int endBlockX = block.get("x2").getAsInt();
                int endBlockY = block.get("y2").getAsInt();
                int endBlockZ = block.get("z2").getAsInt();

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
        if (id == null) {
            // This will handle the case where the identifier could not be parsed correctly.
            System.out.println("Invalid block identifier: " + blockType);
            return Blocks.BRICKS.getDefaultState();
        }
        if (!Registries.BLOCK.containsId(id)){
            System.out.println("Invalid block identifier: " + blockType);
            return Blocks.BRICKS.getDefaultState();
        }
        Block blockMC = Registries.BLOCK.get(id);

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

    public static void buildStructure(ServerWorld world, BlockPos startingPos) {
        // Build the base of the spaceship
        buildCylinder(world, startingPos, 5, 1, Blocks.IRON_BLOCK);

        // Build the body of the spaceship with a tapering shape
        for (int i = 0; i < 6; i++) {
            buildCylinder(world, startingPos.up(i + 1), 5 - i, 1, Blocks.IRON_BLOCK);
        }

        // Build the cockpit using glass
        buildDome(world, startingPos.up(7), 2, Blocks.GLASS);

        // Add details to the body
        addDetailsToBody(world, startingPos.up(1));

        // Build the engines at the base
        buildEngines(world, startingPos.down(2));
    }

    private static void buildCylinder(ServerWorld world, BlockPos center, int radius, int height, Block block) {
        for (int y = 0; y < height; y++) {
            for (int i = 0; i < 360; i += 10) {
                double angle = Math.toRadians(i);
                int x = (int) (center.getX() + radius * Math.cos(angle));
                int z = (int) (center.getZ() + radius * Math.sin(angle));
                BlockPos pos = new BlockPos(x, center.getY() + y, z);
                world.setBlockState(pos, block.getDefaultState());
            }
        }
    }

    private static void buildDome(ServerWorld world, BlockPos center, int radius, Block block) {
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (i * i + j * j <= radius * radius) {
                    BlockPos pos = new BlockPos(center.getX() + i, center.getY(), center.getZ() + j);
                    world.setBlockState(pos, block.getDefaultState());
                }
            }
        }
    }

    private static void addDetailsToBody(ServerWorld world, BlockPos center) {
        // Use different blocks like buttons, levers, and lights to add details
        Block buttonBlock = Blocks.STONE_BUTTON;
        Block redstoneBlock = Blocks.REDSTONE_BLOCK;
        Block leverBlock = Blocks.LEVER;
        // Details are abstract as real functionality in Minecraft is not possible for a spaceship
        BlockPos buttonPos = center.add(0, 1, 2);
        BlockPos redstonePos = center.add(0, 1, -2);
        BlockPos leverPos = center.add(2, 1, 0);
        world.setBlockState(buttonPos, buttonBlock.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
        world.setBlockState(redstonePos, redstoneBlock.getDefaultState());
        world.setBlockState(leverPos, leverBlock.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.EAST));
    }

    private static void buildEngines(ServerWorld world, BlockPos center) {
        // Use blocks to create a pair of engines
        BlockPos engine1Pos = center.add(2, 0, 2);
        BlockPos engine2Pos = center.add(-2, 0, 2);
        Block engineBlock = Blocks.OBSIDIAN;
        world.setBlockState(engine1Pos, engineBlock.getDefaultState());
        world.setBlockState(engine2Pos, engineBlock.getDefaultState());
        // Add flame using campfires or other blocks representing fire
        Block flameBlock = Blocks.CAMPFIRE;
        world.setBlockState(engine1Pos.down(), flameBlock.getDefaultState().with(Properties.LIT, true));
        world.setBlockState(engine2Pos.down(), flameBlock.getDefaultState().with(Properties.LIT, true));
    }
}
