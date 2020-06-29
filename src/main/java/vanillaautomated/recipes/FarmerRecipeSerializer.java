package vanillaautomated.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import vanillaautomated.VanillaAutomated;


public class FarmerRecipeSerializer implements RecipeSerializer<FarmerRecipe> {

    private FarmerRecipeSerializer () {}

    public static final FarmerRecipeSerializer INSTANCE = new FarmerRecipeSerializer();
    public static final Identifier ID = new Identifier(VanillaAutomated.prefix, "farmer_recipe");

    @Override
    public FarmerRecipe read(Identifier id, JsonObject json) {
        FarmerRecipeJsonFormat recipeJson = VanillaAutomated.gson.fromJson(json, FarmerRecipeJsonFormat.class);

        // Validate all fields are there
        if (recipeJson.ingredient == null || recipeJson.result == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }

        Ingredient input = Ingredient.fromJson(recipeJson.ingredient);
        ItemStack output = ShapedRecipe.getItemStack(JsonHelper.getObject(json, "result"));

        return new FarmerRecipe(input, output, id);
    }

    @Override
    public FarmerRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient input = Ingredient.fromPacket(buf);
        ItemStack output = buf.readItemStack();
        return new FarmerRecipe(input, output, id);
    }

    @Override
    public void write(PacketByteBuf buf, FarmerRecipe recipe) {
        recipe.getIngredient().write(buf);
        buf.writeItemStack(recipe.getOutput());
    }
}
