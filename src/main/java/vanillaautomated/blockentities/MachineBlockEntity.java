package vanillaautomated.blockentities;

import blue.endless.jankson.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import vanillaautomated.VanillaAutomated;

public class MachineBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {

    protected final PropertyDelegate propertyDelegate;

    protected Text customName;
    protected int processingTime;
    protected int fuelTime;
    protected int maxFuelTime;
    protected DefaultedList<ItemStack> items;

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                switch (index) {
                    case 0:
                        return fuelTime;
                    case 1:
                        return processingTime;
                    case 2:
                        return maxFuelTime;
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
                    default:
                        break;
                }

            }

            public int size() {
                return 4;
            }
        };
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
    }

    public Text getName() {
        return this.customName != null ? this.customName : this.getContainerName();
    }

    public Text getDisplayName() {
        return this.getName();
    }

    @Nullable
    public Text getCustomName() {
        return this.customName;
    }

    protected Text getContainerName() {
        return new TranslatableText("block." + VanillaAutomated.prefix + ".mob_farm_block");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return null;
    }


    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        Inventories.readNbt(tag, items);
        if (tag.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
        this.processingTime = tag.getShort("ProcessingTime");
        this.fuelTime = tag.getShort("FuelTime");
        this.maxFuelTime = tag.getShort("MaxFuelTime");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        Inventories.writeNbt(tag, items);
        if (this.customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        tag.putShort("ProcessingTime", (short) this.processingTime);
        tag.putShort("FuelTime", (short) this.fuelTime);
        tag.putShort("MaxFuelTime", (short) this.maxFuelTime);
        return super.writeNbt(tag);
    }

}
