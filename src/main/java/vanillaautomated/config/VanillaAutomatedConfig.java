package vanillaautomated.config;


import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

@Config(name="vanillaautomated")
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
    public void validatePostLoad() throws ConfigData.ValidationException {
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
        return this::getConfigScreen;
    }

    public Screen getConfigScreen(Screen parent){
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("title.examplemod.config"));
        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.examplemod.general"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        general.addEntry(entryBuilder.startIntField(new TranslatableText("option.vanillaautomated.breakerTimer"), breakerTime)
                .setDefaultValue(10) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("This option is awesome!")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> breakerTime = Math.max(5, newValue)) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        general.addEntry(entryBuilder.startIntField(new TranslatableText("option.vanillaautomated.cobblestoneGeneratorTime"), cobblestoneGeneratorTime)
                .setDefaultValue(200) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("This option is awesome!")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> cobblestoneGeneratorTime = Math.max(5, newValue)) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        general.addEntry(entryBuilder.startIntField(new TranslatableText("option.vanillaautomated.crafterTime"), crafterTime)
                .setDefaultValue(10) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("This option is awesome!")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> crafterTime = Math.max(5, newValue)) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        general.addEntry(entryBuilder.startIntField(new TranslatableText("option.vanillaautomated.crusherTime"), crusherTime)
                .setDefaultValue(200) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("This option is awesome!")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> crusherTime = Math.max(5, newValue)) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        general.addEntry(entryBuilder.startIntField(new TranslatableText("option.vanillaautomated.farmerTime"), farmerTime)
                .setDefaultValue(2400) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("This option is awesome!")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> farmerTime = Math.max(5, newValue)) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        general.addEntry(entryBuilder.startIntField(new TranslatableText("option.vanillaautomated.bonemealedFarmerTime"), bonemealedFarmerTime)
                .setDefaultValue(20) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("This option is awesome!")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> bonemealedFarmerTime = Math.max(5, newValue)) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        general.addEntry(entryBuilder.startIntField(new TranslatableText("option.vanillaautomated.fisherTime"), fisherTime)
                .setDefaultValue(200) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("This option is awesome!")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> fisherTime = Math.max(5, newValue)) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        general.addEntry(entryBuilder.startIntField(new TranslatableText("option.vanillaautomated.mobFarmTime"), mobFarmTime)
                .setDefaultValue(400) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("This option is awesome!")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> mobFarmTime = Math.max(5, newValue)) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        general.addEntry(entryBuilder.startIntField(new TranslatableText("option.vanillaautomated.placerTime"), placerTime)
                .setDefaultValue(10) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("This option is awesome!")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> placerTime = Math.max(5, newValue)) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        return builder.build();
    }
}
