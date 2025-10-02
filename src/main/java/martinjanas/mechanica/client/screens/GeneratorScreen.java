package martinjanas.mechanica.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;

import martinjanas.mechanica.api.energy.EnergyStorage;
import martinjanas.mechanica.block_entities.BlockEntityGenerator;
import org.joml.Vector2i;

public class GeneratorScreen extends Screen
{
    private final BlockPos pos;
    private final Level level;
    private Button network_settings_widget;
    private NetworkSettingsWidget widget_network_settings;
    private boolean display_network_settings = false;
    BlockEntityGenerator generator;

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

        network_settings_widget = Button.builder(Component.literal("Network Settings"), btn -> {
            display_network_settings = true;
        }).bounds(btn_pos.x, btn_pos.y, btn_size.x, btn_size.y).build();

        this.addRenderableWidget(network_settings_widget);

        Vector2i overlay_size = new Vector2i(220, 140);
        Vector2i overlay_pos = new Vector2i((this.width - overlay_size.x) / 2, 40);

        widget_network_settings = new NetworkSettingsWidget(overlay_pos, overlay_size);
        widget_network_settings.Init(minecraft);
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
            EnergyStorage storage = gen.GetEnergyStorage();
            if (storage != null)
                energyText = storage.toString();
        }

        int energyWidth = this.font.width(energyText);
        graphics.drawString(this.font, energyText, (this.width - energyWidth) / 2, titleY + 20, 0xFFFF55, false);

        if (display_network_settings)
        {
            graphics.fill(widget_network_settings == null ? 0 : widget_network_settings.pos.x, widget_network_settings == null ? 0 : widget_network_settings.pos.y, widget_network_settings == null ? 0 : widget_network_settings.pos.x + widget_network_settings.size.x, widget_network_settings == null ? 0 : widget_network_settings.pos.y + widget_network_settings.size.y, 0xAA000000);

            if (widget_network_settings != null)
                widget_network_settings.Render(graphics, mouseX, mouseY, partialTicks, this.font);
        }
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (display_network_settings && widget_network_settings != null) {
            if (widget_network_settings.KeyPressed(keyCode, scanCode, modifiers))
                return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (display_network_settings && widget_network_settings != null) {
            if (widget_network_settings.CharTyped(codePoint, modifiers)) return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (display_network_settings && widget_network_settings != null)
        {
            widget_network_settings.MouseClicked(mouseX, mouseY, button, generator);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
