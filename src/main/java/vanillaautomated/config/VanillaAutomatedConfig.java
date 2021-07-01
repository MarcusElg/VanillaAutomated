package vanillaautomated.config;


import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;


@Config(name = "vanillaautomated")
public class VanillaAutomatedConfig implements ConfigData, ModMenuApi {

    @ConfigEntry.Category("Breaker")
    public boolean enableBreaker = true;
    @ConfigEntry.Category("Breaker")
    public int breakerTime = 10;

    @ConfigEntry.Category("Cobblegenerator")
    public boolean enableCobblegenerator = true;
    @ConfigEntry.Category("Cobblegenerator")
    public int cobblestoneGeneratorTime = 200;

    @ConfigEntry.Category("Crafter")
    public boolean enableCrafter = true;
    @ConfigEntry.Category("Crafter")
    public int crafterTime = 10;

    @ConfigEntry.Category("Crusher")
    public boolean enableCrusher = true;
    @ConfigEntry.Category("Crusher")
    public int crusherTime = 200;

    @ConfigEntry.Category("Farmer")
    public boolean enableFarmer = true;
    @ConfigEntry.Category("Farmer")
    public int farmerTime = 2400;
    @ConfigEntry.Category("Farmer")
    public int bonemealedFarmerTime = 20;

    @ConfigEntry.Category("Fisher")
    public boolean enableFisher = true;
    @ConfigEntry.Category("Fisher")
    public int fisherTime = 200;

    @ConfigEntry.Category("MobFarm")
    public boolean enableMobFarm = true;
    @ConfigEntry.Category("MobFarm")
    public int mobFarmTime = 400;

    @ConfigEntry.Category("Placer")
    public boolean enablePlacer = true;
    @ConfigEntry.Category("Placer")
    public int placerTime = 10;

    @ConfigEntry.Category("Magnet")
    public boolean enableMagnet = true;
    @ConfigEntry.Category("Magnet")
    public int magnetRange = 5;
    @ConfigEntry.Category("Magnet")
    public int magnetTime = 8;

    @ConfigEntry.Category("Nullifier")
    public boolean enableNullifier = true;

    @ConfigEntry.Category("Timer")
    public boolean enableTimer = true;


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
