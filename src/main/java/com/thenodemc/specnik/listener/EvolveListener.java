package com.thenodemc.specnik.listener;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.EvolveEvent;
import com.thenodemc.specnik.Specnik;
import com.thenodemc.specnik.Utils;
import com.thenodemc.specnik.config.SpecnikConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;

public class EvolveListener {

    public EvolveListener() {
        Pixelmon.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPostEvolveEvent(EvolveEvent.Post e) {
        SpecnikConfig config = Specnik.instance.getConfig();
        Player player = e.getPlayer();
        if (player == null) return;
        if (config.isDebug()) {
            player.sendSystemMessage(Component.literal("[Debug] EvolveEvent Triggered for " + player.getName().getString() + "'s " + e.getPokemon().getSpecies().getName()));
        }
        if (e.getPokemon() != null) {
            for (SpecnikConfig.NicknameSetting nicknameSetting : config.getForceNicknames().values()) {
                for (PokemonSpecification spec : nicknameSetting.getSpecsToMatch()) {
                    if (!spec.matches(e.getPokemon())) {
                        break;
                    }
                    if (config.isDebug()) {
                        player.sendSystemMessage(Component.literal("[Debug] §a✔ Pokemon matches specs: " + spec));
                    }

                    if (nicknameSetting.isUpdateOnEvolve()) {
                        String newName = Utils.parseLegacyToHex(config.replacePlaceholders(
                                nicknameSetting.getName(), e.getPokemon()
                        ).replaceAll("%nickname%", e.getPokemon().getSpecies().getTranslatedName().getString()));
                        e.getPokemon().setNickname(Component.literal(newName));
                        if (config.isNotifyModified()) {
                            String msg = config.getLangSettings().get("notify-modified-message")
                                    .replaceAll("%nickname%", e.getPokemon().getNickname().getString());
                            player.sendSystemMessage(Component.literal(msg));
                        }
                    }
                    return;
                }
            }
        } else {
            if (config.isDebug()) {
                player.sendSystemMessage(Component.literal("[Debug] EvolveEvent Pokemon entity was null - could not process update."));
            }
        }
    }
}
