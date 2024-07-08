package org.example.smart.smartcitytemp;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;



import org.example.smart.smartcitytemp.Smartcitytem;

@Mod.EventBusSubscriber(modid = Smartcitytem.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WeatherOverlayRenderer {


    @SubscribeEvent
    public void onClientChat(ClientChatEvent event) {
        // Example: Send temperature information to chat
        String temperatureText = "Temperature: " + getTemperatureString();
        Minecraft.getInstance().player.displayClientMessage(Component.translatable(temperatureText), true);
    }

    private static String getTemperatureString() {
        // Implement logic to fetch current temperature or use stored value
        return "25°C"; // Example temperature string
    }
}