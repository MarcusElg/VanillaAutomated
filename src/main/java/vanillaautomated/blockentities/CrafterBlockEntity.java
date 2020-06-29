package vanillaautomated.blockentities;

import blue.endless.jankson.annotation.Nullable;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.recipe.*;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.gui.CrafterBlockController;

import java.util.Collection;
import java.util.Random;

public class CrafterBlockEntity extends MachineBlockEntity implements SidedInventory, Tickable, PropertyDelegateHolder {

    DefaultedList<ItemStack> items = DefaultedList.ofSize(11, ItemStack.EMPTY);
    DefaultedList<Item> recipeItems = DefaultedList.ofSize(9, Items.AIR);
    private int processingTime;
    private int fuelTime;
    private int maxFuelTime;
    private int speed = 10; // TODO: config file
    private Random random = new Random();
    private final PropertyDelegate propertyDelegate;
    private String recipeString = "null";
    private CraftingRecipe currentRecipe = null;
    boolean firstTick = true;

    public CrafterBlockEntity() {
        super(VanillaAutomatedBlocks.crafterBlockEntity);
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

    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public DefaultedList<Item> getRecipeItems() {
        return recipeItems;
    }

    public void resetRecipe() {
        recipeItems.clear();
        ItemScatterer.spawn(world, pos, this);
        items.clear();
    }

    public void resetRecipeClient () {
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
            recipeItems.set(slot - 1, stack.getItem());

            if (world.isClient) {
                Screen currentScreen = MinecraftClient.getInstance().currentScreen;
                if (currentScreen == null || !(currentScreen instanceof ScreenHandlerProvider)) {

                } else {
                    ((CrafterBlockController) ((ScreenHandlerProvider) currentScreen).getScreenHandler()).itemSprites.get(slot - 1).setItem(new ItemStack(stack.getItem(), 1));
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
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        Inventories.fromTag(tag, items);
        recipeItemsFromTag(tag, recipeItems);
        if (tag.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
        this.processingTime = tag.getShort("ProcessingTime");
        this.fuelTime = tag.getShort("FuelTime");
        this.maxFuelTime = tag.getShort("MaxFuelTime");
        recipeString = tag.getString("CurrentRecipe");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        recipeItemsToTag(tag, recipeItems);
        Inventories.toTag(tag, items);
        if (this.customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        tag.putShort("ProcessingTime", (short) this.processingTime);
        tag.putShort("FuelTime", (short) this.fuelTime);
        tag.putShort("MaxFuelTime", (short) this.maxFuelTime);
        tag.putString("CurrentRecipe", currentRecipe == null ? "null" : this.currentRecipe.getId().toString());
        return super.toTag(tag);
    }

    public CompoundTag recipeItemsToTag(CompoundTag tag, DefaultedList<Item> itemList) {
        ListTag listTag = new ListTag();

        for (int i = 0; i < itemList.size(); ++i) {
            listTag.add(i, StringTag.of(Registry.ITEM.getId(itemList.get(i)).toString()));
        }

        if (!listTag.isEmpty()) {
            tag.put("RecipeItems", listTag);
        }

        return tag;
    }

    public void recipeItemsFromTag(CompoundTag tag, DefaultedList<Item> itemList) {
        ListTag listTag = tag.getList("RecipeItems", 8);

        for (int i = 0; i < listTag.size(); ++i) {
            String item = listTag.getString(i);
            recipeItems.set(i, Registry.ITEM.get(Identifier.tryParse(item)));
        }
    }

    public void tick() {
        if (world.isClient) {
            return;
        }

        if (firstTick) {
            if (!recipeString.equals("null") && !recipeString.isEmpty()) {
                this.currentRecipe = (CraftingRecipe) world.getRecipeManager().get(Identifier.tryParse(recipeString)).get();
                firstTick = false;
            }
        }

        if (this.isBurning()) {
            this.fuelTime--;
        }

        if (items.get(0).isEmpty()) {
            this.processingTime = 0;
            return;
        }

        // Freeze when powered
        if (world.getBlockState(getPos()).get(Properties.POWERED).booleanValue()) {
            this.processingTime = 0;
            return;
        }

        if (currentRecipe == null) {
            return;
        }

        boolean changed = false;
        if (this.isBurning()) {
            this.processingTime++;
        }

        ItemStack itemStack = this.items.get(0);
        if (canAcceptOutput(currentRecipe)) {
            // Burn another item
            if (!this.isBurning()) {
                if (!itemStack.isEmpty()) {
                    this.maxFuelTime = this.getFuelTime(itemStack);
                    this.fuelTime = this.maxFuelTime;
                    changed = true;

                    Item item = itemStack.getItem();
                    itemStack.decrement(1);
                    if (itemStack.isEmpty()) {
                        Item item2 = item.getRecipeRemainder();
                        this.items.set(0, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                    }
                } else {
                    this.processingTime = 0;
                }
            }

            // Generate items
            if (this.processingTime == speed) {
                this.processingTime = 0;
                this.craftItem(currentRecipe);
                updateCurrentRecipe();
                changed = true;
            }
        } else {
            processingTime = 0;
        }

        if (changed) {
            this.markDirty();
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

        return (items.get(10).getCount() + recipe.getOutput().getCount()) <= 64;
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
                        ingredient = recipe.getPreviewInputs().get(recipe.getWidth() - k - 1 + l * recipe.getWidth());
                    } else {
                        ingredient = recipe.getPreviewInputs().get(k + l * recipe.getWidth());
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
        RecipeFinder recipeFinder = new RecipeFinder();
        int i = 0;

        for (int j = 0; j < 9; ++j) {
            ItemStack itemStack = new ItemStack(recipeItems.get(j));
            if (!itemStack.isEmpty()) {
                ++i;
                recipeFinder.method_20478(itemStack, 1);
            }
        }

        return i == shapelessRecipe.getPreviewInputs().size() && recipeFinder.findRecipe(shapelessRecipe, (IntList) null);
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

    public static boolean canUseAsFuel(ItemStack stack) {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().containsKey(stack.getItem());
    }

    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{11};
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
        }

        return possible;
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN && slot == 0) {
            Item item = stack.getItem();
            if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
                return false;
            }
        } else if (slot == 11) {
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

}
