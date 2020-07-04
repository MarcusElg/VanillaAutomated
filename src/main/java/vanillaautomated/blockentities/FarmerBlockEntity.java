package vanillaautomated.blockentities;

import blue.endless.jankson.annotation.Nullable;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.gui.FarmerBlockController;
import vanillaautomated.recipes.FarmerRecipe;

public class FarmerBlockEntity extends MachineBlockEntity implements SidedInventory, Tickable, PropertyDelegateHolder {

    DefaultedList<ItemStack> items = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private int processingTime;
    private int fuelTime;
    private int maxFuelTime;
    public int speed = 2400;
    public int setSpeed = 2400;
    public int spedUpSpeed = 20;
    private final PropertyDelegate propertyDelegate;
    public boolean spedUp = false; // Used bonemeal
    private String recipeString = "null";
    private FarmerRecipe currentRecipe = null;
    boolean firstTick = true;

    public FarmerBlockEntity() {
        super(VanillaAutomatedBlocks.farmerBlockEntity);
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

    @Override
    public int size() {
        return 4;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return (ItemStack) this.items.get(slot);
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
        ItemStack itemStack = this.items.get(slot);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areTagsEqual(stack, itemStack);
        this.items.set(slot, stack);
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
            return true;
        } else if (slot == 1) {
            return stack.getItem() == Items.BONE_MEAL;
        } else if (slot == 2) {
            ItemStack itemStack = this.items.get(2);
            return canUseAsFuel(stack) || stack.getItem() == Items.BUCKET && itemStack.getItem() != Items.BUCKET;
        } else {
            return false;
        }
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        Inventories.fromTag(tag, items);
        if (tag.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
        this.processingTime = tag.getShort("ProcessingTime");
        this.fuelTime = tag.getShort("FuelTime");
        this.maxFuelTime = tag.getShort("MaxFuelTime");
        this.spedUp = tag.getBoolean("SpedUp");

        if (spedUp) {
            speed = 20;
        }

        recipeString = tag.getString("CurrentRecipe");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, items);
        if (this.customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        tag.putShort("ProcessingTime", (short) this.processingTime);
        tag.putShort("FuelTime", (short) this.fuelTime);
        tag.putShort("MaxFuelTime", (short) this.maxFuelTime);
        tag.putBoolean("SpedUp", this.spedUp);
        tag.putString("CurrentRecipe", currentRecipe == null ? "null" : this.currentRecipe.getId().toString());
        return super.toTag(tag);
    }

    public void tick() {
        if (world.isClient) {
            return;
        }

        if (firstTick) {
            if (!recipeString.equals("null") && !recipeString.isEmpty()) {
                this.currentRecipe = (FarmerRecipe) world.getRecipeManager().get(Identifier.tryParse(recipeString)).get();
                firstTick = false;
            }
        }

        boolean changed = false;
        if (this.isBurning()) {
            this.fuelTime--;
        }

        // Freeze when powered
        if (world.getBlockState(getPos()).get(Properties.POWERED).booleanValue()) {
            return;
        }

        ItemStack itemStack = this.items.get(2);
        if (this.canAcceptOutput(currentRecipe) && !this.items.get(0).isEmpty()) {
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
                        this.items.set(2, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                    }
                } else {
                    this.processingTime = 0;
                }
            }

            if (currentRecipe == null) {
                processingTime = 0;
                return;
            } else {
                if (isBurning()) {
                    processingTime++;
                    updateSpeed();
                }
            }

            // Generate items
            if (this.processingTime >= speed) {
                this.processingTime = 0;
                this.generateItems(currentRecipe);
                spedUp = false;

                changed = true;
            }
        } else {
            this.processingTime = 0;
        }

        if (changed) {
            this.markDirty();
        }
    }

    private void updateSpeed() {
        if (!spedUp) {
            if (!items.get(1).isEmpty()) {
                speed = spedUpSpeed;
                items.get(1).decrement(1);
                spedUp = true;
            } else {
                speed = setSpeed;
                spedUp = false;
            }
        }
    }

    private boolean canAcceptOutput(FarmerRecipe recipe) {
        if (recipe == null) {
            return false;
        }

        if (items.get(3).isEmpty()) {
            return true;
        }

        if (items.get(3).getItem() != recipe.getOutput().getItem()) {
            return false;
        }

        return (items.get(3).getCount() + recipe.getOutput().getCount()) <= 64;
    }

    private void updateCurrentRecipe () {
        currentRecipe = (FarmerRecipe) this.world.getRecipeManager().getFirstMatch(VanillaAutomated.farmerRecipeType, this, this.world).orElse((Object) null);
    }

    private void generateItems(FarmerRecipe recipe) {
        ItemStack output = recipe.getOutput();

        if (items.get(3).isEmpty()) {
            items.set(3, output.copy());
        } else {
            items.set(3, new ItemStack(output.getItem(), items.get(3).getCount() + output.getCount()));
        }
        items.get(0).decrement(1);
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
            return new int[]{3};
        } else if (side == Direction.UP) {
            return new int[]{0, 1};
        } else {
            return new int[]{2};
        }
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (slot == 0 || slot == 1) {
            return false;
        }

        if (dir == Direction.DOWN && slot == 2) {
            Item item = stack.getItem();
            if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void clear() {
        items = DefaultedList.ofSize(4, ItemStack.EMPTY);
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
        return new TranslatableText("block." + VanillaAutomated.prefix + ".farmer_block");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new FarmerBlockController(syncId, inventory, ScreenHandlerContext.create(world, pos));
    }
}
