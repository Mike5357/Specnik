package com.thenodemc.specnik.listener;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.EvolveEvent;
import com.pixelmonmod.pixelmon.api.events.PokemonReceivedEvent;
import com.thenodemc.specnik.Specnik;
import com.thenodemc.specnik.config.SpecnikConfig;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PokemonReceivedListener {

    SpecnikConfig config;

    public PokemonReceivedListener() {
        Pixelmon.EVENT_BUS.register(this);
        config = Specnik.instance.getConfig();
    }

    @SubscribeEvent
    public void onPokemonReceivedEvent(PokemonReceivedEvent e) {
        config = Specnik.instance.getConfig();
        if (config.isDebug()) e.getPlayer().sendMessage(new StringTextComponent("[Debug] EvolveEvent Triggered for " + e.getPlayer().getName().getString() + "'s " + e.getPokemon().getSpecies().getName()), Util.NIL_UUID);

        for (SpecnikConfig.NicknameSetting nicknameSetting : config.getForceNicknames().values()) {
            for (PokemonSpecification spec : nicknameSetting.getSpecsToMatch()) {
                if (!spec.matches(e.getPokemon())) {
                    break;
                }
                if (config.isDebug())
                    e.getPlayer().sendMessage(new StringTextComponent("[Debug] §a✔ Pokemon matches specs: " + spec), Util.NIL_UUID);
                if (nicknameSetting.isUpdateOnReceived()) {
                    e.getPokemon().setNickname(new StringTextComponent(config.replacePlaceholders(nicknameSetting.getName(), e.getPokemon()).replaceAll("%nickname%", "")));
                    if (config.isNotifyModifiedOnReceived())
                        e.getPlayer().sendMessage(new StringTextComponent(config.getLangSettings().get("notify-modified-message").replaceAll("%nickname%", e.getPokemon().getFormattedNickname().getString())), Util.NIL_UUID);
                }
                return;
            }
        }
    }
}
