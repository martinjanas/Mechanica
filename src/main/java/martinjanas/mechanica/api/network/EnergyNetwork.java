package martinjanas.mechanica.api.network;

import martinjanas.mechanica.api.network.impl.BaseNetwork;
import martinjanas.mechanica.block_entities.BlockEntityEnergyAcceptor;
import martinjanas.mechanica.block_entities.BlockEntityGenerator;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;

public class EnergyNetwork extends BaseNetwork<BaseMachineBlockEntity>
{
    long joules_per_tick = 0;

    public EnergyNetwork()
    {

    }

    @Override
    public void OnServerTick()
    {
        for (var device : devices)
        {
            if (device instanceof BlockEntityGenerator generator)
            {
                generator.GetEnergyStorage().Extract(generator.JOULES_PER_TICK);
                joules_per_tick = generator.JOULES_PER_TICK;
            }
            else if (device instanceof BlockEntityEnergyAcceptor)
                device.GetEnergyStorage().Insert(joules_per_tick);

            device.setChanged();
        }

        //TODO: Implement correct generator -> energy acceptor energy interaction
    }
}
