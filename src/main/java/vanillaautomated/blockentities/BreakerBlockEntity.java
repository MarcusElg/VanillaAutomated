package vanillaautomated.blockentities;

import blue.endless.jankson.annotation.Nullable;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.blocks.BreakerBlock;
import vanillaautomated.gui.BreakerBlockController;

import java.util.List;
import java.util.Random;

public class BreakerBlockEntity extends MachineBlockEntity implements SidedInventory, PropertyDelegateHolder {

    public int speed = VanillaAutomated.config.breakerTime;
    private Random random = new Random();

    public BreakerBlockEntity(BlockPos pos, BlockState state) {
        super(VanillaAutomatedBlocks.breakerBlockEntity, pos, state);
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
            return stack.getItem() instanceof MiningToolItem;
        } else {
            ItemStack itemStack = this.items.get(1);
            return canUseAsFuel(stack) || stack.getItem() == Items.BUCKET && itemStack.getItem() != Items.BUCKET;
        }
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, BreakerBlockEntity t) {
        if (world.isClient) {
            return;
        }

        Direction direction = (Direction) world.getBlockState(t.pos).get(BreakerBlock.FACING);
        BlockPos position = new BlockPos(t.pos.getX() + direction.getOffsetX(), t.pos.getY() + direction.getOffsetY(), t.pos.getZ() + direction.getOffsetZ());

        if (t.isBurning()) {
            t.fuelTime--;
        }

        if (world.getBlockState(position).isAir() ||
                world.getBlockState(position).getHardness(world, position) > 16000000 ||
                world.getBlockState(position).getHardness(world, position) == -1 ||
                world.getBlockState(position).hasBlockEntity()) {
            t.processingTime = 0;
            return;
        }

        // Freeze when powered
        if (world.getBlockState(t.getPos()).get(Properties.POWERED)) {
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

        System.out.println(t.speed);
        // Generate items
        if (t.processingTime == t.speed) {
            t.processingTime = 0;
            t.generateItems();
            changed = true;
        }

        if (changed) {
            t.markDirty();
        }
    }

    private void generateItems() {
        PlayerEntity player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), Float.MAX_VALUE, false);
        if (player == null) {
            return;
        }

        Direction direction = (Direction) world.getBlockState(pos).get(BreakerBlock.FACING);
        BlockPos position = new BlockPos(pos.getX() + direction.getOffsetX(), pos.getY() + direction.getOffsetY(), pos.getZ() + direction.getOffsetZ());
        Block block = world.getBlockState(position).getBlock();
        LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).parameter(LootContextParameters.ORIGIN, new Vec3d(getPos().getX(), getPos().getY(), getPos().getZ())).parameter(LootContextParameters.TOOL, items.get(0)).parameter(LootContextParameters.THIS_ENTITY, player).parameter(LootContextParameters.BLOCK_STATE, world.getBlockState(position)).random(this.random);
        LootTable lootTable = this.world.getServer().getLootManager().getTable(block.getLootTableId());

        if (!items.get(0).isEmpty()) {
            MiningToolItem miningToolItem = (MiningToolItem) items.get(0).getItem();

            if (miningToolItem.isSuitableFor(world.getBlockState(position))) {
                items.get(0).damage(1, random, null);
            } else {
                items.get(0).damage(3, random, null);
            }

            if (items.get(0).getDamage() >= items.get(0).getMaxDamage()) {
                items.set(0, ItemStack.EMPTY);
            }
        }

        world.setBlockState(position, Blocks.AIR.getDefaultState());

        List<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.BLOCK));
        list.forEach((itemStack) -> {
            ItemEntity itemEntity = new ItemEntity(world, position.getX(), position.getY(), position.getZ(), itemStack.copy());
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        });
    }

    protected int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            Item item = fuel.getItem();
            return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
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
        return new TranslatableText("block." + VanillaAutomated.prefix + ".breaker_block");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new BreakerBlockController(syncId, inventory, ScreenHandlerContext.create(world, pos));
    }
}
