package vanillaautomated.blockentities;

import blue.endless.jankson.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import vanillaautomated.VanillaAutomated;

public class MachineBlockEntity extends BlockEntity implements Nameable {

    protected Text customName;

    public MachineBlockEntity(BlockEntityType<?> type) {
        super(type);
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
}
