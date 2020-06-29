package vanillaautomated.recipes;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import vanillaautomated.VanillaAutomated;

public class FarmerRecipe extends AbstractCookingRecipe {

    public FarmerRecipe (Ingredient ingredient, ItemStack output, Identifier id) {
        super(VanillaAutomated.farmerRecipeType, id, null, ingredient, output, 0, 20);
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        return input.test(inv.getStack(0));
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
        return input;
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
