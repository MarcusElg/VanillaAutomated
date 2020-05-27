package vanillaautomated.compat;

import me.shedaniel.rei.plugin.cooking.DefaultCookingDisplay;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.util.Identifier;

public class FarmingDisplay extends DefaultCookingDisplay {

    public FarmingDisplay(AbstractCookingRecipe recipe) {
        super(recipe);
    }

    @Override
    public Identifier getRecipeCategory() {
        return REIPlugin.FARMING_CATEGORY.getIdentifier();
    }
}
