package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.MarkableLanternTE;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static kogasastudio.ashihara.helper.BlockActionHelper.getLightValueLit;

public class MarkableHangingLanternBlock extends LanternBlock.HangingLanternBlock implements EntityBlock
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public MarkableHangingLanternBlock()
    {
        super
                (
                        Properties.of()
                                .mapColor(MapColor.WOOL)
                                .strength(1.0F)
                                .sound(SoundType.BAMBOO_SAPLING)
                                .lightLevel(getLightValueLit(15)),
                        0.5d, 0.5d, 0.5d
                );
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape shape1 = Block.box(5, 0.5, 5, 11, 1.5, 11);
        VoxelShape shape2 = Block.box(4, 1.5, 4, 12, 14.5, 12);
        VoxelShape shape3 = Block.box(5, 14.5, 5, 11, 15.5, 11);
        return Shapes.or(shape1, shape2, shape3);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if (player.getItemInHand(handIn).getItem() == ItemRegistryHandler.KOISHI.get())
        {
            MarkableLanternTE te = (MarkableLanternTE) worldIn.getBlockEntity(pos);
            if (te != null)
            {
                te.nextIcon();
                te.setChanged();
                return ItemInteractionResult.SUCCESS;
            } else return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
    {
        return new MarkableLanternTE(pPos, pState);
    }
}
