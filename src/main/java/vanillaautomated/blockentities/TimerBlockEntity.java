package vanillaautomated.blockentities;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.gui.TimerController;

public class TimerBlockEntity extends MachineBlockEntity implements Nameable, ExtendedScreenHandlerFactory {
    private int currentTime = 0;
    private int time = 20;
    private boolean disabled = false;

    public TimerBlockEntity(BlockPos pos, BlockState state) {
        super(VanillaAutomatedBlocks.timerBlockEntity, pos, state);
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
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (tag.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
        this.currentTime = tag.getInt("CurrentTime");
        this.time = tag.getInt("Time");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        if (this.customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        tag.putInt("CurrentTime", this.currentTime);
        tag.putInt("Time", this.time);
        return super.writeNbt(tag);
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("block." + VanillaAutomated.prefix + ".timer");
    }

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

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new TimerController(syncId, inventory, ScreenHandlerContext.create(world, pos), pos, time);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
        packetByteBuf.writeInt(time);
    }
}
