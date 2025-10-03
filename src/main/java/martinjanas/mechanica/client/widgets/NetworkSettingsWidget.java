package martinjanas.mechanica.client.widgets;

import martinjanas.mechanica.api.network.ClientNetworkManager;
import martinjanas.mechanica.api.packet.JoinNetworkPacket;
import martinjanas.mechanica.api.packet.RegisterNetworkPacket;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class NetworkSettingsWidget extends AbstractWidget
{
    public Vector2i pos, size;

    private EditBox name_input;
    private Button btn_add_network;
    private int selected_network = -1;
    private int scroll_offset = 0;
    private static final int MAX_VISIBLE_NETWORKS = 3;

    private boolean is_dragging_thumb = false;
    private int drag_start_y = 0;
    private int drag_start_offset = 0;

    Font font;
    BaseMachineBlockEntity machine;

    List<String> network_names = new ArrayList<>(ClientNetworkManager.Get().GetNetworks().keySet());

    public static NetworkSettingsWidget INSTANCE;

    public NetworkSettingsWidget(Vector2i pos, Vector2i size, Minecraft mc, BaseMachineBlockEntity machine)
    {
        super(pos.x, pos.y, size.x, size.y, Component.literal("Network Settings"));
        this.pos = pos;
        this.size = size;

        name_input = new EditBox(mc.font, this.pos.x + 10, this.pos.y + 30, this.size.x - 60, 20, Component.literal("Network Name"));
        btn_add_network = Button.builder(Component.literal("+"), btn ->
        {
            String name = name_input.getValue().trim();
            if (!name.isEmpty())
            {
                Minecraft.getInstance().getConnection().send(new RegisterNetworkPacket(name));
                name_input.setValue("");
            }
        }).bounds(this.pos.x + this.size.x - 40, this.pos.y + 30, 30, 20).build();

        this.font = mc.font;
        this.machine = machine;
        INSTANCE = this;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 100); // Move 100 units forward in z

        graphics.fill(pos.x, pos.y, pos.x + size.x, pos.y + size.y, 0xAA000000);

        graphics.drawString(font, "Network Name:", this.pos.x + 10, this.pos.y + 10, 0xFFFFFF, false);
        name_input.render(graphics, mouseX, mouseY, partialTick);
        btn_add_network.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawString(font, "Networks:", this.pos.x + 10, this.pos.y + 60, 0xFFFFFF, false);

        int listY = this.pos.y + 80;
        int total_networks = network_names.size();
        int visible_count = Math.min(MAX_VISIBLE_NETWORKS, total_networks);
        int start_index = scroll_offset;
        int end_index = Math.min(start_index + MAX_VISIBLE_NETWORKS, total_networks);

        for (int i = start_index; i < end_index; i++)
        {
            int entryY = listY + (i - start_index) * 18;
            int color = (i == selected_network) ? 0xFFAA00 : 0xFFFFFF;
            String network_name = network_names.get(i);
            graphics.drawString(font, network_name, this.pos.x + 20, entryY, color, false);
        }

        // Draw scrollbar if needed
        if (total_networks > MAX_VISIBLE_NETWORKS)
        {
            int scrollbarX = this.pos.x + this.size.x - 16;
            int scrollbarY = listY;
            int scrollbarHeight = MAX_VISIBLE_NETWORKS * 18;
            graphics.fill(scrollbarX, scrollbarY, scrollbarX + 8, scrollbarY + scrollbarHeight, 0xFF333333);

            // Thumb size and position
            int thumbHeight = Math.max(18, scrollbarHeight * MAX_VISIBLE_NETWORKS / total_networks);
            int maxScroll = total_networks - MAX_VISIBLE_NETWORKS;
            int thumbY = scrollbarY + (maxScroll == 0 ? 0 : (scroll_offset * (scrollbarHeight - thumbHeight) / maxScroll));
            graphics.fill(scrollbarX, thumbY, scrollbarX + 8, thumbY + thumbHeight, 0xFFAAAAAA);

            // Up arrow (triangle)
            int arrowCenterX = scrollbarX + 4;
            int upArrowY = scrollbarY - 10;
            graphics.fill(arrowCenterX - 3, upArrowY + 3, arrowCenterX + 3, upArrowY + 4, 0xFF666666); // base
            graphics.fill(arrowCenterX - 2, upArrowY + 2, arrowCenterX + 2, upArrowY + 3, 0xFF666666);
            graphics.fill(arrowCenterX - 1, upArrowY + 1, arrowCenterX + 1, upArrowY + 2, 0xFF666666);
            graphics.fill(arrowCenterX, upArrowY, arrowCenterX + 1, upArrowY + 1, 0xFF666666); // tip

            // Down arrow (triangle)
            int downArrowY = scrollbarY + scrollbarHeight + 10;
            graphics.fill(arrowCenterX - 3, downArrowY, arrowCenterX + 3, downArrowY + 1, 0xFF666666); // base
            graphics.fill(arrowCenterX - 2, downArrowY + 1, arrowCenterX + 2, downArrowY + 2, 0xFF666666);
            graphics.fill(arrowCenterX - 1, downArrowY + 2, arrowCenterX + 1, downArrowY + 3, 0xFF666666);
            graphics.fill(arrowCenterX, downArrowY + 3, arrowCenterX + 1, downArrowY + 4, 0xFF666666); // tip
        }

        graphics.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        boolean network_name_clicked = name_input.mouseClicked(mouseX, mouseY, button);
        name_input.setFocused(network_name_clicked);
        btn_add_network.mouseClicked(mouseX, mouseY, button);

        int list_y = this.pos.y + 80;
        int total_networks = network_names.size();
        int start_index = scroll_offset;
        int end_index = Math.min(start_index + MAX_VISIBLE_NETWORKS, total_networks);

        for (int i = scroll_offset; i < end_index; i++)
        {
            int entry_y = list_y + (i - start_index) * 18;
            if (mouseX >= this.pos.x + 20 && mouseX <= this.pos.x + this.size.x - 36 && mouseY >= entry_y && mouseY <= entry_y + 16)
            {
                selected_network = i;
                Minecraft.getInstance().getConnection().send(new JoinNetworkPacket(network_names.get(i), machine.getBlockPos()));
            }
        }

        // Scrollbar interaction
        if (total_networks > MAX_VISIBLE_NETWORKS)
        {
            Vector2i scrollbar_offset = new Vector2i(this.pos.x + this.size.x - 16, list_y);
            int scrollbar_h = MAX_VISIBLE_NETWORKS * 18;

            int thumb_h = Math.max(18, scrollbar_h * MAX_VISIBLE_NETWORKS / total_networks);
            int max_scroll = total_networks - MAX_VISIBLE_NETWORKS;
            int thumb_y = scrollbar_offset.y + (max_scroll == 0 ? 0 : (scroll_offset * (scrollbar_h - thumb_h) / max_scroll));

            if (mouseX >= scrollbar_offset.x && mouseX <= scrollbar_offset.x + 8 && mouseY >= scrollbar_offset.y - 14 && mouseY <= scrollbar_offset.y - 6)
                if (scroll_offset > 0)
                    scroll_offset--;

            if (mouseX >= scrollbar_offset.x && mouseX <= scrollbar_offset.x + 8 && mouseY >= scrollbar_offset.y + scrollbar_h + 6 && mouseY <= scrollbar_offset.y + scrollbar_h + 14)
                if (scroll_offset < max_scroll)
                    scroll_offset++;

            if (mouseX >= scrollbar_offset.x && mouseX <= scrollbar_offset.x + 8 && mouseY >= thumb_y && mouseY <= thumb_y + thumb_h)
            {
                is_dragging_thumb = true;
                drag_start_y = (int)mouseY;
                drag_start_offset = scroll_offset;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        is_dragging_thumb = false;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (!is_dragging_thumb)
            return false;

        int total_networks = network_names.size();
        int scrollbar_height = MAX_VISIBLE_NETWORKS * 18;
        int thumb_height = Math.max(18, scrollbar_height * MAX_VISIBLE_NETWORKS / total_networks);

        int max_scroll = total_networks - MAX_VISIBLE_NETWORKS;
        if (max_scroll <= 0)
            return false;

        int delta_y = (int)mouseY - drag_start_y;
        int scroll_range = scrollbar_height - thumb_height;
        int new_offset = drag_start_offset + Math.round((float)delta_y * max_scroll / (float)Math.max(1, scroll_range));
        scroll_offset = Math.max(0, Math.min(max_scroll, new_offset));

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput)
    {

    }

    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        return name_input.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return name_input.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        int total_networks = network_names.size();
        int maxScroll = Math.max(0, total_networks - MAX_VISIBLE_NETWORKS);
        int listY = this.pos.y + 80;
        int listHeight = MAX_VISIBLE_NETWORKS * 18;
        int scrollbarX = this.pos.x + this.size.x - 16;
        int scrollbarY = listY;
        int scrollbarHeight = listHeight;

        // Check if mouse is over the network list or scrollbar
        boolean overList = mouseX >= this.pos.x + 10 && mouseX <= this.pos.x + this.size.x - 36 && mouseY >= listY && mouseY <= listY + listHeight;
        boolean overScrollbar = mouseX >= scrollbarX && mouseX <= scrollbarX + 8 && mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight;

        if ((overList || overScrollbar) && maxScroll > 0)
        {
            if (scrollY < 0 && scroll_offset < maxScroll) // Scroll down
                scroll_offset++;
            else if (scrollY > 0 && scroll_offset > 0) // Scroll up
                scroll_offset--;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public void RefreshNetworkList()
    {
        this.network_names = new ArrayList<>(ClientNetworkManager.Get().GetNetworks().keySet());

        // Reset selection if the previous selection is no longer valid
        if (selected_network >= network_names.size()) {
            selected_network = -1;
        }

        // Reset scroll if needed
        scroll_offset = 0;
    }
}
