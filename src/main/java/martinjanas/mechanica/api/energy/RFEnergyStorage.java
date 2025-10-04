package martinjanas.mechanica.api.energy;

import martinjanas.mechanica.api.packet.EnergyUpdatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;

public class RFEnergyStorage extends EnergyStorage
{
    public RFEnergyStorage(int capacity)
    {
        super(capacity);
    }

    public RFEnergyStorage(int capacity, int maxTransfer)
    {
        super(capacity, maxTransfer);
    }

    public RFEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy)
    {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public RFEnergyStorage(int capacity, int maxReceive, int maxExtract)
    {
        super(capacity, maxReceive, maxExtract);
    }

    private static int clamp(int value, int min, int max)
    {
        if (min > max) {
            throw new IllegalArgumentException(min + " > " + max);
        }
        return (int) Math.min(max, Math.max(value, min));
    }

    public void SetEnergy(int amount)
    {
        this.energy = amount;
    }

    public boolean IsFull()
    {
        return this.energy == this.getMaxEnergyStored();
    }

    public int receiveEnergyWithPacket(int toReceive, boolean simulate, Level level, BlockPos pos)
    {
        int value = super.receiveEnergy(toReceive, simulate);
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel)level, new ChunkPos(pos), new EnergyUpdatePacket(pos, this.getEnergyStored()));

        return value;
    }

    public int extractEnergyWithPacket(int toExtract, boolean simulate, Level level, BlockPos pos)
    {
        int value = super.extractEnergy(toExtract, simulate);
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel)level, new ChunkPos(pos), new EnergyUpdatePacket(pos, this.getEnergyStored()));

        return value;
    }
}
