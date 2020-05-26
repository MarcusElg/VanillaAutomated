package vanillaautomated.blockentities;

import blue.endless.jankson.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.Tickable;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;

import java.util.logging.Logger;

public class TimerBlockEntity extends BlockEntity implements Nameable, Tickable {
    private Text customName;
    private int currentTime = 0;
    private int time = 20;
    private boolean disabled = false;

    public TimerBlockEntity() {
        super(VanillaAutomatedBlocks.timerBlockEntity);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (tag.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
        this.currentTime = tag.getInt("CurrentTime");
        this.time = tag.getInt("Time");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (this.customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        tag.putInt("CurrentTime", this.currentTime);
        tag.putInt("Time", this.time);
        return super.toTag(tag);
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
        return new TranslatableText("block." + VanillaAutomated.prefix + ".timer");
    }

    @Override
    public void tick() {
        if (world.isClient()) {
            return;
        }

        currentTime++;

        if (currentTime >= time) {
            world.setBlockState(pos, VanillaAutomatedBlocks.timerBlock.getDefaultState().with(Properties.ENABLED, true));
            currentTime = -1;
            disabled = false;
        } else if (!disabled) {
            world.setBlockState(pos, VanillaAutomatedBlocks.timerBlock.getDefaultState().with(Properties.ENABLED, false));
            disabled = true;
        }
    }
}
