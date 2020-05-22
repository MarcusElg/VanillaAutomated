package vanillaautomated;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class VanillaAutomated implements ModInitializer {

	public static String prefix = "vanillaautomated";
	public static Identifier flames = new Identifier(prefix, "textures/gui/flames.png");
	public static Identifier flames_background = new Identifier(prefix, "textures/gui/flames_background.png");
	public static Identifier progress = new Identifier(prefix, "textures/gui/progress.png");
	public static Identifier progress_background = new Identifier(prefix, "textures/gui/progress_background.png");

	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
			new Identifier(prefix, "machines"),
			() -> new ItemStack(VanillaAutomatedBlocks.machineBlock));

	@Override
	public void onInitialize() {
		VanillaAutomatedBlocks.register();
		VanillaAutomatedItems.register();
	}
}
