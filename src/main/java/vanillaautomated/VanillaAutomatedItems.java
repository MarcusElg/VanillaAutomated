package vanillaautomated;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import vanillaautomated.items.MobNetItem;

public class VanillaAutomatedItems {

    private static final MobNetItem mobNet = new MobNetItem(new Item.Settings().group(VanillaAutomated.ITEM_GROUP));

    public static void register () {
        // Items
        registerItem(mobNet, "mob_net");
    }

    private static void registerItem(Item item, String name) {
        Registry.register(Registry.ITEM, new Identifier(VanillaAutomated.prefix, name), item);
    }

}