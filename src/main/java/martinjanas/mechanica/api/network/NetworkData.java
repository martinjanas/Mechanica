package martinjanas.mechanica.api.network;

import net.minecraft.core.BlockPos;
import java.util.List;

public record NetworkData(String network_name, NetworkType network_type, List<BlockPos> device_positions)
{

}
