package martinjanas.mechanica.api.energy;

/*
*
* Todo: Brainstorm this $hit later
*
* */
public class EnergyStorage
{
    private EnergyUnit buffer;
    private final EnergyUnit capacity;
    private final EnergyUnit max_input;
    private final EnergyUnit max_output;

    public EnergyStorage(EnergyUnit capacity, EnergyUnit max_input, EnergyUnit max_output)
    {
        this.capacity = capacity;
        this.max_input = max_input;
        this.max_output = max_output;

        buffer = new EnergyUnit(0);
    }

    public EnergyStorage(double capacity_kwh, double max_input_kwh, double max_output_kwh)
    {
        this(new EnergyUnit(capacity_kwh), new EnergyUnit(max_input_kwh), new EnergyUnit(max_output_kwh));
    }

    public long Insert(long amount)
    {
        var to_insert = Math.clamp(amount, 0, max_input.GetJoules());
        buffer.AddJoules(to_insert);

        var joules = Math.clamp(buffer.GetJoules(), 0, capacity.GetJoules());
        buffer.SetJoules(joules);

        return to_insert;
    }

    public long Extract(long amount)
    {
        var to_remove = Math.clamp(amount, 0, max_output.GetJoules());
        buffer.RemoveJoules(to_remove);

        var joules = Math.clamp(buffer.GetJoules(), 0, capacity.GetJoules());
        buffer.SetJoules(joules);

        return to_remove;
    }

    public void SetStored(long joules)
    {
        buffer.SetJoules(joules);
    }

    public long GetStored()
    {
        return buffer.GetJoules();
    }

    public boolean IsGenerator()
    {
        return false;
    }

    @Override
    public String toString()
    {
        String string = String.format(java.util.Locale.US, "%d J, %.1f kWh", buffer.GetJoules(), buffer.ToKWH());

        return string;
    }
}
