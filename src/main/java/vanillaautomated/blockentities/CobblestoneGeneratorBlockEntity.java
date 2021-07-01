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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.gui.CobblestoneGeneratorBlockController;

import java.util.Random;

public class CobblestoneGeneratorBlockEntity extends MachineBlockEntity implements SidedInventory, PropertyDelegateHolder {

    public int speed = VanillaAutomated.config.cobblestoneGeneratorTime;
    private Random random = new Random();

    public CobblestoneGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(VanillaAutomatedBlocks.cobblestoneGeneratorBlockEntity, pos, state);
        items = DefaultedList.ofSize(4, ItemStack.EMPTY);
    }

    public static boolean canUseAsFuel(ItemStack stack) {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().containsKey(stack.getItem());
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, CobblestoneGeneratorBlockEntity t) {

        if (world.isClient) {
            return;
        }

        if (t.getItems().get(0).isEmpty() || t.getItems().get(1).isEmpty()) {
            t.setProcessingTime(0);
            return;
        }

        // Freeze when powered
        if (world.getBlockState(t.getPos()).get(Properties.POWERED)) {
            if (t.isBurning()) {
                t.fuelTime--;
            }

            return;
        }

        boolean changed = false;
        if (t.isBurning()) {
            t.processingTime++;
            t.fuelTime--;
        }

        ItemStack itemStack = t.items.get(2);
        if (t.canAcceptOutput()) {
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
                        t.items.set(2, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                    }
                } else {
                    t.processingTime = 0;
                }
            }

            // Generate items
            if (t.processingTime == t.speed) {
                t.processingTime = 0;
                t.generateItems();
                changed = true;
            }
        } else {
            t.processingTime = 0;
        }

        if (changed) {
            t.markDirty();
        }
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
        ItemStack itemStack = this.items.get(slot);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areTagsEqual(stack, itemStack);
        this.items.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
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
            return stack.getItem() == Items.WATER_BUCKET;
        } else if (slot == 1) {
            return stack.getItem() == Items.LAVA_BUCKET;
        } else if (slot == 2) {
            ItemStack itemStack = this.items.get(2);
            return canUseAsFuel(stack) || stack.getItem() == Items.BUCKET && itemStack.getItem() != Items.BUCKET;
        } else {
            return true;
        }
    }

    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    private boolean canAcceptOutput() {
        if (items.get(3).isEmpty() || items.get(3).getCount() < items.get(3).getMaxCount()) {
            return true;
        }

        return false;
    }

    private void generateItems() {
        if (items.get(3).isEmpty()) {
            items.set(3, new ItemStack(Items.COBBLESTONE, 1));
        } else {
            items.get(3).increment(1);
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
            return new int[]{3};
        } else {
            return new int[]{2};
        }
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 3;
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
        return new TranslatableText("block." + VanillaAutomated.prefix + ".cobblestone_generator_block");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new CobblestoneGeneratorBlockController(syncId, inventory, ScreenHandlerContext.create(world, pos));
    }
}
