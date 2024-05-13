# Specnik
A spec-based Pokémon nickname enforcer!

![A simplistic cyan-colored satellite icon with white text on its main body spelling out the word "Specnik".](https://i.imgur.com/AO3Lmwg.png)

# Summary
Specnik is a Forge sidemod for Pixelmon that allows you to easily configure how your players are able to nickname their Pokemon.

Specnik can:
- Prevent modifying nicknames of Pokemon via a new `unnickable` spec
- Prevent modifying nicknames of Pokemon that match any set of specs defined in the config
- Modify the players nickname input to match a specific format you define (re-formatting the players input)
    - Includes built-in placeholders for automatically populating Pokemon info (type, species, palette, form)
    - Also allows you to define whether the nickname should be forcefully applied whenever a pokemon with the defined specs is received and/or when it evolves)
- Blacklist words or phrases from being used in Pokemon nicknames with regex support
- Includes a text-replacement feature, allowing you to replace any string that appears in a nickname with something else. Can be used for example to add icons in place of Pokemon typing, or to resolve custom palette, form, or species names not appearing correctly.


# Dependencies
- Pixelmon Reforged (built for 9.1.0)

# Permissions
```yaml
specnik.colors # Allows the use of & color codes in player-edited nicknames to be parsed
```

# Configuration
Please [refer to the Wiki](https://github.com/Mike5357/Specnik/wiki) for information on how to configure this plugin, including examples and available placeholders.
