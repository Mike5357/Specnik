package com.thenodemc.specnik.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.thenodemc.specnik.Specnik;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;
import info.pixelmon.repack.org.spongepowered.objectmapping.meta.Comment;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public class SpecnikConfig {

    @Comment("Notify the player when their Pokémon's name has been modified.")
    private boolean notifyModified = true;

    @Comment("Notify the player when their Pokémon's name has been modified as soon as they receive it (if update-on-received is enabled)")
    private boolean notifyModifiedOnReceived = false;

    @Comment("Enable this if you have issues figuring out why the plugin is behaving in certain ways- it will print plenty of extra info to your chat.")
    private boolean debug = false;

    private List<String> nicknameBlacklist = Lists.newArrayList("(?i)badword", "supports_regex", "(?i).*(b[i1]a?tch(es)?).*");

    private Map<String, NicknameSetting> forceNicknames = ImmutableMap.of(
            "dont-modify",
            new NicknameSetting("", Lists.newArrayList("type:fire shiny"), false, false, false),
            "fire-pokemon",
            new NicknameSetting("§f[§6%type1% %type2%§f] §a%form% %species% §e(§f%nickname%§e)", Lists.newArrayList("type:fire"), true, true, false)
    );

    private List<PlaceholderReplacement> replacementList = ImmutableList.of(
            new PlaceholderReplacement("Fire", "Spicy"),
            new PlaceholderReplacement("Charmander", "Charry Boi"),
            new PlaceholderReplacement("pixelmon.palette.prestige", "Prestige")
    );

    private Map<String, String> langSettings = ImmutableMap.of(
            "editing-not-allowed-message", "§cYou cannot edit the nickname of this Pokémon.",
            "notify-modified-message", "§eYour Pokémon's name has been changed to: %nickname%",
            "nickname-blacklist-triggered", "§cThe name you entered (%nickname%) is not allowed.",
            "colors-not-allowed", "§cYou cannot use colors in Pokémon nicknames."
    );

    public SpecnikConfig() {
        super();
    }

    public boolean isNotifyModified() {
        return this.notifyModified;
    }

    public boolean isNotifyModifiedOnReceived() {
        return this.notifyModifiedOnReceived;
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

    public List<PlaceholderReplacement> getReplacementList() {
        return this.replacementList;
    }

    public Map<String,String> getLangSettings() {
        return this.langSettings;
    }

    @ConfigSerializable
    public static class NicknameSetting {

        private String name;
        private List<String> specsToMatch;
        private boolean playerEditingAllowed;
        private boolean updateOnEvolve;
        private boolean updateOnReceived;

        private transient List<PokemonSpecification> matchingSpecs = null;

//        public NicknameSetting() {
//        }

        public NicknameSetting(String name, List<String> specsToMatch, boolean playerEditingAllowed, boolean updateOnEvolve, boolean updateOnReceived) {
            this.name = name;
            this.specsToMatch = specsToMatch;
            this.playerEditingAllowed = playerEditingAllowed;
            this.updateOnEvolve = updateOnEvolve;
            this.updateOnReceived = updateOnReceived;
        }

        public String getName() {
            return name;
        }

        public List<PokemonSpecification> getSpecsToMatch() {
            if (this.matchingSpecs == null) {
                List<PokemonSpecification> matchingSpecs = Lists.newArrayList();
                for (String specString : this.specsToMatch) {
                    var parseAttempt = PokemonSpecificationProxy.create(specString);
                    if (!parseAttempt.wasSuccess()) {
                        if (parseAttempt.wasError()) {
                            Specnik.getLogger().error("Error parsing spec: " + parseAttempt.getError());
                        }

                        Specnik.getLogger().error("Invalid spec provided! " + parseAttempt.getException().getLocalizedMessage());
                    }

                    matchingSpecs.add(parseAttempt.get());
                }
                this.matchingSpecs = matchingSpecs;
            }

            return this.matchingSpecs;
        }

        public boolean isPlayerEditingAllowed() {
            return playerEditingAllowed;
        }

        public boolean isUpdateOnEvolve() {
            return updateOnEvolve;
        }

        public boolean isUpdateOnReceived() {
            return updateOnReceived;
        }
    }

    @ConfigSerializable
    public static class PlaceholderReplacement {

        private String find;
        private String replaceWith;

//        public PlaceholderReplacement() {
//        }

        public PlaceholderReplacement(String f, String r) {
            this.find = f;
            this.replaceWith = r;
        }

        public String getFindString() {
            return this.find;
        }

        public String getReplaceWithString() {
            return this.replaceWith;
        }
    }

    public String replacePlaceholders(String s, Pokemon p) {
        if (!p.getPalette().getTranslatedName().getString().equalsIgnoreCase("none")) {
            s = s.replaceAll("%palette%",p.getPalette().getTranslatedName().getString());
        } else s = s.replaceAll("%palette%(\\s)?","");
        if (!p.getForm().getLocalizedName().equalsIgnoreCase("none")) {
            s = s.replaceAll("%form%",p.getForm().getLocalizedName());
        } else s = s.replaceAll("%form%(\\s)?","");
        if (p.getSpecies().getDefaultForm().getTypes().get(0) != null) {
            s = s.replaceAll("%type1%",p.getSpecies().getDefaultForm().getTypes().getFirst().getLocalizedName());
        } else s = s.replaceAll("%type1%(\\s)?","");
        if (p.getSpecies().getDefaultForm().getTypes().size()>1) {
            s = s.replaceAll("%type2%",p.getSpecies().getDefaultForm().getTypes().get(1).getLocalizedName());
        } else s = s.replaceAll("(\\s)?%type2%(\\s)?","");
        s = s.replaceAll("%species%",p.getSpecies().getTranslatedName().getString());

        for(PlaceholderReplacement repl:this.getReplacementList()) {
            s = s.replaceAll(repl.getFindString(),repl.getReplaceWithString());
        }
        return s;
    }

}
