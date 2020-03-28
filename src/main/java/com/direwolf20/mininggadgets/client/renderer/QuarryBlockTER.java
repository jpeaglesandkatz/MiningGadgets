package com.direwolf20.mininggadgets.client.renderer;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.ModItems;
import com.direwolf20.mininggadgets.common.tiles.QuarryBlockTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class QuarryBlockTER extends TileEntityRenderer<QuarryBlockTileEntity> {

    private final static ResourceLocation laserBeam = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/misc/laser.png");
    private final static ResourceLocation laserBeam2 = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/misc/laser2.png");
    private final static ResourceLocation laserBeamGlow = new ResourceLocation(MiningGadgets.MOD_ID + ":textures/misc/laser_glow.png");

    public QuarryBlockTER(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    private void renderModelBrightnessColorQuads(MatrixStack.Entry matrixEntry, IVertexBuilder builder, float red, float green, float blue, float alpha, List<BakedQuad> listQuads, int combinedLightsIn, int combinedOverlayIn) {
        for (BakedQuad bakedquad : listQuads) {
            float f;
            float f1;
            float f2;

            if (bakedquad.hasTintIndex()) {
                f = red * 1f;
                f1 = green * 1f;
                f2 = blue * 1f;
            } else {
                f = 1f;
                f1 = 1f;
                f2 = 1f;
            }

            builder.addVertexData(matrixEntry, bakedquad, f, f1, f2, alpha, combinedLightsIn, combinedOverlayIn);
        }
    }

    @Override
    public void render(QuarryBlockTileEntity tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightsIn, int combinedOverlayIn) {
        if (tile.getMarkerX().equals(BlockPos.ZERO) || tile.getMarkerZ().equals(BlockPos.ZERO)) return;
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder builder = buffer.getBuffer(MyRenderType.OVERLAY_LINES);

        matrixStackIn.push();
        matrixStackIn.translate(.5, .5, .5);
        matrixStackIn.translate(-tile.getPos().getX(), -tile.getPos().getY(), -tile.getPos().getZ());
        Matrix4f positionMatrix = matrixStackIn.getLast().getMatrix();
        BlockPos markerX = tile.getMarkerX();
        BlockPos markerZ = tile.getMarkerZ();
        BlockPos corner = new BlockPos(markerX.getX(), markerX.getY(), markerZ.getZ());
        drawLasers(builder, positionMatrix, tile.getPos(), markerX, 1f, 0f, 0f, 1f);
        drawLasers(builder, positionMatrix, tile.getPos(), markerZ, 1f, 0f, 0f, 1f);
        drawLasers(builder, positionMatrix, markerX, corner, 1f, 0f, 0f, 1f);
        drawLasers(builder, positionMatrix, markerZ, corner, 1f, 0f, 0f, 1f);

        matrixStackIn.pop();
        if (tile.getCurrentPos() == BlockPos.ZERO || tile.getCurrentPos() == null) {
            return;
        }

        int diffX = tile.getCurrentPos().getX() - tile.getPos().getX();
        int diffY = tile.getCurrentPos().getY() - tile.getPos().getY();
        int diffZ = tile.getCurrentPos().getZ() - tile.getPos().getZ();

        matrixStackIn.push();

        matrixStackIn.translate(0.5, 3.5, 0.5);
        matrixStackIn.translate(diffX, 0, diffZ);
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0F));
        matrixStackIn.scale(1.5f, 1.5f, 1.5f);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = new ItemStack(ModItems.MININGGADGET.get());
        IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(stack, tile.getWorld(), null);
        itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED, true, matrixStackIn, buffer, LightTexture.packLight(15, 15), combinedOverlayIn, ibakedmodel);

        matrixStackIn.pop();

        IVertexBuilder builder2 = buffer.getBuffer(MyRenderType.LASER_MAIN_BEAM);
        matrixStackIn.push();
        matrixStackIn.translate(0.6, 3.5, 0.45);
        matrixStackIn.translate(diffX, 0, diffZ);

        //matrixStackIn.translate(-tile.getPos().getX(), -tile.getPos().getY(), -tile.getPos().getZ());
        Matrix4f positionMatrix2 = matrixStackIn.getLast().getMatrix();
        long gameTime = tile.getWorld().getGameTime();
        double v = gameTime;
        MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
        Matrix3f matrixNormal = matrixstack$entry.getNormal();
        drawMiningLaser(builder2, positionMatrix2, matrixNormal, BlockPos.ZERO.down(1), new BlockPos(0, diffY - 2.5, 0), 1f, 0f, 0f, 0.05f, v, v + 2 * 1.5);
        //drawLasers(builder2, positionMatrix2, BlockPos.ZERO.down(1), new BlockPos(0,-3,0), 1f, 0f, 0f, 1f);
        //drawLasers(builder, positionMatrix, markerX, corner, 1f, 0f, 0f, 1f);
        //drawLasers(builder, positionMatrix, markerZ, corner, 1f, 0f, 0f, 1f);

        matrixStackIn.pop();
        buffer.finish(MyRenderType.OVERLAY_LINES);
    }

    private static void drawLasers(IVertexBuilder builder, Matrix4f positionMatrix, BlockPos from, BlockPos to, float r, float g, float b, float thickness) {
        builder.pos(positionMatrix, from.getX(), from.getY(), from.getZ())
                .color(r, g, b, 1.0f)
                .endVertex();
        builder.pos(positionMatrix, to.getX(), to.getY(), to.getZ())
                .color(r, g, b, 1.0f)
                .endVertex();
    }

    private static void drawMiningLaser(IVertexBuilder builder, Matrix4f positionMatrix, Matrix3f matrixNormalIn, BlockPos from, BlockPos to, float r, float g, float b, float thickness, double v1, double v2) {
        builder.pos(positionMatrix, from.getX() - thickness, from.getY(), from.getZ() - thickness)
                .color(r, g, b, 1.0f)
                .tex(0, (float) v1)
                .overlay(OverlayTexture.NO_OVERLAY)
                .lightmap(15728880)
                .endVertex();
        builder.pos(positionMatrix, (float) to.getX() - thickness, (float) to.getY(), (float) to.getZ() - thickness)
                .color(r, g, b, 1.0f)
                .tex(0, (float) v2)
                .overlay(OverlayTexture.NO_OVERLAY)
                .lightmap(15728880)
                .endVertex();
        builder.pos(positionMatrix, (float) to.getX() + thickness, (float) to.getY(), (float) to.getZ() + thickness)
                .color(r, g, b, 1.0f)
                .tex(1, (float) v2)
                .overlay(OverlayTexture.NO_OVERLAY)
                .lightmap(15728880)
                .endVertex();
        builder.pos(positionMatrix, from.getX() + thickness, from.getY(), from.getZ() + thickness)
                .color(r, g, b, 1.0f)
                .tex(1, (float) v1)
                .overlay(OverlayTexture.NO_OVERLAY)
                .lightmap(15728880)
                .endVertex();
        /*Vector3f vector3f = new Vector3f(0.0f, 1.0f, 0.0f);
        vector3f.transform(matrixNormalIn);
        Vector4f vec1 = new Vector4f(-thickness, -1, -thickness, 1.0F);
        vec1.transform(positionMatrix);
        Vector4f vec2 = new Vector4f((float) -thickness, (float) -4.5, (float) -thickness, 1.0F);
        vec2.transform(positionMatrix);
        Vector4f vec3 = new Vector4f((float) thickness, (float) -4.5, (float) thickness, 1.0F);
        vec3.transform(positionMatrix);
        Vector4f vec4 = new Vector4f(thickness, -1, thickness, 1.0F);
        vec4.transform(positionMatrix);
        builder.addVertex(vec1.getX(), vec4.getY(), vec4.getZ(), r, g, b, 1f, 0, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.getX(), vector3f.getY(), vector3f.getZ());
        builder.addVertex(vec2.getX(), vec3.getY(), vec3.getZ(), r, g, b, 1f, 0, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.getX(), vector3f.getY(), vector3f.getZ());
        builder.addVertex(vec3.getX(), vec2.getY(), vec2.getZ(), r, g, b, 1f, 1, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.getX(), vector3f.getY(), vector3f.getZ());
        builder.addVertex(vec4.getX(), vec1.getY(), vec1.getZ(), r, g, b, 1f, 1, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.getX(), vector3f.getY(), vector3f.getZ());
        builder.addVertex(vec1.getX(), vec4.getY(), vec4.getZ(), r, g, b, 1f, 0, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.getX(), vector3f.getY(), vector3f.getZ());
        builder.addVertex(vec2.getX(), vec3.getY(), vec3.getZ(), r, g, b, 1f, 0, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.getX(), vector3f.getY(), vector3f.getZ());
        builder.addVertex(vec3.getX(), vec2.getY(), vec2.getZ(), r, g, b, 1f, 1, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.getX(), vector3f.getY(), vector3f.getZ());
        builder.addVertex(vec4.getX(), vec1.getY(), vec1.getZ(), r, g, b, 1f, 1, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.getX(), vector3f.getY(), vector3f.getZ());
*/
    }

    @Override
    public boolean isGlobalRenderer(QuarryBlockTileEntity te) {
        return true;
    }
}
