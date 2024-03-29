package com.thenodemc.specnik.listener;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.pokemon.SetNicknameEvent;
import com.pixelmonmod.pixelmon.api.util.helpers.PlayerHelper;
import com.thenodemc.specnik.Specnik;
import com.thenodemc.specnik.Utils;
import com.thenodemc.specnik.config.SpecnikConfig;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NicknameListener {

    public NicknameListener() {
        Pixelmon.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onNicknameEvent(SetNicknameEvent e) {
        SpecnikConfig config = Specnik.instance.getConfig();
        if (config.isDebug()) e.player.sendMessage(new StringTextComponent("[Debug] SetNicknameEvent Triggered! " + e.player.getName().getString() + "'s " + e.pokemon.getSpecies().getName() + " changed to: " + e.nickname), Util.NIL_UUID);

        if (!e.pokemon.hasFlag("unnickable")) {
            if (e.nickname.equals("")) e.nickname = e.pokemon.getSpecies().getLocalizedName();
            for (String s : config.getNicknameBlacklist()) {
                if (config.isDebug()) e.player.sendMessage(new StringTextComponent("Checking regex: " + s), Util.NIL_UUID);
                Pattern pattern = Pattern.compile(s);
                Matcher matcher = pattern.matcher(e.nickname);
                if (matcher.find()) {
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
                        if (e.nickname.contains("&") && !PlayerHelper.hasPermission(e.player,"specnik.colors")) {
                            e.player.sendMessage(new StringTextComponent(config.getLangSettings().get("colors-not-allowed")), Util.NIL_UUID);
                            e.setCanceled(true);
                            return;
                        }
                        e.nickname = config.replacePlaceholders(nicknameSetting.getName(), e.pokemon).replaceAll("%nickname%", e.nickname);
                        if (config.isNotifyModified())
                            e.player.sendMessage(new StringTextComponent(config.getLangSettings().get("notify-modified-message").replaceAll("%nickname%", e.nickname)), Util.NIL_UUID);
                        e.nickname = Utils.parseLegacyToHex(e.nickname);
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
