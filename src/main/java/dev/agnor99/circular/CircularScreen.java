package dev.agnor99.circular;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec2;

import java.util.List;

public class CircularScreen extends AbstractContainerScreen<InventoryMenu> {

    private static final Vec2i SIZE = new Vec2i(279,279);
    private static final Vec2i CENTER = new Vec2i(140,140);
    public static final ResourceLocation INVENTORY_LOCATION = new ResourceLocation("circular","textures/gui/inventory.png");

    public CircularScreen(InventoryMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        menu.slots.forEach(slot -> {
            if (slot.container == inventory) {
                if (List.of(5,6,7,8,45).contains(slot.index)) {
                    //ArmorSlots+Shield(45)
                    int relSlot = slot.index == 45 ? 4 : slot.index - 5;
                    slot.x = (int)(Math.sin(getAngleForSlot(relSlot, 5))*50)-8 + CENTER.x();
                    slot.y = (int)(-Math.cos(getAngleForSlot(relSlot, 5))*50)-8 + CENTER.y();
                } else {
                    //Inventory
                    int relSlot = slot.index%9;
                    int row;
                    if (slot.index >= 36) {
                        row = 0;
                    } else {
                        row = slot.index/9;
                    }
                    slot.x = (int)(Math.sin(getAngleForSlot(relSlot, 9))*(70 + 18*row))-8 + CENTER.x();
                    slot.y = (int)(-Math.cos(getAngleForSlot(relSlot, 9))*(70 + 18*row))-8 + CENTER.y();
                }
            } else {
                //Crafting
            }
        });
    }

    private static double getAngleForSlot(int relIndex, int slotsPerCircle) {
        return Math.toRadians(relIndex*(360f / slotsPerCircle));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void init() {
        if (minecraft.gameMode.hasInfiniteItems()) {
            minecraft.setScreen(new CreativeModeInventoryScreen(minecraft.player));
        } else {
            imageWidth = SIZE.x();
            imageHeight = SIZE.y();
            super.init();
        }
        titleLabelX = Integer.MIN_VALUE;
        titleLabelY = Integer.MIN_VALUE;
        inventoryLabelX = Integer.MIN_VALUE;
        inventoryLabelY = Integer.MIN_VALUE;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, INVENTORY_LOCATION);
        this.blit(poseStack, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        Vec2 normalMouseVec = new Vec2i(mouseX - CENTER.x() - leftPos, mouseY - CENTER.y() - topPos).toFloat().normalized();
        renderEntityInInventory(leftPos + CENTER.x(), topPos+CENTER.y(), 30, findClosestAngle(normalMouseVec), this.minecraft.player);
    }

    public static int findClosestAngle(Vec2 targetVec) {
        int angle = 0;
        float closesDistance = Float.MAX_VALUE;
        for (int i = 0; i < 360; i++) {
            float dist = targetVec.distanceToSqr(new Vec2((float)Math.sin(Math.toRadians(i)), (float)-Math.cos(Math.toRadians(i))));
            if (dist < closesDistance) {
                closesDistance = dist;
                angle = i;
            }
        }
        return angle;
    }

    public static void renderEntityInInventory(int posX, int posY, int scale, int angle, LivingEntity livingEntity) {
        PoseStack viewStack = RenderSystem.getModelViewStack();
        viewStack.pushPose();
        viewStack.translate(posX, posY, 1050.0D);
        viewStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.0D, 0.0D, 1000.0D);
        poseStack.scale((float)scale, (float)scale, (float)scale);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.ZP.rotationDegrees(angle);
        Quaternion quaternion2 = Vector3f.YP.rotationDegrees(180F);
        quaternion.mul(quaternion1);
        quaternion.mul(quaternion2);
        poseStack.mulPose(quaternion);
        poseStack.translate(0, -1, 0);
        float f2 = livingEntity.yBodyRot;
        float f3 = livingEntity.getYRot();
        float f4 = livingEntity.getXRot();
        float f5 = livingEntity.yHeadRotO;
        float f6 = livingEntity.yHeadRot;
        livingEntity.yBodyRot = 0;
        livingEntity.setYRot(0);
        livingEntity.setXRot(0);
        livingEntity.yHeadRot = livingEntity.getYRot();
        livingEntity.yHeadRotO = livingEntity.getYRot();
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        entityrenderdispatcher.overrideCameraOrientation(quaternion1);
        entityrenderdispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() ->
            entityrenderdispatcher.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, poseStack, source, 15728880)
        );
        source.endBatch();
        entityrenderdispatcher.setRenderShadow(true);
        livingEntity.yBodyRot = f2;
        livingEntity.setYRot(f3);
        livingEntity.setXRot(f4);
        livingEntity.yHeadRotO = f5;
        livingEntity.yHeadRot = f6;
        viewStack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }
}
