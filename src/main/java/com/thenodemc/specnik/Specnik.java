package com.thenodemc.specnik;

import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.config.api.yaml.YamlConfigFactory;import com.pixelmonmod.pixelmon.api.events.init.PixelmonInitEvent;
import com.thenodemc.specnik.command.SpecnikCommand;
import com.thenodemc.specnik.config.SpecnikConfig;
import com.thenodemc.specnik.listener.EvolveListener;
import com.thenodemc.specnik.listener.NicknameListener;
import com.thenodemc.specnik.listener.PokemonReceivedListener;
import com.thenodemc.specnik.spec.UnnickableRequirement;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod("specnik")
public class Specnik {

    public static Specnik instance;
    private SpecnikConfig config;
    Logger logger = LogManager.getLogger("specnik");

    public Specnik() {
        Pixelmon.EVENT_BUS.register(this);
        Pixelmon.EVENT_BUS.register(new NicknameListener());
        Pixelmon.EVENT_BUS.register(new EvolveListener());
        Pixelmon.EVENT_BUS.register(new PokemonReceivedListener());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void setup(PixelmonInitEvent event) {
        PokemonSpecificationProxy.register(new UnnickableRequirement());
    }

    @SubscribeEvent
    public void registerCommand(RegisterCommandsEvent event) {
        new SpecnikCommand(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerAboutToStartEvent event) {
        instance = this;
        this.loadConfig();
        logger.info("Specnik has launched. Now handling nickname events.");
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(SpecnikConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SpecnikConfig getConfig() {
        return this.config;
    }

}
