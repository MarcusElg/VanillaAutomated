package vanillaautomated.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.Tickable;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;

public class TimerBlockEntity extends MachineBlockEntity implements Nameable, Tickable {
    private int currentTime = 0;
    private int time = 20;
    private boolean disabled = false;

    public TimerBlockEntity() {
        super(VanillaAutomatedBlocks.timerBlockEntity);
    }

    public void modifyTime(int time) {
        this.time += time;
        this.time = Math.max(2, this.time); // 1/10 second
        this.time = Math.min(72000, this.time); // 1 hour
    }

    public int getTime() {
        return time;
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

    @Override
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
