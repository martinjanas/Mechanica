package martinjanas.mechanica.api.energy;

import net.neoforged.neoforge.energy.EnergyStorage;

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

    public void SetEnergy(int amount)
    {
        this.energy = amount;
    }
}
