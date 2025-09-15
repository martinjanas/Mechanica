package martinjanas.mechanica.api.energy;

/*
*
* Todo: Brainstorm this $hit later
*
* */

public class EnergyBuffer
{
    public EnergyBuffer(EnergyUnit capacity, EnergyUnit max_input, EnergyUnit max_output)
    {
        this.capacity = capacity;
        this.max_input = max_input;
        this.max_output = max_output;

        buffer = new EnergyUnit(0);
    }

    public EnergyBuffer(double capacity_kwh, double max_input_kwh, double max_output_kwh)
    {
        this(new EnergyUnit(capacity_kwh), new EnergyUnit(max_input_kwh), new EnergyUnit(max_output_kwh));
    }

    public void insert(long amount)
    {
        var to_insert = Math.clamp(amount, 0, max_input.ToJoules());
        buffer.Increase(to_insert);

        var joules = Math.clamp(buffer.ToJoules(), 0, capacity.ToJoules());
        buffer.SetJoules(joules);
    }

    public void extract(long amount)
    {
        var to_remove = Math.clamp(amount, 0, max_output.ToJoules());
        buffer.Decrease(to_remove);

        var joules = Math.clamp(buffer.ToJoules(), 0, capacity.ToJoules());
        buffer.SetJoules(joules);
    }

    public void set_joules(long joules)
    {
        buffer.SetJoules(joules);
    }

    public long get_joules()
    {
        return buffer.ToJoules();
    }

    @Override
    public String toString()
    {
        return buffer.ToJoules() + "J, " + "kWh: " + buffer.ToKWH();
    }

    private final EnergyUnit capacity;
    private final EnergyUnit max_input;
    private final EnergyUnit max_output;

    private EnergyUnit buffer;
}
