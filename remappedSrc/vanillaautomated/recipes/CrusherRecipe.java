package vanillaautomated.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class CrusherRecipe extends FarmerRecipe {

    public CrusherRecipe(Ingredient ingredient, ItemStack output, Identifier id) {
        super(ingredient, output, id);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CrusherRecipeSerializer.INSTANCE;
    }

    public static class Type implements RecipeType<CrusherRecipe> {
        private Type() {}
        public static final vanillaautomated.recipes.CrusherRecipe.Type INSTANCE = new vanillaautomated.recipes.CrusherRecipe.Type();

        public static final String ID = "crusher_recipe";
    }

    @Override
    public RecipeType<?> getType() {
        return vanillaautomated.recipes.CrusherRecipe.Type.INSTANCE;
    }
}
