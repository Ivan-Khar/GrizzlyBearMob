package com.aqupd.brownbear;

import com.aqupd.brownbear.client.renderer.BrownBearEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class MainClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.INSTANCE.register(Main.BROWNBEAR, (dispatcher, context) -> new BrownBearEntityRenderer(dispatcher));
    }
}
