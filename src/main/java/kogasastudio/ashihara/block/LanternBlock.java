package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class LanternBlock extends Block
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    protected final double flameX;
    protected final double flameY;
    protected final double flameZ;

    public LanternBlock(Properties properties, double XIn, double YIn, double ZIn)
    {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false));

        this.flameX = XIn;
        this.flameY = YIn;
        this.flameZ = ZIn;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand)
    {
        if (stateIn.getValue(LIT))
        {
            double x = (double) pos.getX() + this.flameX;
            double y = (double) pos.getY() + this.flameY;
            double z = (double) pos.getZ() + this.flameZ;
            worldIn.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult)
    {
        RandomSource random = pLevel.getRandom();
        if (pState.getValue(LIT))
        {
            pLevel.playSound(pPlayer, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            pLevel.setBlockAndUpdate(pPos, pState.setValue(LIT, false));
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack pStack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        RandomSource random = worldIn.getRandom();
        if (player.getItemInHand(handIn).getItem().equals(Items.FLINT_AND_STEEL) && !state.getValue(LIT))
        {
            worldIn.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            worldIn.setBlockAndUpdate(pos, state.setValue(LIT, true));
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(pStack, state, worldIn, pos, player, handIn, hit);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LIT);
    }

    public static class HangingLanternBlock extends LanternBlock
    {
        public HangingLanternBlock(Properties properties, double XIn, double YIn, double ZIn)
        {
            super(properties, XIn, YIn, ZIn);
        }

        @Override
        public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
        {
            return worldIn.getBlockState(pos.above()).getBlock() != Blocks.AIR;
        }

        public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
        {
            return !this.canSurvive(stateIn, worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }
}
