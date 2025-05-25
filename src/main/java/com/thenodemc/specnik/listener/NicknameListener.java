package com.thenodemc.specnik.listener;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.pokemon.SetNicknameEvent;
import com.pixelmonmod.pixelmon.api.util.helpers.PlayerHelper;
import com.thenodemc.specnik.Specnik;
import com.thenodemc.specnik.Utils;
import com.thenodemc.specnik.config.SpecnikConfig;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NicknameListener {

    public NicknameListener() {
        Pixelmon.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onNicknameEvent(SetNicknameEvent e) {
        SpecnikConfig config = Specnik.instance.getConfig();

        if (config.isDebug()) {
            e.player.sendSystemMessage(Component.literal("[Debug] SetNicknameEvent Triggered! "
                    + e.player.getName().getString() + "'s " + e.pokemon.getSpecies().getName()
                    + " changed to: " + e.nickname));
        }

        if (!e.pokemon.getPersistentData().contains("unnickable")) {
            if (e.nickname.isEmpty()) {
                e.nickname = e.pokemon.getSpecies().getTranslatedName().getString();
            }
            for (String regex : config.getNicknameBlacklist()) {
                if (config.isDebug()) {
                    e.player.sendSystemMessage(Component.literal("Checking regex: " + regex));
                }
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(e.nickname);
                if (matcher.find()) {
                    e.player.sendSystemMessage(Component.literal(
                            config.getLangSettings().get("nickname-blacklist-triggered")
                                    .replaceAll("%nickname%", e.nickname)
                    ));
                    e.setCanceled(true);
                    return;
                }
            }

            for (SpecnikConfig.NicknameSetting nicknameSetting : config.getForceNicknames().values()) {
                for (PokemonSpecification spec : nicknameSetting.getSpecsToMatch()) {
                    if (!spec.matches(e.pokemon)) {
                        break;
                    }
                    if (config.isDebug()) {
                        e.player.sendSystemMessage(Component.literal("[Debug] §a✔ Pokemon matches specs: " + spec));
                    }

                    if (nicknameSetting.isPlayerEditingAllowed()) {
                        if (e.nickname.contains("&") && !PlayerHelper.hasPermission(e.player, "specnik.colors")) {
                            e.player.sendSystemMessage(Component.literal(config.getLangSettings().get("colors-not-allowed")));
                            e.setCanceled(true);
                            return;
                        }
                        e.nickname = config.replacePlaceholders(nicknameSetting.getName(), e.pokemon)
                                .replaceAll("%nickname%", e.nickname);

                        if (config.isNotifyModified()) {
                            e.player.sendSystemMessage(Component.literal(
                                    config.getLangSettings().get("notify-modified-message")
                                            .replaceAll("%nickname%", e.nickname)
                            ));
                        }
                        e.nickname = Utils.parseLegacyToHex(e.nickname);
                    } else {
                        e.player.sendSystemMessage(Component.literal(config.getLangSettings().get("editing-not-allowed-message")));
                        e.setCanceled(true);
                    }
                    return;
                }
            }
        } else {
            e.player.sendSystemMessage(Component.literal(config.getLangSettings().get("editing-not-allowed-message")));
            e.setCanceled(true);
        }
    }
}
