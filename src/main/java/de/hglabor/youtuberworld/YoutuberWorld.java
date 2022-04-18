package de.hglabor.youtuberworld;

import de.hglabor.youtuberworld.config.HeadConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YoutuberWorld implements ModInitializer {
    private static final Logger LOGGER = LogManager.getLogger("youtuberworld");

    @Override
    public void onInitialize() {
        HeadConfig.onInitialize();
        try {
            HeadConfig.loadPngs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("Guten Moin!");
        ServerWorldEvents.LOAD.register((server, world) -> {
            LOGGER.info("Setze doFireTick auf False");
            server.getCommandManager().execute(server.getCommandSource(), "/gamerule doFireTick false");
        });
    }
}
