package com.thenodemc.specnik.spec;

import com.google.common.collect.Sets;
import com.pixelmonmod.api.pokemon.requirement.AbstractBooleanPokemonRequirement;
import com.pixelmonmod.api.requirement.Requirement;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;

import java.util.Set;

public class UnnickableRequirement extends AbstractBooleanPokemonRequirement {
    private static final Set<String> KEYS = Sets.newHashSet(new String[]{"unnickable"});

    public UnnickableRequirement() {
        super(KEYS);
    }

    public UnnickableRequirement(boolean value) {
        super(KEYS, value);
    }

    public Requirement<Pokemon, PixelmonEntity, Boolean> createInstance(Boolean value) {
        return new UnnickableRequirement(value);
    }

    public boolean isDataMatch(Pokemon pixelmon) {
        return pixelmon.hasFlag("unnickable") == this.value;
    }

    public void applyData(Pokemon pixelmon) {
        if (this.value) {
            pixelmon.addFlag("unnickable");
        } else {
            pixelmon.removeFlag("unnickable");
        }

    }
}
