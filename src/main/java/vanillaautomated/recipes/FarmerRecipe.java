package vanillaautomated.recipes;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FarmerRecipe implements Recipe<Inventory> {
    private final Ingredient ingredient;
    private final ItemStack output;
    private final Identifier id;

    public FarmerRecipe (Ingredient ingredient, ItemStack output, Identifier id) {
        this.ingredient = ingredient;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        return ingredient.test(inv.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public Ingredient getIngredient () {
        return ingredient;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FarmerRecipeSerializer.INSTANCE;
    }

    public static class Type implements RecipeType<FarmerRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();

        public static final String ID = "farmer_recipe";
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }
}
