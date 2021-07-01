package vanillaautomated.blockentities;

import blue.endless.jankson.annotation.Nullable;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.gui.CrafterBlockController;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

public class CrafterBlockEntity extends MachineBlockEntity implements SidedInventory, PropertyDelegateHolder, ExtendedScreenHandlerFactory {

    private final PropertyDelegate propertyDelegate;
    public int speed = 10;
    DefaultedList<ItemStack> items = DefaultedList.ofSize(11, ItemStack.EMPTY);
    DefaultedList<Item> recipeItems = DefaultedList.ofSize(9, Items.AIR);
    boolean firstTick = true;
    private int processingTime;
    private int fuelTime;
    private int maxFuelTime;
    private String recipeString = "null";
    private CraftingRecipe currentRecipe = null;

    public CrafterBlockEntity(BlockPos pos, BlockState state) {
        super(VanillaAutomatedBlocks.crafterBlockEntity, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                switch (index) {
                    case 0:
                        return fuelTime;
                    case 1:
                        return processingTime;
                    case 2:
                        return maxFuelTime;
                    case 3:
                        return speed;
                    default:
                        return 0;
                }
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        fuelTime = value;
                        break;
                    case 1:
                        processingTime = value;
                        break;
                    case 2:
                        maxFuelTime = value;
                        break;
                    case 3:
                        speed = value;
                        break;
                    default:
                        break;
                }

            }

            public int size() {
                return 4;
            }
        };
    }

    public static boolean canUseAsFuel(ItemStack stack) {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().containsKey(stack.getItem());
    }

    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public DefaultedList<Item> getRecipeItems() {
        return recipeItems;
    }

    /*
     * TODO
     *  this somehow duplicates items. When you click on X the previous recipe will be processed once again
     */
    public void resetRecipe() {
        recipeItems = DefaultedList.ofSize(9, Items.AIR);
        for (int i = 0; i < 9; ++i) {
            ItemScatterer.spawn(world, getPos().getX(), getPos().getY(), getPos().getZ(), getStack(i + 1));
            items.set(i + 1, ItemStack.EMPTY);
        }
    }

    public void resetRecipeClient() {
        Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        if (currentScreen == null || !(currentScreen instanceof ScreenHandlerProvider)) {

        } else {
            for (int i = 0; i < 9; i++) {
                ((CrafterBlockController) ((ScreenHandlerProvider) currentScreen).getScreenHandler()).itemSprites.get(i).setItem(ItemStack.EMPTY);
            }
        }
    }

    @Override
    public int size() {
        return 11;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.items, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.items, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        // Set recipe
        if (slot > 0 && slot < 10 && stack.getItem() != Items.AIR) {
            Item oldRecipeItem = recipeItems.get(slot - 1);
            recipeItems.set(slot - 1, stack.getItem());

            // Only change visuals if it's the player who set the item
            if (oldRecipeItem != recipeItems.get(slot - 1)) {
                List<? extends PlayerEntity> players = world.getPlayers();
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).currentScreenHandler != null && players.get(i).currentScreenHandler instanceof CrafterBlockController) {
                        // Server side
                        CrafterBlockController controller = (CrafterBlockController) players.get(i).currentScreenHandler;
                        controller.itemSprites.get(slot - 1).setItem(new ItemStack(stack.getItem()));

                        // Client side
                        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
                        data.writeItemStack(stack);
                        data.writeInt(slot - 1);
                        ServerSidePacketRegistry.INSTANCE.sendToPlayer(players.get(i), VanillaAutomated.update_crafter_gui_packet, data);
                    }
                }
            }
        }

        // Insert items
        this.items.set(slot, new ItemStack(stack.getItem(), stack.getCount()));
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

        updateCurrentRecipe();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 0) {
            ItemStack itemStack = this.items.get(1);
            return canUseAsFuel(stack) || stack.getItem() == Items.BUCKET && itemStack.getItem() != Items.BUCKET;
        } else if (slot == 10) {
            return false;
        } else {
            // Recipe
            return true;
        }
    }

    @Override
    public void readNbt( NbtCompound tag) {
        super.readNbt(tag);
        Inventories.readNbt(tag, items);
        recipeItemsreadNbt(tag, recipeItems);
        if (tag.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
        this.processingTime = tag.getShort("ProcessingTime");
        this.fuelTime = tag.getShort("FuelTime");
        this.maxFuelTime = tag.getShort("MaxFuelTime");
        recipeString = tag.getString("CurrentRecipe");

        if (tag.contains("Speed")) {
            this.speed = tag.getShort("Speed");
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        recipeItemswriteNbt(tag, recipeItems);
        Inventories.writeNbt(tag, items);
        if (this.customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        tag.putShort("ProcessingTime", (short) this.processingTime);
        tag.putShort("FuelTime", (short) this.fuelTime);
        tag.putShort("MaxFuelTime", (short) this.maxFuelTime);
        tag.putString("CurrentRecipe", currentRecipe == null ? "null" : this.currentRecipe.getId().toString());
        tag.putShort("Speed", (short) this.speed);
        return super.writeNbt(tag);
    }

    public NbtCompound recipeItemswriteNbt(NbtCompound tag, DefaultedList<Item> itemList) {
        NbtList listTag = new NbtList();

        for (int i = 0; i < itemList.size(); ++i) {
            listTag.add(i, NbtString.of(Registry.ITEM.getId(itemList.get(i)).toString()));
        }

        if (!listTag.isEmpty()) {
            tag.put("RecipeItems", listTag);
        }

        return tag;
    }

    public void recipeItemsreadNbt(NbtCompound tag, DefaultedList<Item> itemList) {
        NbtList listTag = tag.getList("RecipeItems", 8);

        for (int i = 0; i < listTag.size(); ++i) {
            String item = listTag.getString(i);
            recipeItems.set(i, Registry.ITEM.get(Identifier.tryParse(item)));
        }
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, CrafterBlockEntity t) {
        if (world.isClient) {
            return;
        }

        if (t.firstTick) {
            if (!t.recipeString.equals("null") && !t.recipeString.isEmpty()) {
                t.currentRecipe = (CraftingRecipe) world.getRecipeManager().get(Identifier.tryParse(t.recipeString)).get();
                t.firstTick = false;
            }
        }

        if (t.isBurning()) {
            t.fuelTime--;
        }

        // Freeze when powered
        if (world.getBlockState(t.getPos()).get(Properties.POWERED).booleanValue()) {
            t.processingTime = 0;
            return;
        }

        if (t.currentRecipe == null) {
            return;
        }

        boolean changed = false;
        if (t.isBurning()) {
            t.processingTime++;
        }

        ItemStack itemStack = t.items.get(0);
        if (t.canAcceptOutput(t.currentRecipe)) {
            // Burn another item
            if (!t.isBurning()) {
                if (!itemStack.isEmpty()) {
                    t.maxFuelTime = t.getFuelTime(itemStack);
                    t.fuelTime = t.maxFuelTime;
                    changed = true;

                    Item item = itemStack.getItem();
                    itemStack.decrement(1);
                    if (itemStack.isEmpty()) {
                        Item item2 = item.getRecipeRemainder();
                        t.items.set(0, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                    }
                } else {
                    t.processingTime = 0;
                }
            }

            // Generate items
            if (t.processingTime == t.speed) {
                t.processingTime = 0;
                t.craftItem(t.currentRecipe);
                t.updateCurrentRecipe();
                changed = true;
            }
        } else {
            t.processingTime = 0;
        }

        if (changed) {
            t.markDirty();
        }
    }

    private boolean canAcceptOutput(CraftingRecipe recipe) {
        if (recipe == null) {
            return false;
        }

        // Check if all slots match recipe
        for (int i = 0; i < 9; i++) {
            if (items.get(i + 1).getItem() != recipeItems.get(i)) {
                return false;
            }
        }

        if (items.get(10).isEmpty()) {
            return true;
        }

        if (items.get(10).getItem() != recipe.getOutput().getItem()) {
            return false;
        }

        return (items.get(10).getCount() + recipe.getOutput().getCount()) <= recipe.getOutput().getMaxCount();
    }

    private void updateCurrentRecipe() {
        Collection<CraftingRecipe> recipes = VanillaAutomated.getOrCreateCraftingRecipes(getWorld());
        for (CraftingRecipe recipe : recipes) {
            if (recipe instanceof SpecialCraftingRecipe) {
                continue;
            }

            if (recipe instanceof ShapedRecipe) {
                if (checkShaped((ShapedRecipe) recipe)) {
                    currentRecipe = recipe;
                    return;
                }
            }

            if (recipe instanceof ShapelessRecipe) {
                if (checkShapeless((ShapelessRecipe) recipe)) {
                    currentRecipe = recipe;
                    return;
                }
            }
        }

        // Found none
        currentRecipe = null;
    }

    private boolean checkShaped(ShapedRecipe shapedRecipe) {
        for (int i = 0; i <= 3 - shapedRecipe.getWidth(); ++i) {
            for (int j = 0; j <= 3 - shapedRecipe.getHeight(); ++j) {
                if (this.matchesSmall(this, i, j, true, shapedRecipe)) {
                    return true;
                }

                if (this.matchesSmall(this, i, j, false, shapedRecipe)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchesSmall(Inventory inv, int offsetX, int offsetY, boolean bl, ShapedRecipe recipe) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                int k = i - offsetX;
                int l = j - offsetY;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < recipe.getWidth() && l < recipe.getHeight()) {
                    if (bl) {
                        ingredient = recipe.getIngredients().get(recipe.getWidth() - k - 1 + l * recipe.getWidth());
                    } else {
                        ingredient = recipe.getIngredients().get(k + l * recipe.getWidth());
                    }
                }

                if (!ingredient.test(inv.getStack(i + j * 3 + 1))) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkShapeless(ShapelessRecipe shapelessRecipe) {
        RecipeMatcher recipeFinder = new RecipeMatcher();

        int i = 0;

        for (int j = 0; j < 9; ++j) {
            ItemStack itemStack = new ItemStack(recipeItems.get(j));
            if (!itemStack.isEmpty()) {
                ++i;
                recipeFinder.addInput(itemStack, 1);
            }
        }

        return i == shapelessRecipe.getIngredients().size() && recipeFinder.match(shapelessRecipe, (IntList) null);
    }

    private void craftItem(CraftingRecipe craftingRecipe) {
        for (int i = 0; i < 9; i++) {
            if (items.get(i + 1).getCount() >= 1) {
                items.get(i + 1).decrement(1);
            }
        }

        if (items.get(10).getItem() == Items.AIR) {
            items.set(10, craftingRecipe.getOutput().copy());
        } else {
            items.set(10, new ItemStack(craftingRecipe.getOutput().getItem(), craftingRecipe.getOutput().getCount() + items.get(10).getCount()));
        }
    }

    protected int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            Item item = fuel.getItem();
            return (Integer) AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
        }
    }

    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{10};
        } else if (side == Direction.UP) {
            return new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        } else {
            return new int[]{0};
        }
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        boolean possible = this.isValid(slot, stack);

        if (slot > 0 && slot < 10) {
            possible = recipeItems.get(slot - 1) == stack.getItem();

            if (!items.get(slot).isEmpty()) {
                possible = false;
            }
        }

        return possible;
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN && slot == 0) {
            Item item = stack.getItem();
            if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
                return false;
            }
        } else if (slot == 10) {
            return true;
        }

        return false;
    }

    @Override
    public void clear() {
        items = DefaultedList.ofSize(11, ItemStack.EMPTY);
        recipeItems = DefaultedList.ofSize(9, Items.AIR);
    }

    public boolean isBurning() {
        return this.fuelTime > 0;
    }

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("block." + VanillaAutomated.prefix + ".crafter_block");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new CrafterBlockController(syncId, inventory, ScreenHandlerContext.create(world, pos), pos, getRecipeItems().toString().replace(" ", "").replace("[", "").replace("]", ""));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
        StringBuilder items = new StringBuilder();
        DefaultedList<Item> recipeItems = getRecipeItems();

        for (int i = 0; i < 9; i++) {
            items.append(Registry.ITEM.getId(recipeItems.get(i)).toString().replace(":", "."));
            if (i < 8) {
                items.append(",");
            }
        }
        packetByteBuf.writeString(items.toString());
    }
}
