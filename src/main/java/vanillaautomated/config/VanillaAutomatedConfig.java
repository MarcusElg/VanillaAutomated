package vanillaautomated.config;


import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;


@Config(name = "vanillaautomated")
public class VanillaAutomatedConfig implements ConfigData, ModMenuApi {

    public int breakerTime = 10;
    public int cobblestoneGeneratorTime = 200;
    public int crafterTime = 10;
    public int crusherTime = 200;
    public int farmerTime = 2400;
    public int bonemealedFarmerTime = 20;
    public int fisherTime = 200;
    public int mobFarmTime = 400;
    public int placerTime = 10;

    @Override
    public void validatePostLoad() {
        breakerTime = Math.max(5, breakerTime);
        cobblestoneGeneratorTime = Math.max(5, cobblestoneGeneratorTime);
        crafterTime = Math.max(5, crafterTime);
        crusherTime = Math.max(5, crusherTime);
        farmerTime = Math.max(5, farmerTime);
        bonemealedFarmerTime = Math.max(5, bonemealedFarmerTime);
        fisherTime = Math.max(5, fisherTime);
        mobFarmTime = Math.max(5, mobFarmTime);
        placerTime = Math.max(5, placerTime);
    }

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent) -> AutoConfig.getConfigScreen(VanillaAutomatedConfig.class, parent).get();
    }
}
