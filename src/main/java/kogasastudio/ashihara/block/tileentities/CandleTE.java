package kogasastudio.ashihara.block.tileentities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.client.models.CandleModel;
import kogasastudio.ashihara.client.models.baked.BakedModels;
import kogasastudio.ashihara.client.models.baked.UnbakedModelPart;
import kogasastudio.ashihara.client.render.AshiharaRenderTypes;
import kogasastudio.ashihara.client.render.LayerRegistryHandler;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

import static kogasastudio.ashihara.helper.MathHelper.simplifyDouble;

public class CandleTE extends AshiharaMachineTE
{
    private final NonNullList<double[]> posList = NonNullList.create();
    private final BlockState state = BlockRegistryHandler.CANDLE.get().defaultBlockState();

    public CandleTE(BlockPos pos, BlockState state)
    {
        super(TERegistryHandler.CANDLE_TE.get(), pos, state);
    }

    public boolean addCurrentCandle(double x, double y, double z)
    {
        if (this.level == null || posList.size() >= 4) return false;
        else if (this.level.isClientSide())
        {
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
            return false;
        }

        double[] serialized = new double[]
                {
                        simplifyDouble(x, 2),
                        simplifyDouble(z, 2),
                        simplifyDouble(y, 2)
                };
        posList.add(serialized);
        this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        setChanged();
        return true;
    }

    public boolean addCurrentCandle(double x, double z, RandomSource rand)
    {
        return this.addCurrentCandle(x, 0 - (0.4d * rand.nextDouble()), z);
    }

    public int pickCandle(boolean pickAll, Level worldIn, BlockPos posIn)
    {
        if (pickAll || posList.size() == 1)
        {
            int i = posList.size();
            posList.clear();
            setChanged();
            worldIn.removeBlock(posIn, false);
            worldIn.sendBlockUpdated(posIn, state, state, 3);
            return worldIn.isClientSide() ? 0 : i;
        } else
        {
            int pointer = posList.size() - 1;
            posList.remove(pointer);
            worldIn.sendBlockUpdated(posIn, state, state, 3);
            setChanged();
            return worldIn.isClientSide() ? 0 : 1;
        }
    }

    public void init()
    {
        if (this.level == null) return;
        this.posList.clear();
        this.addCurrentCandle(0.5d, 0.0d, 0.5d);
    }

    public void init(double x, double z)
    {
        if (this.level == null) return;
        this.posList.clear();
        this.addCurrentCandle(x, z, this.level.getRandom());
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries)
    {
        super.loadAdditional(pTag, pRegistries);
        ListTag poses = pTag.getList("posList", Tag.TAG_COMPOUND);
        for (int i = 0; i < poses.size(); i += 1)
        {
            CompoundTag array = poses.getCompound(i);
            double x = array.getDouble("x");
            double y = array.getDouble("y");
            double z = array.getDouble("z");
            this.posList.add(new double[]{x, z, y});
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries)
    {
        super.saveAdditional(compound, pRegistries);
        ListTag nbt = new ListTag();
        for (double[] d : this.posList)
        {
            if (d.length == 3)
            {
                CompoundTag arrays = new CompoundTag();
                arrays.putDouble("x", d[0]);
                arrays.putDouble("z", d[1]);
                arrays.putDouble("y", d[2]);
                nbt.add(arrays);
            }
        }
        compound.put("posList", nbt);
    }

    public NonNullList<double[]> getPosList()
    {
        return this.posList;
    }
}
