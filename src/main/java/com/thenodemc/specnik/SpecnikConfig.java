package com.thenodemc.specnik;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;
import info.pixelmon.repack.org.spongepowered.objectmapping.meta.Comment;

import java.util.List;
import java.util.Map;

@ConfigSerializable
@ConfigPath("config/Specnik/config.yml")
public class SpecnikConfig extends AbstractYamlConfig {

    private boolean notifyModified = true; @Comment("Notify the player when their pokemons name has been modified.")
    private boolean debug = false; @Comment("Enable this if you have issues figuring out why the plugin is behaving in certain ways- it will print plenty of extra info to your chat.")

    private List<String> nicknameBlacklist = Lists.newArrayList("(?i)badword","supports_regex","(?i).*(b[i1]a?tch(es)?).*");

    private Map<String, NicknameSetting> forceNicknames = ImmutableMap.of(
        "dont-modify",
            new NicknameSetting("", Lists.newArrayList("type:fire shiny"),
                    false),
        "fire-pokemon",
            new NicknameSetting("§f[§6%type1% %type2%§f] §a%form% %species% §e(§f%nickname%§e)", Lists.newArrayList("type:fire"),
            true),
        "christmas-pokemon",
            new NicknameSetting("Christmas %species%", Lists.newArrayList("ribbon:christmas"),
            true)
    );

    private Map<String,String> langSettings = ImmutableMap.of(
        "editing-not-allowed-message","§cYou cannot edit the nickname of this Pokemon.",
        "notify-modified-message","§eYour Pokemon's name has been changed to: %nickname%",
        "nickname-blacklist-triggered","§cThe name you entered (%nickname%) is not allowed."
    );

    public SpecnikConfig() {
        super();
    }

    public boolean isNotifyModified() {
        return this.notifyModified;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public List<String> getNicknameBlacklist() {
        return this.nicknameBlacklist;
    }

    public Map<String,NicknameSetting> getForceNicknames() {
        return this.forceNicknames;
    }

    public Map<String,String> getLangSettings() {
        return this.langSettings;
    }

    @ConfigSerializable
    public static class NicknameSetting {

        private String name;
        private List<String> specsToMatch;
        private boolean playerEditingAllowed;

        private transient List<PokemonSpecification> matchingSpecs = null;

        public NicknameSetting() {
        }

        public NicknameSetting(String name, List<String> specsToMatch, boolean playerEditingAllowed) {
            this.name = name;
            this.specsToMatch = specsToMatch;
            this.playerEditingAllowed = playerEditingAllowed;
        }

        public String getName() {
            return name;
        }

        public List<PokemonSpecification> getSpecsToMatch() {
            if (this.matchingSpecs == null) {
                List<PokemonSpecification> matchingSpecs = Lists.newArrayList();
                for (String specString : this.specsToMatch) {
                    matchingSpecs.add(PokemonSpecificationProxy.create(specString));
                }
                this.matchingSpecs = matchingSpecs;
            }

            return this.matchingSpecs;
        }

        public boolean isPlayerEditingAllowed() {
            return playerEditingAllowed;
        }

    }

    public String replacePlaceholders(String s, Pokemon p) {
        if (!p.getPalette().getLocalizedName().equalsIgnoreCase("none")) {
            s = s.replaceAll("%palette%",p.getPalette().getLocalizedName());
        } else s = s.replaceAll("%palette%(\\s)?","");
        if (!p.getForm().getLocalizedName().equalsIgnoreCase("none")) {
            s = s.replaceAll("%form%",p.getForm().getLocalizedName());
        } else s = s.replaceAll("%form%(\\s)?","");
        if (p.getSpecies().getDefaultForm().getTypes().get(0) != null) {
            s = s.replaceAll("%type1%",p.getSpecies().getDefaultForm().getTypes().get(0).getLocalizedName());
        } else s = s.replaceAll("%type1%(\\s)?","");
        if (p.getSpecies().getDefaultForm().getTypes().size()>1) {
            s = s.replaceAll("%type2%",p.getSpecies().getDefaultForm().getTypes().get(1).getLocalizedName());
        } else s = s.replaceAll("(\\s)?%type2%(\\s)?","");
        return s.replaceAll("%species%",p.getSpecies().getLocalizedName());
    }

}
