package vanillaautomated.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import vanillaautomated.VanillaAutomated;

public class CrusherRecipeSerializer implements RecipeSerializer<CrusherRecipe> {

    private CrusherRecipeSerializer() {}

    public static final CrusherRecipeSerializer INSTANCE = new CrusherRecipeSerializer();
    public static final Identifier ID = new Identifier(VanillaAutomated.prefix, "crusher_recipe");

    @Override
    public CrusherRecipe read(Identifier id, JsonObject json) {
        FarmerRecipeJsonFormat recipeJson = VanillaAutomated.gson.fromJson(json, FarmerRecipeJsonFormat.class);

        // Validate all fields are there
        if (recipeJson.ingredient == null || recipeJson.result == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }
        // We'll allow to not specify the output, and default it to 1.
        if (ShapedRecipe.getItemStack(recipeJson.result).getCount() == 0) ShapedRecipe.getItemStack(recipeJson.result).setCount(1);

        Ingredient input = Ingredient.fromJson(recipeJson.ingredient);
        ItemStack output = ShapedRecipe.getItemStack(JsonHelper.getObject(json, "result"));

        return new CrusherRecipe(input, output, id);
    }

    @Override
    public CrusherRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient input = Ingredient.fromPacket(buf);
        ItemStack output = buf.readItemStack();
        return new CrusherRecipe(input, output, id);
    }

    @Override
    public void write(PacketByteBuf buf, CrusherRecipe recipe) {
        recipe.getIngredient().write(buf);
        buf.writeItemStack(recipe.getOutput());
    }
}
