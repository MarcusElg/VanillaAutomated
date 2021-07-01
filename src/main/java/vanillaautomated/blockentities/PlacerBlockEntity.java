package vanillaautomated.blockentities;

import blue.endless.jankson.annotation.Nullable;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.block.*;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.BlockItem;
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
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.blocks.PlacerBlock;
import vanillaautomated.gui.PlacerBlockController;

import java.util.Random;

public class PlacerBlockEntity extends MachineBlockEntity implements SidedInventory, PropertyDelegateHolder, Nameable {

    public int speed = VanillaAutomated.config.placerTime;
    private Random random = new Random();

    public PlacerBlockEntity(BlockPos pos, BlockState state) {
        super(VanillaAutomatedBlocks.placerBlockEntity, pos, state);
        items = DefaultedList.ofSize(2, ItemStack.EMPTY);
    }

    public static boolean canUseAsFuel(ItemStack stack) {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().containsKey(stack.getItem());
    }

    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int size() {
        return 2;
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
            return stack.getItem() instanceof BlockItem && !(((BlockItem) stack.getItem()).getBlock() instanceof BlockWithEntity);
        } else {
            ItemStack itemStack = this.items.get(1);
            return canUseAsFuel(stack) || stack.getItem() == Items.BUCKET && itemStack.getItem() != Items.BUCKET;
        }
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, PlacerBlockEntity t) {
        if (world.isClient) {
            return;
        }

        if (t.isBurning()) {
            t.fuelTime--;
        }

        if (t.items.get(0).isEmpty()) {
            t.processingTime = 0;
            return;
        }

        // Freeze when powered
        if (world.getBlockState(t.getPos()).get(Properties.POWERED).booleanValue()) {
            t.processingTime = 0;
            return;
        }
        BlockPos pos = t.pos;
        PlayerEntity player = world.getClosestPlayer((int) pos.getX(), (int) pos.getY(), (int) pos.getZ(), Float.MAX_VALUE, false);
        if (player == null) {
            return;
        }

        Direction direction = (Direction) world.getBlockState(pos).get(PlacerBlock.FACING);
        BlockPos position = new BlockPos(pos.getX() + direction.getOffsetX(), pos.getY() + direction.getOffsetY(), pos.getZ() + direction.getOffsetZ());
        Block block = ((BlockItem) t.items.get(0).getItem()).getBlock();

        if (!block.canPlaceAt(block.getDefaultState(), world, position)) {
            t.processingTime = 0;
            return;
        }

        if (!world.getBlockState(position).getMaterial().isReplaceable()) {
            t.processingTime = 0;
            return;
        }

        boolean changed = false;
        if (t.isBurning()) {
            t.processingTime++;
        }

        ItemStack itemStack = t.items.get(1);
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
                    t.items.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                }
            } else {
                t.processingTime = 0;
            }
        }

        // Generate items
        if (t.processingTime == t.speed) {
            t.processingTime = 0;
            t.placeBlock(position, block);
            changed = true;
        }

        if (changed) {
            t.markDirty();
        }
    }

    private void placeBlock(BlockPos position, Block block) {
        BlockState blockState = block.getDefaultState();

        if (blockState.contains(FacingBlock.FACING)) {
            blockState = blockState.with(FacingBlock.FACING, world.getBlockState(pos).get(PlacerBlock.FACING).getOpposite());
        } else if (blockState.contains(HorizontalFacingBlock.FACING)) {
            blockState = blockState.with(HorizontalFacingBlock.FACING, world.getBlockState(pos).get(PlacerBlock.FACING).getOpposite());
        }

        world.setBlockState(position, blockState);

        block.onPlaced(world, position, blockState, null, ItemStack.EMPTY);
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

    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{};
        } else if (side == Direction.UP) {
            return new int[]{0};
        } else {
            return new int[]{1};
        }
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN && slot == 1) {
            Item item = stack.getItem();
            if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
                return false;
            }
        }

        return false;
    }

    @Override
    public void clear() {
        items = DefaultedList.ofSize(2, ItemStack.EMPTY);
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
        return new TranslatableText("block." + VanillaAutomated.prefix + ".placer_block");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new PlacerBlockController(syncId, inventory, ScreenHandlerContext.create(world, pos));
    }
}
