package martinjanas.mechanica.api.network.impl;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseNetwork<T>
{
    protected final Set<T> devices = new HashSet<>();

    public void Join(T device)
    {
        devices.add(device);
    }

    public void Disconnect(T device)
    {
        devices.remove(device);
    }

    public abstract void OnServerTick();
}
