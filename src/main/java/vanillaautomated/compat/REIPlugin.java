package vanillaautomated.compat;

import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.plugin.cooking.DefaultCookingCategory;
import net.minecraft.util.Identifier;
import vanillaautomated.VanillaAutomated;

public class REIPlugin implements REIPluginV0 {

    public static final Identifier FARMING = new Identifier(VanillaAutomated.prefix, "plugins/farming");
    public static final Identifier CRUSHING = new Identifier(VanillaAutomated.prefix, "plugins/crushing");

    public static DefaultCookingCategory FARMING_CATEGORY;

    public REIPlugin () {

    }

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier(VanillaAutomated.prefix, "vanillaautomated_plugin");
    }

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        //FARMING_CATEGORY = new DefaultCookingCategory(FARMING, EntryStack.create(VanillaAutomatedBlocks.farmerBlock), "category.rei.farming");
        //recipeHelper.registerCategory(FARMING_CATEGORY);
        //recipeHelper.registerCategory(new DefaultCookingCategory(CRUSHING, EntryStack.create(VanillaAutomatedBlocks.crusherBlock), "category.rei.crushing"));
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        //recipeHelper.registerRecipes(FARMING, FarmerRecipe.class, FarmingDisplay::new);
        //recipeHelper.registerRecipes(CRUSHING, CrusherRecipe.class, DefaultSmeltingDisplay::new);
    }
}
