package vanillaautomated.blockentities;

import blue.endless.jankson.annotation.Nullable;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.VanillaAutomatedItems;

import java.util.List;
import java.util.Random;

public class MobFarmBlockEntity extends MachineBlockEntity implements SidedInventory, PropertyDelegateHolder, Nameable, Tickable {

    DefaultedList<ItemStack> items = DefaultedList.ofSize(11, ItemStack.EMPTY);
    private int processingTime;
    private int fuelTime;
    private int maxFuelTime;
    private int speed = 400; // TODO: config file
    private Random random = new Random();
    private final PropertyDelegate propertyDelegate;
    private String entityType = "";

    public MobFarmBlockEntity() {
        super(VanillaAutomatedBlocks.mobFarmBlockEntity);
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

    private void updateEntityType() {
        if (items.get(0) == ItemStack.EMPTY) {
            entityType = "";
        } else {
            ItemStack mobNet = items.get(0);
            entityType = mobNet.getTag().getString("EntityId");
        }
    }

    public DefaultedList<ItemStack> getItems() {
        return items;
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
        return (ItemStack) this.items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = Inventories.splitStack(this.items, slot, amount);

        if (slot == 0) {
            updateEntityType();
        }

        return stack;
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

        if (slot == 0) {
            updateEntityType();
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
            return stack.getItem() == VanillaAutomatedItems.mobNet && stack.hasTag() && stack.getTag().contains("EntityId");
        } else if (slot == 1) {
            ItemStack itemStack = this.items.get(1);
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
        this.entityType = tag.getString("EntityType");
        updateEntityType();
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
        tag.putString("EntityType", this.entityType);
        return super.toTag(tag);
    }

    public void tick() {
        if (world.isClient) {
            return;
        }

        if (entityType == "") {
            processingTime = 0;
            return;
        }

        // Freeze when powered
        if (world.getBlockState(getPos()).get(Properties.POWERED).booleanValue()) {
            if (this.isBurning()) {
                this.fuelTime--;
            }

            return;
        }

        boolean changed = false;
        if (this.isBurning()) {
            this.processingTime++;
            this.fuelTime--;
        }

        ItemStack itemStack = this.items.get(1);
        if (this.canAcceptOutput()) {
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
                        this.items.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                    }
                } else {
                    this.processingTime = 0;
                }
            }

            // Generate items
            if (this.processingTime == speed) {
                this.processingTime = 0;
                this.generateItems();
                changed = true;
            }
        } else {
            this.processingTime = 0;
        }

        if (changed) {
            this.markDirty();
        }
    }

    private boolean canAcceptOutput() {
        for (int i = 2; i < size(); i++) {
            if (items.get(i).isEmpty() || items.get(i).getCount() < items.get(i).getMaxCount()) {
                return true;
            }
        }

        return false;
    }

    private void generateItems() {
        PlayerEntity player = world.getClosestPlayer((int) pos.getX(), (int) pos.getY(), (int) pos.getZ(), Float.MAX_VALUE, false);
        if (player == null) {
            return;
        }

        CompoundTag entityData = items.get(0).getTag().getCompound("EntityData");
        LivingEntity entity = (LivingEntity) EntityType.loadEntityWithPassengers(entityData, world, (entityx) -> {
            return entityx;
        });

        LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).parameter(LootContextParameters.POSITION, getPos()).parameter(LootContextParameters.TOOL, ItemStack.EMPTY).parameter(LootContextParameters.THIS_ENTITY, player).random(this.random);
        LootTable lootTable = this.world.getServer().getLootManager().getTable(entity.getLootTable());
        List<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.FISHING));

        for (int i = 2; i < 11; i++) {
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).isEmpty()) {
                    continue;
                }

                if (items.get(i).isEmpty()) {
                    setStack(i, list.get(j));
                    list.set(j, ItemStack.EMPTY);
                } else if (items.get(i).isItemEqual(list.get(j))) {
                    int amountToAdd = items.get(i).getMaxCount() - items.get(i).getCount();
                    amountToAdd = Math.min(amountToAdd, list.get(j).getCount());
                    items.get(i).increment(amountToAdd);
                    list.get(j).decrement(amountToAdd);
                }
            }
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
            return new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10};
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

        if (slot == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void clear() {
        items = DefaultedList.ofSize(11, ItemStack.EMPTY);
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
        return new TranslatableText("block." + VanillaAutomated.prefix + ".mob_farm_block");
    }
}
