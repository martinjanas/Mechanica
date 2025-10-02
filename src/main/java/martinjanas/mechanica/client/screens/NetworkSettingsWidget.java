package martinjanas.mechanica.client.screens;

import martinjanas.mechanica.api.network.EnergyNetwork;
import martinjanas.mechanica.api.network.NetworkManager;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class NetworkSettingsWidget
{
    public Vector2i pos, size;

    private EditBox network_name_input;
    private Button btn_add_network;
    private int selected_network = -1;

    final List<String> GetNetworkNames()
    {
        return new ArrayList<>(NetworkManager.Get().GetNetworks().keySet());
    }

    public NetworkSettingsWidget(Vector2i pos, Vector2i size)
    {
        this.pos = pos;
        this.size = size;
    }

    public void Init(Minecraft mc)
    {
        network_name_input = new EditBox(mc.font, this.pos.x + 10, this.pos.y + 30, this.size.x - 60, 20, Component.literal("Network Name"));
        btn_add_network = Button.builder(Component.literal("+"), btn ->
        {
            String name = network_name_input.getValue().trim();
            if (!name.isEmpty())
            {
                NetworkManager.Get().Register(name, EnergyNetwork::new);
                network_name_input.setValue("");
            }
        }).bounds(this.pos.x + this.size.x - 40, this.pos.y + 30, 30, 20).build();
    }

    public boolean KeyPressed(int keyCode, int scanCode, int modifiers)
    {
        return network_name_input.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean CharTyped(char codePoint, int modifiers)
    {
        return network_name_input.charTyped(codePoint, modifiers);
    }

    public void Render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Font font)
    {
        graphics.drawString(font, "Network Name:", this.pos.x + 10, this.pos.y + 10, 0xFFFFFF, false);
        network_name_input.render(graphics, mouseX, mouseY, partialTicks);
        btn_add_network.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawString(font, "Networks:", this.pos.x + 10, this.pos.y + 60, 0xFFFFFF, false);

        int listY = this.pos.y + 80;

        final var network_names = GetNetworkNames();
        for (int i = 0; i < network_names.size(); i++)
        {
            int entryY = listY + i * 18;
            int color = (i == selected_network) ? 0xFFAA00 : 0xFFFFFF;

            String network_name = network_names.get(i);
            graphics.drawString(font, network_name, this.pos.x + 20, entryY, color, false);
        }
    }

    public void MouseClicked(double mouseX, double mouseY, int button, BaseMachineBlockEntity machine)
    {
        boolean network_name_clicked = network_name_input.mouseClicked(mouseX, mouseY, button);
        network_name_input.setFocused(network_name_clicked);
        btn_add_network.mouseClicked(mouseX, mouseY, button);

        int listY = this.pos.y + 80;

        final var network_names = GetNetworkNames();
        for (int i = 0; i < network_names.size(); i++)
        {
            int entryY = listY + i * 18;
            if (mouseX >= this.pos.x + 20 && mouseX <= this.pos.x + this.size.x - 20 && mouseY >= entryY && mouseY <= entryY + 16)
            {
                selected_network = i;
                NetworkManager.Get().Join(network_names.get(i).toString(), machine);
            }
        }
    }
}
