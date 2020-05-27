package vanillaautomated.compat;

import me.shedaniel.rei.api.RecipeCategory;
import net.minecraft.util.Identifier;
import vanillaautomated.VanillaAutomated;

public class FarmingCategory implements RecipeCategory {
    @Override
    public Identifier getIdentifier() {
        return new Identifier(VanillaAutomated.prefix, "farming");
    }

    @Override
    public String getCategoryName() {
        return "farming";
    }
}
