package com.thenodemc.specnik;

import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.init.PixelmonInitEvent;
import com.thenodemc.specnik.command.SpecnikCommand;
import com.thenodemc.specnik.config.SpecnikConfigManager;
import com.thenodemc.specnik.config.SpecnikConfig;
import com.thenodemc.specnik.listener.EvolveListener;
import com.thenodemc.specnik.listener.NicknameListener;
import com.thenodemc.specnik.listener.PokemonReceivedListener;
import com.thenodemc.specnik.spec.UnnickableRequirement;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod("specnik")
public class Specnik {
    public static Specnik instance;
    private SpecnikConfig config;
    private static final Logger LOGGER = LogUtils.getLogger();

    public Specnik() {
        Pixelmon.EVENT_BUS.register(this);
        Pixelmon.EVENT_BUS.register(new NicknameListener());
        Pixelmon.EVENT_BUS.register(new EvolveListener());
        Pixelmon.EVENT_BUS.register(new PokemonReceivedListener());
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void setup(PixelmonInitEvent event) {
        PokemonSpecificationProxy.register(new UnnickableRequirement());
    }

    @SubscribeEvent
    public void registerCommand(RegisterCommandsEvent event) {
        SpecnikCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        instance = this;
        this.loadConfig();
        LOGGER.info("Specnik has launched. Now handling nickname events.");
    }

    public void loadConfig() {
        this.config = SpecnikConfigManager.load();
    }

    public SpecnikConfig getConfig() {
        return this.config;
    }

    public static Specnik getInstance() {
        return instance;
    }

    public static Logger getLogger() { return LOGGER; }

}
