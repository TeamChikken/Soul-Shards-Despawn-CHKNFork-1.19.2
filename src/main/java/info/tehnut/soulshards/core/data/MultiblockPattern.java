package info.tehnut.soulshards.core.data;

import com.google.common.collect.Sets;
import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.InteractionResult;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.awt.Point;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@JsonAdapter(MultiblockPattern.Serializer.class)
public class MultiblockPattern {

    public static final MultiblockPattern DEFAULT = new MultiblockPattern(
            new ItemStack(Items.DIAMOND),
            new String[]{
                    "OQO",
                    "QGQ",
                    "OQO"
            },
            new Point(1, 1),
            new HashMap<Character, Slot>() {{
                put('O', new Slot(Blocks.OBSIDIAN));
                put('Q', new Slot(
                        Blocks.QUARTZ_BLOCK.defaultBlockState(),
                        Blocks.QUARTZ_PILLAR.defaultBlockState(),
                        Blocks.CHISELED_QUARTZ_BLOCK.defaultBlockState(),
                        Blocks.SMOOTH_QUARTZ.defaultBlockState(),
                        Blocks.QUARTZ_SLAB.defaultBlockState().setValue(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE),
                        Blocks.SMOOTH_QUARTZ_SLAB.defaultBlockState().setValue(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE)
                ));
                put('G', new Slot(Blocks.GLOWSTONE));
            }}
    );

    private final ItemStack catalyst;
    private final String[] shape;
    private final Point origin;
    private final Map<Character, Slot> definition;

    public MultiblockPattern(ItemStack catalyst, String[] shape, Point origin, Map<Character, Slot> definition) {
        this.catalyst = catalyst;
        this.shape = shape;
        this.origin = origin;
        this.definition = definition;
        this.definition.put(' ', new Slot(Blocks.AIR.defaultBlockState()));

        char originChar = shape[origin.y].charAt(origin.x);
        if (originChar == ' ' || definition.get(originChar).test(Blocks.AIR.defaultBlockState()))
            throw new IllegalStateException("Origin point cannot be blank space.");

        int lineLength = shape[0].length();
        for (String line : shape) {
            if (line.length() != lineLength)
                throw new IllegalStateException("All lines in the shape must be the same size.");

            for (char letter : line.toCharArray())
                if (definition.get(letter) == null)
                    throw new IllegalStateException(letter + " is not defined.");
        }
    }

    public ItemStack getCatalyst() {
        return catalyst;
    }

    public InteractionResultHolder<Set<BlockPos>> match(Level level, BlockPos originBlock) {
        Set<BlockPos> matched = Sets.newHashSet();
        for (int y = 0; y < shape.length; y++) {
            String line = shape[y];
            for (int x = 0; x < line.length(); x++) {
                BlockPos offset = originBlock.offset(x - origin.x, 0, y - origin.y);
                BlockState state = level.getBlockState(offset);
                if (!definition.get(line.charAt(x)).test(state))
                    return new InteractionResultHolder<>(InteractionResult.FAIL, Collections.emptySet());

                matched.add(offset);
            }
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, matched);
    }

    public boolean isOriginBlock(BlockState state) {
        Slot slot = definition.get(shape[origin.y].charAt(origin.x));
        return slot.test(state);
    }

    public static class Slot implements Predicate<BlockState> {

        @JsonAdapter(SerializerBlockState.class)
        private final Set<BlockState> states;

        public Slot(BlockState... states) {
            this.states = Sets.newHashSet(states);
        }

        public Slot(Block block) {
            this(block.getStateDefinition().getPossibleStates().toArray(new BlockState[0]));
        }

        @Override
        public boolean test(BlockState state) {
            return states.contains(state);
        }
    }

    public static class Serializer implements JsonDeserializer<MultiblockPattern> {
        @Override
        public MultiblockPattern deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = element.getAsJsonObject();

            var itemId =
                    new ResourceLocation(json.getAsJsonObject("catalyst").getAsJsonPrimitive("item").getAsString());
            ItemStack catalyst = new ItemStack(Registry.ITEM.get(itemId), 1);

            String[] shape = context.deserialize(json.getAsJsonArray("shape"), String[].class);
            Point origin = context.deserialize(json.getAsJsonObject("origin"), Point.class);
            Map<Character, Slot> definition = context.deserialize(json.getAsJsonObject("definition"), new TypeToken<Map<Character, Slot>>() {
            }.getType());

            return new MultiblockPattern(catalyst, shape, origin, definition);
        }
    }

    public static class SerializerBlockState implements JsonDeserializer<Set<BlockState>> {
        @Override
        public Set<BlockState> deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Set<BlockState> states = Sets.newHashSet();
            for (JsonElement entry : element.getAsJsonArray()) {
                if (entry.isJsonObject()) {
                    JsonObject json = entry.getAsJsonObject();
                    Block block = Registry.BLOCK.get(new ResourceLocation(json.getAsJsonPrimitive("block").getAsString()));
                    BlockState state = block.defaultBlockState();
                    if (json.has("states")) {
                        JsonObject stateObject = json.getAsJsonObject("states");
                        for (Map.Entry<String, JsonElement> e : stateObject.entrySet()) {
                            var property = block.getStateDefinition().getProperty(e.getKey());
                            if (property != null) {
                                String valueString = e.getValue().getAsString();
                                var value = (Comparable)property.getValue(valueString).get();
                                state = state.setValue(property, value);
                            }
                        }
                        states.add(state);
                    } else {
                        states.addAll(block.getStateDefinition().getPossibleStates());
                    }
                }
            }

            return states;
        }
    }
}
