package com.phazejeff.mcgpt;

import com.google.gson.JsonParser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.tools.*;
import java.io.File;
import java.util.Collections;
import java.lang.reflect.Method;


public class MinecraftGPT implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("minecraftgpt");

	public static final Item BUILD_ITEM = new BuildItem(new FabricItemSettings());

	public static final String EXTERNAL_JSON_FILE_PATH = "C:\\Users\\t-orfranco\\Desktop\\Own Projects\\MinecraftGPT\\src\\main\\java\\com\\phazejeff\\mcgpt\\external.json";

	public static String openai_key;
	public static boolean gpt4 = false;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Starting McGPT!");

		Registry.register(Registries.ITEM, new Identifier("mcgpt", "build"), BUILD_ITEM);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal("gpt4")
			.executes(context -> {
				gpt4 = !gpt4;
				context.getSource().sendMessage(Text.of("GPT4 now set to " + gpt4));
				return 0;
			})
		));
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal("setkey")
			.then(argument("key", StringArgumentType.greedyString())
				.executes(context -> {
					openai_key = StringArgumentType.getString(context, "key");

					context.getSource().sendMessage(Text.of("Open AI key set. Use /build to get started."));
					return 1;
				})
			)

		));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal("build")
			.requires(source -> source.isExecutedByPlayer())
			.then(argument("prompt", StringArgumentType.greedyString())
				.executes(context -> {
					if (openai_key == null) {
						context.getSource().sendMessage(Text.of("Please set your openai key with /setkey"));
						return 0;
					}

					try {
					Long startTime = System.currentTimeMillis();
					
					ServerCommandSource source = context.getSource();
					String prompt = StringArgumentType.getString(context, "prompt");
					source.sendMessage(Text.of("Building " + prompt + "..."));

					new Thread(() -> {
						List<String> messages = new ArrayList<String>();
						messages.add("Build " + prompt);

						BlockPos blockPos = Build.getTargettedBlock(source);
						JsonObject build = OpenAI.promptBuild(prompt);
						messages.add(build.toString());

						ServerWorld world = source.getWorld();

						Build.build(build, blockPos.getX(), blockPos.getY(), blockPos.getZ(), world);
						
						
						BuildItem buildItem = Build.makeBuildItem(messages, blockPos);
						ItemStack itemStack = buildItem.getItemStack(messages, blockPos.getX(), blockPos.getY(), blockPos.getZ());

						itemStack.setCustomName(Text.of(prompt));

						source.getPlayer().giveItemStack(itemStack);
						long endTime = System.currentTimeMillis();
						source.sendMessage(Text.of("Done in " + (float) ((endTime - startTime) / 1000.0f) + " seconds"));
					}).start();

					} catch (Exception e) {
						e.printStackTrace();
						context.getSource().sendMessage(Text.of(e.toString()));
					}
					
					return 1;
				})
			)
		));
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal("edit")
			.requires(source -> source.isExecutedByPlayer() )
			.then(argument("prompt", StringArgumentType.greedyString())
				.executes(context -> {
					if (openai_key == null) {
						context.getSource().sendMessage(Text.of("Please set your openai key with /setkey"));
						return 0;
					}

					try {
					Long startTime = System.currentTimeMillis();

					ServerCommandSource source = context.getSource();
					String prompt = StringArgumentType.getString(context, "prompt");
					source.sendMessage(Text.of("Edit: " + prompt + "..."));

					ItemStack buildItemStack = source.getPlayer().getMainHandStack();

					if (!buildItemStack.getItem().equals(BUILD_ITEM)) {
						source.sendMessage(Text.of("Please be holding a build item (given by /build) to use this."));
						return 1;
					}

					BuildItem buildItem = (BuildItem) buildItemStack.getItem();
					NbtCompound nbt = buildItemStack.getNbt();
					Text name = buildItemStack.getName();

					int x = nbt.getInt("x");
					int y = nbt.getInt("y");
					int z = nbt.getInt("z");

					List<String> messages = new ArrayList<String>();

					for (int i=0; i < nbt.getInt("size"); i++) {
						String m = nbt.getString(String.valueOf(i));
						messages.add(m);
					}
					messages.add(prompt);

					new Thread(() -> {
						JsonObject edit = OpenAI.promptEdit(messages);
						Build.build(edit, x, y, z, source.getWorld());

						messages.add(edit.toString());
						ItemStack newBuildItemStack = buildItem.updateItemStack(buildItemStack.getNbt(), messages);
						buildItemStack.setNbt(newBuildItemStack.getNbt());
						buildItemStack.setCustomName(name);

						long endTime = System.currentTimeMillis();
						source.sendMessage(Text.of("Done in " + (float) ((endTime - startTime) / 1000) + " seconds"));
					}).start();
					
					} catch (Exception e) {
						e.printStackTrace();
						context.getSource().sendMessage(Text.of(e.toString()));
					}
					return 1;
				})
			)
		));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				literal("external")
				.requires(source -> source.isExecutedByPlayer() )
				.then(argument("prompt", StringArgumentType.greedyString())
					.executes(context -> {
						try {
							Long startTime = System.currentTimeMillis();

							ServerCommandSource source = context.getSource();
							String prompt = StringArgumentType.getString(context, "prompt");
							source.sendMessage(Text.of("Building " + prompt + "..."));


							List<String> messages = new ArrayList<String>();
							messages.add("Build " + prompt);

							String externalFileContent = new String(Files.readAllBytes(Paths.get(EXTERNAL_JSON_FILE_PATH)));
							JsonObject externalFileJson = JsonParser.parseString(externalFileContent).getAsJsonObject();

							BlockPos blockPos = Build.getTargettedBlock(source);
							ServerWorld world = source.getWorld();

							Build.build(externalFileJson, blockPos.getX(), blockPos.getY(), blockPos.getZ(), world);


							BuildItem buildItem = Build.makeBuildItem(messages, blockPos);
							ItemStack itemStack = buildItem.getItemStack(messages, blockPos.getX(), blockPos.getY(), blockPos.getZ());

							itemStack.setCustomName(Text.of(prompt));

							source.getPlayer().giveItemStack(itemStack);
							long endTime = System.currentTimeMillis();
							source.sendMessage(Text.of("Done in " + (float) ((endTime - startTime) / 1000.0f) + " seconds"));
						} catch (Exception e) {
							e.printStackTrace();
							context.getSource().sendMessage(Text.of(e.toString()));
						}

						return 1;
								})
						)
		));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				literal("try")
				.requires(source -> source.isExecutedByPlayer() )
				.then(argument("prompt", StringArgumentType.greedyString())
				.executes(context -> {
					ServerCommandSource source = context.getSource();
					BlockPos blockPos = Build.getTargettedBlock(source);
					ServerWorld world = source.getWorld();
					Build.buildStructure(world, blockPos);
					return 1;
		})
		)
		));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				literal("compile")
				.requires(source -> source.isExecutedByPlayer() )
				.then(argument("prompt", StringArgumentType.greedyString())
				.executes(context -> {

					ServerCommandSource source = context.getSource();
					BlockPos blockPos = Build.getTargettedBlock(source);
					ServerWorld world = source.getWorld();
					String prompt = StringArgumentType.getString(context, "prompt");


					String filePath = "C:\\Users\\t-orfranco\\Desktop\\Own Projects\\MinecraftGPT\\src\\main\\java\\com\\phazejeff\\mcgpt\\gptBuilders\\"+prompt+".java";
					String outputDir = "C:\\Users\\t-orfranco\\Desktop\\Own Projects\\MinecraftGPT\\build\\classes\\java\\main";
					JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
					try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
						// Configure the file manager to use the desired output directory
						fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(new File(outputDir)));

						// Get a compilation unit (file) from the file manager
						Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(Collections.singletonList(filePath));

						// Perform the compilation
						JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnits);
						boolean result = task.call();

						if (result) {
							System.out.println("Compilation successful.");
						} else {
							System.out.println("Compilation failed.");
							return 1;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

					try {
						// Load the compiled class
						ClassLoader classLoader = ServerWorld.class.getClassLoader();
						Class<?> clazz = classLoader.loadClass("com.phazejeff.mcgpt.gptBuilders." + prompt);

						// Get the method to be invoked
						Method method = clazz.getMethod("buildStructure", ServerWorld.class, BlockPos.class);

						// Invoke the method with null arguments (or provide actual arguments)
						method.invoke(null, world, blockPos);
					} catch (Exception e) {
						e.printStackTrace();
					}

					return 1;
				})
			)
		));

	}
}