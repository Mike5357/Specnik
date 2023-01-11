package com.thenodemc.specnik;

import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.config.api.yaml.YamlConfigFactory;
import com.thenodemc.specnik.command.SpecnikCommand;
import com.thenodemc.specnik.config.SpecnikConfig;
import com.thenodemc.specnik.spec.UnnickableRequirement;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(FMLCommonSetupEvent event) {
        PokemonSpecificationProxy.register(new UnnickableRequirement());
    }

    @SubscribeEvent
    public void registerCommand(RegisterCommandsEvent event) {
        new SpecnikCommand(event.getDispatcher());
    }


    @SubscribeEvent
    public void onInit(FMLServerStartingEvent event) {
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
