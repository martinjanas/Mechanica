package martinjanas.mechanica.api.energy;

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
        var to_insert = Math.max(0, Math.min(max_input.GetJoules(), amount));
        buffer.Increase(to_insert);

        var joules = Math.max(0, Math.min(capacity.GetJoules(), buffer.GetJoules()));
        buffer.SetJoules(joules);
    }

    public void extract(long amount)
    {
        var to_remove = Math.max(0, Math.min(max_output.GetJoules(), amount));
        buffer.Decrease(to_remove);

        var joules = Math.max(0, Math.min(capacity.GetJoules(), buffer.GetJoules()));
        buffer.SetJoules(joules);
    }

    @Override
    public String toString()
    {
        return buffer.GetJoules() + "J, " + "kWh: " + buffer.ToKWH();
    }

    private EnergyUnit buffer;
    private final EnergyUnit capacity;
    private final EnergyUnit max_input;
    private final EnergyUnit max_output;
}
