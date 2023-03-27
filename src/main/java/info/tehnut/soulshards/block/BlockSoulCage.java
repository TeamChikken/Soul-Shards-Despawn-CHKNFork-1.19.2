package info.tehnut.soulshards.block;

import info.tehnut.soulshards.core.data.Binding;
import info.tehnut.soulshards.core.data.Tier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.BlockView;
import java.util.Random;

public class BlockSoulCage extends Block{

    public static final Property<Boolean> ACTIVE = BooleanProperty.create("active");
    public static final Property<Boolean> POWERED = BooleanProperty.create("powered");

    public BlockSoulCage() {
        super(Properties.copy(Blocks.SPAWNER));

        registerDefaultState(defaultBlockState().setValue(ACTIVE, false).setValue(POWERED, false));
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                          BlockHitResult result) {
        if (!player.isCrouching())
            return InteractionResult.PASS;

        TileEntitySoulCage cage = (TileEntitySoulCage) level.getBlockEntity(pos);
        if (cage == null)
            return InteractionResult.PASS;

        ItemStack stack = cage.getInventory().getItem(0);
        if (stack.isEmpty())
            return InteractionResult.PASS;

        if (!player.getInventory().add(stack)) {
            BlockPos playerPos = new BlockPos(player.position());
            ItemEntity entity = new ItemEntity(level, playerPos.getX(), playerPos.getY(), playerPos.getZ(), stack);
            level.addFreshEntity(entity);
        }
        return InteractionResult.SUCCESS;
    }


    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2,
                               boolean boolean_1) {
        if (this.hasBlockEntity() && blockState.getBlock() != blockState2.getBlock()) {
            TileEntitySoulCage cage = (TileEntitySoulCage) level.getBlockEntity(blockPos);
            if (cage != null)
                cage.getInventory().dropAll();
        }

        super.onRemove(blockState, level, blockPos, blockState2, boolean_1);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        TileEntitySoulCage cage = (TileEntitySoulCage) world.getBlockEntity(pos);
        if (cage == null)
            return 0;

        Binding binding = cage.getBinding();
        if (binding == null)
            return 0;

        return (int) (((double) binding.getTier().getIndex() / ((double) Tier.INDEXED.size() - 1)) * 15D);
    }

    @Override
    public void onPlace(BlockState state1, Level world, BlockPos pos, BlockState state2, boolean someBool) {
        handleRedstoneChange(world, state1, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, Level world, BlockPos pos, Block block, BlockPos neighborPos,
                               boolean someBool) {
        handleRedstoneChange(world, state, pos);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rand) {
        if (state.getValue(POWERED) && !world.hasNeighborSignal(pos))
            world.getChunkAt(pos).setBlockState(pos, state.setValue(POWERED, false), false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> factory) {
        factory.add(ACTIVE, POWERED);
    }

    @Override
    public boolean hasBlockEntity() {
        return true;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new TileEntitySoulCage();
    }

    private void handleRedstoneChange(Level world, BlockState state, BlockPos pos) {
        boolean powered = world.hasNeighborSignal(pos);
        if (state.getValue(POWERED) && !powered)
            world.setBlock(pos, state.setValue(POWERED, false), 2);
        else if (!state.getValue(POWERED) && powered)
            world.setBlock(pos, state.setValue(POWERED, true), 2);
    }
}
