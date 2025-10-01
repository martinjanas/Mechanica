package martinjanas.mechanica.api.network;

import martinjanas.mechanica.api.network.impl.BaseNetwork;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;

public class EnergyNetwork extends BaseNetwork<BaseMachineBlockEntity>
{
    public EnergyNetwork()
    {

    }

    @Override
    public void OnServerTick()
    {
        //TODO: Implement correct generator -> energy acceptor energy interaction
    }
}
