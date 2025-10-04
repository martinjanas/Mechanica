package martinjanas.mechanica.client.screens;

import martinjanas.mechanica.api.energy.RFEnergyStorage;
import martinjanas.mechanica.client.widgets.NetworkSettingsWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import martinjanas.mechanica.block_entities.BlockEntityGenerator;
import org.joml.Vector2i;

public class GeneratorScreen extends Screen
{
    private final BlockPos pos;
    private final Level level;
    private BlockEntityGenerator generator;
    private NetworkSettingsWidget widget;
    boolean display_network_settings = false;

    public GeneratorScreen(BlockEntityGenerator generator)
    {
        super(Component.literal("Generator"));
        this.pos = generator.getBlockPos();
        this.level = generator.getLevel();
        this.generator = generator;
    }

    @Override
    protected void init()
    {
        super.init();

        Vector2i btn_size = new Vector2i(120, 20);
        Vector2i btn_pos = new Vector2i((this.width - btn_size.x) / 2, 10);

        Vector2i overlay_size = new Vector2i(220, 140);
        Vector2i overlay_pos = new Vector2i((this.width - overlay_size.x) / 2, 40);

        widget = new NetworkSettingsWidget(overlay_pos, overlay_size, minecraft, this.generator);

        Button network_settings_widget = Button.builder(Component.literal("Network Settings"), btn -> {
            display_network_settings = !display_network_settings;
        }).bounds(btn_pos.x, btn_pos.y, btn_size.x, btn_size.y).build();

        this.addRenderableWidget(network_settings_widget);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);

        String title = this.title.getString();
        int titleY = this.height / 4;
        int titleWidth = this.font.width(title);
        graphics.drawString(this.font, title, (this.width - titleWidth) / 2, titleY, 0xFFFFFF, false);

        String energyText = "No data";
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockEntityGenerator gen)
        {
            RFEnergyStorage storage = gen.GetEnergyStorage();
            if (storage != null)
                energyText = storage.getEnergyStored() + " RF";
        }

        int energyWidth = this.font.width(energyText);
        graphics.drawString(this.font, energyText, (this.width - energyWidth) / 2, titleY + 20, 0xFFFF55, false);
        if (display_network_settings)
        {
            if (!this.renderables.contains(widget))
                this.addRenderableWidget(widget);
        }
        else if (!display_network_settings && this.renderables.contains(widget))
            this.removeWidget(widget);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
