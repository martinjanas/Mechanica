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

public class GeneratorScreen extends Screen
{
    private final BlockPos pos;
    private final Level level;

    public GeneratorScreen(BlockEntityGenerator generator)
    {
        super(Component.literal("Generator"));
        this.pos = generator.getBlockPos();
        this.level = generator.getLevel();
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
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        // Close on ESC
        if (keyCode == 256)
        {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
