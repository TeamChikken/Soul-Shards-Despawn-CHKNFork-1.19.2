package info.x2a.soulshards.block;

import info.x2a.soulshards.core.data.Binding;
import info.x2a.soulshards.core.data.Tier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockSoulCage extends Block implements EntityBlock {

    public static final Property<Boolean> ACTIVE = BooleanProperty.create("active");
    public static final Property<Boolean> POWERED = BooleanProperty.create("powered");

    public BlockSoulCage() {
        super(Properties.copy(Blocks.SPAWNER));

        registerDefaultState(getStateDefinition().any().setValue(ACTIVE, false).setValue(POWERED, false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> factory) {
        factory.add(ACTIVE, POWERED);
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                          BlockHitResult result) {
        if (!player.isCrouching())
            return InteractionResult.PASS;

        var cage = (TileEntitySoulCage)level.getBlockEntity(pos);
        if (cage == null) {
            return InteractionResult.PASS;
        }
        var shard = cage.getInventory().getItem(0);
        if (shard.isEmpty())
            return InteractionResult.PASS;

        if (!player.getInventory().add(shard)) {
            BlockPos playerPos = new BlockPos(player.position());
            ItemEntity entity = new ItemEntity(level, playerPos.getX(), playerPos.getY(), playerPos.getZ(), shard);
            level.addFreshEntity(entity);
        }
        return InteractionResult.SUCCESS;
    }


    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2,
                               boolean boolean_1) {
        if (blockState.getBlock() != blockState2.getBlock()) {
            TileEntitySoulCage cage = (TileEntitySoulCage) level.getBlockEntity(blockPos);
            if (cage != null)
                Containers.dropContents(level, blockPos, cage.getInventory());
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
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos neighborPos,
                               boolean someBool) {
        handleRedstoneChange(world, state, pos);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        if (state.getValue(POWERED) && !world.hasNeighborSignal(pos))
            world.getChunkAt(pos).setBlockState(pos, state.setValue(POWERED, false), false);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                            BlockEntityType<T> blockEntityType) {
        return TileEntitySoulCage::ticker;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntitySoulCage(pos, state);
    }


    private void handleRedstoneChange(Level world, BlockState state, BlockPos pos) {
        boolean powered = world.hasNeighborSignal(pos);
        if (state.getValue(POWERED) && !powered)
            world.setBlock(pos, state.setValue(POWERED, false), 2);
        else if (!state.getValue(POWERED) && powered)
            world.setBlock(pos, state.setValue(POWERED, true), 2);
    }
}
