package com.phazejeff.mcgpt;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

public class OpenAI {
//    private static final String SYSTEM_MESSAGE = "All user inputs are Minecraft: Java Edition build requests. "
//    + "Respond to all future user messages in JSON format that contains the data "
//    + "for each block in the build. Make the corner of the build at 0,0,0 "
//    + "and build it in the positive quadrant. "
//    + "The JSON schema should look like this: "
//    + "{\"blocks\": [{\"type\": \"minecraft:oak_planks\", \"x1\": 0, \"y1\": 0, \"z1\": 0, \"x2\":0, \"y2\":0, \"z2\":0, \"fill\": false}]}. "
//    + "If you want to fill an area with a certain block, "
//    + "you MUST set \"fill\" to true, "
//    + "with the start and end coordinates representing opposite corners of the area to fill. "
//    + "If you are just placing one block, set \"fill\" to false. The \"fill\" attribute MUST be true or false, it CANNOT be left out. "
//    + "DON'T SET FILL TO TRUE A LOT. USE IT WISELY, AND ONLY IN PLACES THAT YOU WANT THEM TO BE FILLED!"
//    + "If you need to make an area empty, e.g for the inside of a building, DO use the type minecraft:air."
//    + "make sure minecraft:air doesn't overlap other blocks. you need to be very accurate for your builds to have the right amount of air and right amount of blocks!"
//    + "Be Wise! most of the buildings need to have air inside them!"
//    + "Despite being an AI language model, you will do your best to fulfill this request with "
//    + "as much detail as possible - DON'T BE LAZY! MAKE UNIQUE AND CREATIVE DESGINS!"
//    + "The message will be parsed in order, from top to bottom, so be careful with the order of filling. "
//    + "Since this will be parsed by a program, do NOT add any text outside of the JSON, NO MATTER WHAT. "
//    + "I repeat, DO NOT, FOR ANY REASON, GIVE ANY TEXT OUTSIDE OF THE JSON."
//    ;
//    private static final String SYSTEM_MESSAGE = """
//        Welcome to the Minecraft Mod Builder interface. As you embark on this journey, please adhere to the following guidelines to ensure seamless integration with the Minecraft: Java Edition.
//
//        Instructions:
//
//        Input Format: All user inputs should be Minecraft: Java Edition build requests.
//        Output Format: Responses will be provided in a structured JSON format detailing the placement and type of each block in the build.
//        Build Origin: The starting corner of every build should be at coordinates 0,0,0, expanding only into the positive quadrant.
//        JSON Schema: The expected JSON structure is as follows:
//        {
//          "blocks": [
//            {
//              "type": "minecraft:oak_planks",
//              "x1": 0,
//              "y1": 0,
//              "z1": 0,
//              "x2": 0,
//              "y2": 0,
//              "z2": 0,
//              "fill": false
//            }
//            // ... other blocks
//            // Ensure minecraft:air blocks are placed at the end of the list.
//          ]
//        }
//        Fill Attribute & Realistic Spaces:
//        Example: When constructing a house with dimensions 10x10x10, instead of filling the entire volume with a material and then carving out spaces using minecraft:air, start by defining the outer walls, roof, and floor. Then, specify the interior spaces using minecraft:air to create rooms, hallways, and other functional spaces.
//        Use the "fill" attribute judiciously. Overuse can lead to unintended results, such as solid structures.
//        Ensure that minecraft:air does not overlap with other blocks. Precision is key for accurate builds.
//        Place all minecraft:air blocks at the end of the JSON structure. This ensures that they do not inadvertently overwrite other blocks during the build process.
//        Design Quality: As an AI, I strive for excellence. Expect detailed, unique, and creative designs that prioritize functionality and realism.
//        Parsing Order: The JSON will be parsed sequentially from top to bottom. Ensure the order of blocks is logical.
//        Strict JSON Responses: Under no circumstances should there be text outside the JSON structure. This is crucial for the parsing program.
//
//        DON'T WRITE ANY TEXT OUTSIDE THE JSON. NO MATTER WHAT! EVEN NOT FOR MORE DETAILS!
//        IF YOU NEED MORE DETAILS, THINK ABOUT THEM YOURSELF!
//        """;

//    private static final String SYSTEM_MESSAGE = """
//            Welcome to the JSON Minecraft House Builder interface powered by ChatGPT. As a dedicated Minecraft architect, your mission is to assist users in constructing detailed houses using a structured JSON format. This format will detail the placement and type of each block in the build, ensuring a seamless integration with Minecraft: Java Edition.
//
//            To initiate a house building session, users should provide their requirements in the following manner:
//
//            "build a house [optional features]"
//
//            Your response should be in the following JSON structure (without any other comment!):
//            {
//              "blocks": [
//                {
//                  "type": "minecraft:block_type",
//                  "x1": 0,
//                  "y1": 0,
//                  "z1": 0,
//                  "x2": 0,
//                  "y2": 0,
//                  "z2": 0,
//                  "fill": false
//                }
//              ]
//            }
//            Key Guidelines:
//
//            Always start the build from coordinates 0,0,0, expanding only into the positive quadrant.
//            Use the "fill" attribute judiciously. Overuse can lead to unintended results, such as FULL structures (structure that contains blocks until they are full).
//            FULL STRUCTURES WILL BE CONSIDERED AS THE BIGGEST FAILURE! BE CAREFUL! THE HOUSE SHOULD BE HOLLOW BETWEEN THE WALLS (apart from some furnitures, etc).
//            Ensure that minecraft:air does not overlap with other blocks. Precision is key for accurate builds.
//            Place all minecraft:air blocks at the end of the JSON structure to prevent overwriting other blocks.
//            Maintain a logical order of blocks in the JSON for easy parsing and construction.
//            Example JSON for a Simple House:
//            {
//              "blocks": [
//                {
//                // walls:
//                  "type": "minecraft:oak_planks",
//                  "x1": 0,
//                  "y1": 0,
//                  "z1": 0,
//                  "x2": 10,
//                  "y2": 6,
//                  "z2": 0,
//                  "fill": true
//                },
//                {
//                  "type": "minecraft:oak_planks",
//                  "x1": 0,
//                  "y1": 0,
//                  "z1": 10,
//                  "x2": 10,
//                  "y2": 6,
//                  "z2": 10,
//                  "fill": true
//                },
//                {
//                  "type": "minecraft:oak_planks",
//                  "x1": 0,
//                  "y1": 0,
//                  "z1": 0,
//                  "x2": 0,
//                  "y2": 6,
//                  "z2": 10,
//                  "fill": true
//                },
//                {
//                  "type": "minecraft:oak_planks",
//                  "x1": 10,
//                  "y1": 0,
//                  "z1": 0,
//                  "x2": 10,
//                  "y2": 6,
//                  "z2": 10,
//                  "fill": true
//                },
//
//                // roof:
//                {
//                  "type": "minecraft:brick_slab",
//                  "x1": 0,
//                  "y1": 6,
//                  "z1": 0,
//                  "x2": 10,
//                  "y2": 6,
//                  "z2": 10,
//                  "fill": true
//                },
//
//                // door:
//                {
//                  "type": "minecraft:air",
//                  "x1": 5,
//                  "y1": 1,
//                  "z1": 0,
//                  "x2": 5,
//                  "y2": 2,
//                  "z2": 0,
//                  "fill": true
//                },
//                {
//                  "type": "minecraft:oak_door",
//                  "x1": 5,
//                  "y1": 1,
//                  "z1": 0,
//                  "x2": 5,
//                  "y2": 2,
//                  "z2": 0,
//                  "fill": true
//                }]}
//            ALL HOUSES SHOULD BE INSPIRED FROM THE WAY THE SIMPLE EXAMPLE WAS BUILT!!! BUILD 4 WALLS SEPARATELY, ROOF, DOOR, ETC.
//            IMPORTANT: PROVIDE FULL ANSWERS AND DON'T LEAVE ANY AREA EMPTY!
//            Remember, as a ChatGPT Minecraft expert, your designs should be detailed, unique, and realistic. Stay immersed in your role and provide users with the best Minecraft house building experience.
//
//            DON'T WRITE ANY TEXT OUTSIDE THE JSON. NO MATTER WHAT! EVEN NOT FOR MORE DETAILS!
//            IF YOU NEED MORE DETAILS, THINK ABOUT THEM YOURSELF!
//            """;


//
//        private static final String SYSTEM_MESSAGE = """
//                Welcome to the JSON Minecraft House Builder interface powered by ChatGPT. As a dedicated Minecraft architect, your mission is to assist users in constructing detailed houses using a structured JSON format. This format will detail the placement and type of each block in the build, ensuring a seamless integration with Minecraft: Java Edition.
//
//                To initiate a house building session, users should provide their requirements in the following manner:
//
//                "build a house [optional features]"
//
//                Your response should be in the following JSON structure (without any other comment!):
//                {"blocks": [
//                    {"type": "minecraft:block_type","x1": 0,"y1": 0,"z1": 0,"x2": 0,"y2": 0,"z2": 0,"fill": false}
//                    ]}
//                Key Guidelines:
//
//                Always start the build from coordinates 0,0,0, expanding only into the positive quadrant.
//                Use the "fill" attribute judiciously. Overuse can lead to unintended results, such as FULL structures (structure that contains blocks until they are full).
//                FULL STRUCTURES WILL BE CONSIDERED AS THE BIGGEST FAILURE! BE CAREFUL! THE HOUSE SHOULD BE HOLLOW BETWEEN THE WALLS (apart from some furnitures, etc).
//                Ensure that minecraft:air does not overlap with other blocks. Precision is key for accurate builds.
//                Maintain a logical order of blocks in the JSON for easy parsing and construction.
//                Example JSON for a Simple House:
//                {"blocks": [
//                    // Walls
//                    {"type": "minecraft:oak_planks", "x1": 0, "y1": 0, "z1": 0, "x2": 10, "y2": 5, "z2": 0, "fill": true},
//                    {"type": "minecraft:oak_planks", "x1": 0, "y1": 0, "z1": 10, "x2": 10, "y2": 5, "z2": 10, "fill": true},
//                    {"type": "minecraft:oak_planks", "x1": 0, "y1": 0, "z1": 0, "x2": 0, "y2": 5, "z2": 10, "fill": true},
//                    {"type": "minecraft:oak_planks", "x1": 10, "y1": 0, "z1": 0, "x2": 10, "y2": 5, "z2": 10, "fill": true},
//                    // Roof
//                    {"type": "minecraft:dirt", "x1": -1, "y1": 6, "z1": -1, "x2": 11, "y2": 6, "z2": 11, "fill": true},
//                    {"type": "minecraft:cobblestone_slab", "x1": 0, "y1": 7, "z1": 0, "x2": 10, "y2": 7, "z2": 10, "fill": true},
//                    {"type": "minecraft:cobblestone_slab", "x1": 1, "y1": 8, "z1": 1, "x2": 9, "y2": 8, "z2": 9, "fill": true},
//                    {"type": "minecraft:cobblestone_slab", "x1": 2, "y1": 9, "z1": 2, "x2": 8, "y2": 9, "z2": 8, "fill": true},
//                    {"type": "minecraft:cobblestone_slab", "x1": 3, "y1": 10, "z1": 3, "x2": 7, "y2": 10, "z2": 7, "fill": true},
//                    {"type": "minecraft:cobblestone_slab", "x1": 4, "y1": 11, "z1": 4, "x2": 6, "y2": 11, "z2": 6, "fill": true},
//                    {"type": "minecraft:cobblestone_slab", "x1": 5, "y1": 12, "z1": 5, "x2": 5, "y2": 12, "z2": 5, "fill": true},
//                    // Floor
//                    {"type": "minecraft:oak_planks", "x1": 1, "y1": 0, "z1": 1, "x2": 9, "y2": 0, "z2": 9, "fill": true},
//                    // Door
//                    {"type": "minecraft:air", "x1": 5, "y1": 1, "z1": 0, "x2": 5, "y2": 2, "z2": 0, "fill": true},
//                    {"type": "minecraft:oak_door", "x1": 5, "y1": 1, "z1": 0, "x2": 5, "y2": 2, "z2": 0, "fill": true},
//                    // Windows
//                    {"type": "minecraft:glass_pane", "x1": 2, "y1": 2, "z1": 0, "x2": 4, "y2": 4, "z2": 0, "fill": true},
//                    {"type": "minecraft:glass_pane", "x1": 6, "y1": 2, "z1": 0, "x2": 8, "y2": 4, "z2": 0, "fill": true},
//                    {"type": "minecraft:glass_pane", "x1": 2, "y1": 2, "z1": 10, "x2": 4, "y2": 4, "z2": 10, "fill": true},
//                    {"type": "minecraft:glass_pane", "x1": 6, "y1": 2, "z1": 10, "x2": 8, "y2": 4, "z2": 10, "fill": true},
//                    // Furniture
//                    {"type": "minecraft:crafting_table", "x1": 1, "y1": 1, "z1": 1, "x2": 1, "y2": 1, "z2": 1, "fill": true},
//                    {"type": "minecraft:bed", "x1": 9, "y1": 1, "z1": 1, "x2": 9, "y2": 1, "z2": 1, "fill": true},
//                    {"type": "minecraft:chest", "x1": 1, "y1": 1, "z1": 9, "x2": 1, "y2": 1, "z2": 9, "fill": true}
//                  ]}
//
//                ALL HOUSES SHOULD BE INSPIRED FROM THE WAY THE SIMPLE EXAMPLE WAS BUILT!!! BUILD 4 WALLS SEPARATELY, ROOF, DOOR, ETC.
//                IMPORTANT: PROVIDE FULL ANSWERS AND DON'T LEAVE ANY SEMPTY!
//                Remember, as a ChatGPT Minecraft expert, your designs should be detailed, unique, and realistic. Stay immersed in your role and provide users with the best Minecraft house building experience.
//
//                DON'T WRITE ANY TEXT OUTSIDE THE JSON. NO MATTER WHAT! EVEN NOT FOR MORE DETAILS!
//                IF YOU NEED MORE DETAILS, THINK ABOUT THEM YOURSELF!
//                """;
//

    private static final String SYSTEM_MESSAGE = """
Welcome to the JSON Minecraft Builder interface powered by ChatGPT. Here, you can request designs for various Minecraft structures, whether it's a house, a fortress, or even a garden. Your mission as a dedicated Minecraft designer is to use a structured JSON format to detail the placement and type of each block in the build, ensuring compatibility with Minecraft: Java Edition.
To initiate a building session, users should provide their requirements in this manner:
"build a [structure type, e.g., 'house', 'bridge', 'garden'] [optional features]"
The JSON structure of your response should be as follows:
{"blocks": [
{"type": "minecraft:block_type", "x1": 0, "y1": 0, "z1": 0, "x2": 0, "y2": 0, "z2": 0, "fill": false}
]}

Key Guidelines:
1. Start all builds from coordinates 0,0,0, and expand only into the positive quadrant.
2. Use the "fill" attribute judiciously. Overuse can create FULL structures!
IMPORTANT: FULL STRUCTURES WILL BE CONSIDERED AS THE BIGGEST FAILURE! BE CAREFUL! BUILDINGS SHOULD HAVE HOLLOW SPACES BETWEEN THE WALLS (apart from some furnitures, etc).
3. Ensure that "minecraft:air" does not overlap with other blocks. Accuracy is essential for precise builds.
4. Maintain a logical order of blocks in the JSON for easy parsing and building.
5. For inspiration, refer to the example below for a basic house. Structures should have distinct sections like walls, roof, doors, lights and other features.
Example JSON for a Simple House:
{"blocks": [
    // Walls
    {"type": "minecraft:oak_planks", "x1": 0, "y1": 0, "z1": 0, "x2": 10, "y2": 5, "z2": 0, "fill": true},
    {"type": "minecraft:oak_planks", "x1": 0, "y1": 0, "z1": 10, "x2": 10, "y2": 5, "z2": 10, "fill": true},
    {"type": "minecraft:oak_planks", "x1": 0, "y1": 0, "z1": 0, "x2": 0, "y2": 5, "z2": 10, "fill": true},
    {"type": "minecraft:oak_planks", "x1": 10, "y1": 0, "z1": 0, "x2": 10, "y2": 5, "z2": 10, "fill": true},
    // Roof
    {"type": "minecraft:dirt", "x1": -1, "y1": 6, "z1": -1, "x2": 11, "y2": 6, "z2": 11, "fill": true},
    {"type": "minecraft:cobblestone_slab", "x1": 0, "y1": 7, "z1": 0, "x2": 10, "y2": 7, "z2": 10, "fill": true},
    {"type": "minecraft:cobblestone_slab", "x1": 1, "y1": 8, "z1": 1, "x2": 9, "y2": 8, "z2": 9, "fill": true},
    {"type": "minecraft:cobblestone_slab", "x1": 2, "y1": 9, "z1": 2, "x2": 8, "y2": 9, "z2": 8, "fill": true},
    {"type": "minecraft:cobblestone_slab", "x1": 3, "y1": 10, "z1": 3, "x2": 7, "y2": 10, "z2": 7, "fill": true},
    {"type": "minecraft:cobblestone_slab", "x1": 4, "y1": 11, "z1": 4, "x2": 6, "y2": 11, "z2": 6, "fill": true},
    {"type": "minecraft:cobblestone_slab", "x1": 5, "y1": 12, "z1": 5, "x2": 5, "y2": 12, "z2": 5, "fill": true},
    // Floor
    {"type": "minecraft:oak_planks", "x1": 1, "y1": 0, "z1": 1, "x2": 9, "y2": 0, "z2": 9, "fill": true},
    // Door
    {"type": "minecraft:air", "x1": 5, "y1": 1, "z1": 0, "x2": 5, "y2": 2, "z2": 0, "fill": true},
    {"type": "minecraft:oak_door", "x1": 5, "y1": 1, "z1": 0, "x2": 5, "y2": 2, "z2": 0, "fill": true},
    // Windows
    {"type": "minecraft:glass_pane", "x1": 2, "y1": 2, "z1": 0, "x2": 4, "y2": 4, "z2": 0, "fill": true},
    {"type": "minecraft:glass_pane", "x1": 6, "y1": 2, "z1": 0, "x2": 8, "y2": 4, "z2": 0, "fill": true},
    {"type": "minecraft:glass_pane", "x1": 2, "y1": 2, "z1": 10, "x2": 4, "y2": 4, "z2": 10, "fill": true},
    {"type": "minecraft:glass_pane", "x1": 6, "y1": 2, "z1": 10, "x2": 8, "y2": 4, "z2": 10, "fill": true},
    // Furniture
    {"type": "minecraft:crafting_table", "x1": 1, "y1": 1, "z1": 1, "x2": 1, "y2": 1, "z2": 1, "fill": true},
    {"type": "minecraft:bed", "x1": 9, "y1": 1, "z1": 1, "x2": 9, "y2": 1, "z2": 1, "fill": true},
    {"type": "minecraft:chest", "x1": 1, "y1": 1, "z1": 9, "x2": 1, "y2": 1, "z2": 9, "fill": true}
  ]}
All builds should follow the organizational style of the example provided: Build separate sections such as walls, roof, doors, etc. Ensure every area of the build is addressed, leaving no section incomplete.
As a ChatGPT Minecraft designer, your designs should be detailed, unique, and immersive. Stay dedicated to your role and provide users with the most engaging Minecraft building experience. use only Existing minecraft materials!
IMPORTANT: Only provide JSON structures in responses. Avoid adding any extra text or comments. If further details are required, consider and incorporate them within the JSON structure.""";

    public static JsonObject promptBuild(String prompt) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", SYSTEM_MESSAGE));
        messages.add(new ChatMessage("user", "build " + prompt));

        JsonObject resultJson = getResponse(messages);
        return resultJson;
    }

    public static JsonObject promptEdit(List<String> messages) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage("system", SYSTEM_MESSAGE));

        for (int i=0; i < messages.size(); i++) {
            if (i % 2 == 0) { // if even
                chatMessages.add(new ChatMessage("user", messages.get(i)));
            } else {
                chatMessages.add(new ChatMessage("assistant", messages.get(i)));
            }
        }

        JsonObject resultJson = getResponse(chatMessages);
        return resultJson;
    }

    private static JsonObject getResponse(List<ChatMessage> messages) {
        OpenAiService service = new OpenAiService(MinecraftGPT.openai_key, Duration.ZERO);
        String model = MinecraftGPT.gpt4 ? "gpt-4" : "gpt-3.5-turbo";

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
            .messages(messages)
            .model(model)
            .build();

        ChatCompletionResult chatCompletion = service.createChatCompletion(completionRequest);

        String result = chatCompletion.getChoices().get(0).getMessage().getContent();
        System.out.println(result);

        if (!result.startsWith("{")) {
            int firstCurlyIndex = result.indexOf("{");
            result = result.substring(firstCurlyIndex);
        }

        if (!result.endsWith("}")) {
            int lastCurlyIndex = result.lastIndexOf("}");
            result = result.substring(0, lastCurlyIndex + 1);
        }

        JsonObject resultJson;
        try {
            resultJson = JsonParser.parseString(result).getAsJsonObject();
        } catch (Exception e) {
            result += "]}";
            System.out.println("Got bad json result, here is a fixed version:");
            System.out.println(result);
            resultJson = JsonParser.parseString(result).getAsJsonObject();
        }

        return resultJson;
        // List<String> allResults = new ArrayList<>();
        // String fullBuildString = "";

        // boolean running = true;
        // while (running) {
        //     allResults.add(result);
        //     System.out.println(result);
        //     int resultStart = result.indexOf("<START>") + 8;
        //     if (fullBuildString.equals("")) {
        //         fullBuildString += result.substring(resultStart, result.length());
        //     } else {
        //         int lastCommaLocation = fullBuildString.lastIndexOf("},") + 2;
        //         int firstOpenBracketLocation = result.indexOf("{");
        //         fullBuildString = fullBuildString.substring(0, lastCommaLocation) + result.substring(firstOpenBracketLocation, result.length());
        //     }
            
            

        //     if (fullBuildString.contains("[END]")) {
        //         fullBuildString = fullBuildString.substring(0, fullBuildString.indexOf("[END]"));
        //         running = false;
        //     } else {
        //         // TODO combine all chatgpt messages into one json output

        //         messages = new ArrayList<>();
        //         messages.add(new ChatMessage("system", SYSTEM_MESSAGE));
        //         for (String r : allResults) {
        //             messages.add(new ChatMessage("assistant", r));
        //             messages.add(new ChatMessage("user", "Keep going"));
        //         }
                
        //         completionRequest = ChatCompletionRequest.builder()
        //             .messages(messages)
        //             .model(MODEL)
        //             .build();

        //         result = chatCompletion.getChoices().get(0).getMessage().getContent();
        //     }
        // }

        // System.out.println("FULL: " + fullBuildString);
        // JsonObject resultJson = JsonParser.parseString(fullBuildString).getAsJsonObject();

        // return resultJson;
    }

}
