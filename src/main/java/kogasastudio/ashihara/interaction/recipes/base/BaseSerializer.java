package kogasastudio.ashihara.interaction.recipes.base;

import com.google.gson.JsonObject;
import kogasastudio.ashihara.utils.json.JsonUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

/**
 * @author DustW
 **/
public class BaseSerializer<RECIPE extends BaseRecipe>
        extends ForgeRegistryEntry<RecipeSerializer<?>>
        implements RecipeSerializer<RECIPE> {

    Class<RECIPE> recipeClass;

    public BaseSerializer(Class<RECIPE> recipeClass) {
        this.recipeClass = recipeClass;
    }

    @Nullable
    @Override
    public RECIPE fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        return JsonUtils.INSTANCE.normal.fromJson(pBuffer.readUtf(), recipeClass);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, RECIPE pRecipe) {
        pBuffer.writeUtf(JsonUtils.INSTANCE.normal.toJson(pRecipe));
    }

    @Override
    public RECIPE fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
        var recipe = JsonUtils.INSTANCE.normal.fromJson(pSerializedRecipe, recipeClass);
        recipe.setID(pRecipeId);
        return recipe;
    }

    public JsonObject toJson(RECIPE pRecipe) {
        return JsonUtils.INSTANCE.normal.toJsonTree(pRecipe).getAsJsonObject();
    }
}
