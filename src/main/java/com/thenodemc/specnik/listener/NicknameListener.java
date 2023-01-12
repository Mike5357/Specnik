package com.thenodemc.specnik.listener;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.pokemon.SetNicknameEvent;
import com.thenodemc.specnik.Specnik;
import com.thenodemc.specnik.config.SpecnikConfig;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NicknameListener {
    SpecnikConfig config = Specnik.instance.getConfig();

    public NicknameListener() {
        Pixelmon.EVENT_BUS.register(this);
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
