package com.thenodemc.specnik.spec;

import com.google.common.collect.Sets;
import com.pixelmonmod.api.parsing.ParseAttempt;
import com.pixelmonmod.api.pokemon.requirement.AbstractBooleanPokemonRequirement;
import com.pixelmonmod.api.requirement.Requirement;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;

import java.util.Set;

public class UnnickableRequirement extends AbstractBooleanPokemonRequirement {
    private static final Set<String> KEYS = Sets.newHashSet("unnickable");

    public UnnickableRequirement() {
        super(KEYS);
    }

    public UnnickableRequirement(boolean value) {
        super(KEYS, value);
    }

    public ParseAttempt<Requirement<Pokemon, PixelmonEntity, Boolean>> createInstance(Boolean value) {
        return ParseAttempt.success(new UnnickableRequirement(value));
    }

    public boolean isDataMatch(Pokemon pixelmon) {
        return (pixelmon.hasFlag("unnickable") == this.value || pixelmon.getPersistentData().contains("unnickable") == this.value);
    }

    public void applyData(Pokemon pixelmon) {
        if (this.value) {
            pixelmon.getPersistentData().putBoolean("unnickable", true); // Use tags instead of flags so they don't appear under the Pokemon's name
        } else {
            pixelmon.getPersistentData().remove("unnickable");
        }

    }
}
