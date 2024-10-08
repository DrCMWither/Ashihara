package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import static kogasastudio.ashihara.block.PaddyFieldBlock.LEVEL;

public class RiceCropBlock extends CropBlock
{
    public RiceCropBlock()
    {
        super
                (
                        Properties.of()
                                .mapColor(MapColor.PLANT)
                                .noCollission()
                                .randomTicks()
                                .instabreak()
                                .sound(SoundType.CROP)
                );
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (worldIn.getRawBrightness(pos, 0) >= 9)
        {
            int i = this.getAge(state);
            if (i < this.getMaxAge() && this.isValidBonemealTarget(worldIn, pos, state))
            {
                float f = getGrowthSpeed(state, worldIn, pos);
                if (net.neoforged.neoforge.common.CommonHooks.canCropGrow(worldIn, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0))
                {
                    worldIn.setBlock(pos, this.getStateForAge(i + 1), 2);
                    net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.below()).is(BlockRegistryHandler.WATER_FIELD.get());
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos)
    {
        return state.is(BlockRegistryHandler.WATER_FIELD.get());
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader worldIn, BlockPos pos, BlockState state)
    {
        boolean flag = false;
        if (worldIn.getBlockState(pos.below()).is(BlockRegistryHandler.WATER_FIELD.get()))
        {
            flag = !this.isMaxAge(state) && worldIn.getBlockState(pos.below()).getValue(LEVEL) > 5;
        }
        return flag;
    }

    @Override
    protected ItemLike getBaseSeedId()
    {
        return ItemRegistryHandler.RICE_SEEDLING.get();
    }

    @Override
    public boolean isBonemealSuccess(Level worldIn, RandomSource rand, BlockPos pos, BlockState state)
    {
        return this.isValidBonemealTarget(worldIn, pos, state);
    }
}
