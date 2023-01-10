package com.thenodemc.specnik;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.config.api.yaml.YamlConfigFactory;
import com.pixelmonmod.pixelmon.api.events.pokemon.SetNicknameEvent;

import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
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

    @SubscribeEvent
    public void onNicknameEvent(SetNicknameEvent e) {
        if (config.isDebug()) e.player.sendMessage(new StringTextComponent("[Debug] SetNicknameEvent Triggered! " + e.player.getName().getString() + "'s " + e.pokemon.getSpecies().getName() + " changed to: " + e.nickname), Util.NIL_UUID);

        if (!e.pokemon.hasFlag("unnickable")) {
            for (String s : config.getNicknameBlacklist()) {
                if (config.isDebug()) e.player.sendMessage(new StringTextComponent("Checking regex: " + s), Util.NIL_UUID);
                if (e.nickname.matches(s)) {
                    e.player.sendMessage(new StringTextComponent(config.getLangSettings().get("nickname-blacklist-triggered").replaceAll("%nickname%", e.nickname)), Util.NIL_UUID);
                    e.setCanceled(true);
                    return;
                }
            }

            for (SpecnikConfig.NicknameSetting nicknameSetting : config.getForceNicknames().values()) {
                for (PokemonSpecification spec : nicknameSetting.getSpecsToMatch()) {
                    if (!spec.matches(e.pokemon)) {
                        break;
                    }
                    if (config.isDebug())
                        e.player.sendMessage(new StringTextComponent("[Debug] §a✔ Pokemon matches specs: " + spec), Util.NIL_UUID);
                    if (nicknameSetting.isPlayerEditingAllowed()) {
                        e.nickname = config.replacePlaceholders(nicknameSetting.getName(), e.pokemon).replaceAll("%nickname%", e.nickname);
                        if (config.isNotifyModified())
                            e.player.sendMessage(new StringTextComponent(config.getLangSettings().get("notify-modified-message").replaceAll("%nickname%", e.nickname)), Util.NIL_UUID);
                    } else {
                        e.player.sendMessage(new StringTextComponent(config.getLangSettings().get("editing-not-allowed-message")), Util.NIL_UUID);
                        e.setCanceled(true);
                    }
                    return;
                }
            }
        } else {
            e.player.sendMessage(new StringTextComponent(config.getLangSettings().get("editing-not-allowed-message")), Util.NIL_UUID);
            e.setCanceled(true);
        }
    }

}
