package martinjanas.mechanica.api.energy;

public class EnergyUnit
{
    private long joules;

    public EnergyUnit(long joules)
    {
        this.joules = joules;
    }

    public EnergyUnit(double kwh)
    {
        this.joules = Math.round(kwh * 3_600_000);
    }

    public double ToKWH()
    {
        double kwh = (double)joules / (double)3_600_000;
        return kwh;
    }

    public long GetJoules()
    {
        return joules;
    }

    public void SetJoules(long amount)
    {
        joules = amount;
    }

    public void AddJoules(long joules)
    {
        this.joules += joules;
    }

    public void RemoveJoules(long joules)
    {
        this.joules -= joules;
    }
}
