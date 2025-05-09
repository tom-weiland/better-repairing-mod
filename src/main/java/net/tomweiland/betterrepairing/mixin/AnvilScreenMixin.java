package net.tomweiland.betterrepairing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin extends ForgingScreen<AnvilScreenHandler> {

    @Shadow
    private PlayerEntity player;

    public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
        super(handler, playerInventory, title, texture);
    }

    @Inject(method = "drawForeground", at = @At("HEAD"), cancellable = true)
    private void onDrawForeground(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        super.drawForeground(context, mouseX, mouseY);
		int i = this.handler.getLevelCost();
		if (i > 0) {
			int j = 8453920;
			Text text;
            // Removed TOO EXPENSIVE check, everything else is the same as the original method
			if (!this.handler.getSlot(2).hasStack()) {
				text = null;
			} else {
				text = Text.translatable("container.repair.cost", i);
				if (!this.handler.getSlot(2).canTakeItems(this.player)) {
					j = 16736352;
				}
			}

			if (text != null) {
				int k = this.backgroundWidth - 8 - this.textRenderer.getWidth(text) - 2;
				//int l = 69; // Why does this variable exist? It isn't used anywhere lol
				context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
				context.drawTextWithShadow(this.textRenderer, text, k, 69, j);
			}
		}

        ci.cancel();
    }

    @Shadow
    protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) { }
}
