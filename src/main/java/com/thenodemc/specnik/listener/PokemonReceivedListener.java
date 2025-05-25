package com.thenodemc.specnik.listener;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.PokemonReceivedEvent;
import com.thenodemc.specnik.Specnik;
import com.thenodemc.specnik.Utils;
import com.thenodemc.specnik.config.SpecnikConfig;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;

public class PokemonReceivedListener {

    public PokemonReceivedListener() {
        Pixelmon.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPokemonReceivedEvent(PokemonReceivedEvent e) {
        SpecnikConfig config = Specnik.instance.getConfig();

        if (config.isDebug()) {
            e.getPlayer().sendSystemMessage(Component.literal(
                    "[Debug] PokemonReceivedEvent Triggered for "
                            + e.getPlayer().getName().getString()
                            + "'s " + e.getPokemon().getSpecies().getName()));
        }
        for (SpecnikConfig.NicknameSetting nicknameSetting : config.getForceNicknames().values()) {
            for (PokemonSpecification spec : nicknameSetting.getSpecsToMatch()) {
                if (!spec.matches(e.getPokemon())) {
                    break;
                }
                if (config.isDebug()) {
                    e.getPlayer().sendSystemMessage(Component.literal(
                            "[Debug] §a✔ Pokemon matches specs: " + spec));
                }
                if (nicknameSetting.isUpdateOnReceived()) {
                    e.getPokemon().setNickname(Component.literal(Utils.parseLegacyToHex(
                            config.replacePlaceholders(nicknameSetting.getName(), e.getPokemon())
                                    .replaceAll("%nickname%", e.getPokemon().getSpecies().getTranslatedName().getString())
                    )));
                    if (config.isNotifyModifiedOnReceived()) {
                        e.getPlayer().sendSystemMessage(Component.literal(
                                config.getLangSettings().get("notify-modified-message")
                                        .replaceAll("%nickname%", e.getPokemon().getNickname().getString())
                        ));
                    }
                }
                return;
            }
        }
    }
}
