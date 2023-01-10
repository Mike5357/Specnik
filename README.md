# Specnik
A spec-based Pokémon nickname enforcer!

# Summary
Specnik is a Forge sidemod for Pixelmon that allows you to easily configure how your players are able to nickname their Pokemon.

Specnik can:
- Prevent modifying nicknames of Pokemon via a new `unnickable` spec, or any Pokemon that match any set of specs defined in the config
- Modify the players nickname input to match a specific format you define (re-formatting the players input)
    - Includes built-in placeholders for automatically populating Pokemon info (type, species, palette, form)
- Blacklist words or phrases from being used in Pokemon nicknames with regex support


#Dependencies
- Pixelmon Reforged (built for 9.1.0)

# Permissions
There are no permissions associated with this plugin.

# Default Config Example (with comments)
```yaml
notify-modified: true # Notify the player when their pokemons name has been modified.
debug: false # Send a message to the pokemons owner any time one of the above events is fired.
# Nickname Blacklist uses regex to filter out words or phrases.
# If you don't know how to use regex, you can just include simple strings in this list.
# Regex tip: If you want a string to be case-insensitive (ignore caps), simply add (?i) before the word.
nickname-blacklist: 
  - (?i)badword
  - supports_regex
  - (?i).*(b[i1]a?tch(es)?).* 
force-nicknames:
  # Important note: This list is prioritized from the top down. If more than one setting can match a pokemon, it will only consider the first match in this list so the order you enter things here does matter if you have any kind of overlapping.
  # ie. the "type:fire shiny" example needs to be before the "type:fire" setting, or else the latter would be the only setting to apply to both.
  # The following example prevents editing the nickname of any shiny fire-type pokemon.
  dont-modify:
    name: ''
    specs-to-match:
      - type:fire shiny # To match a combination of specs, they should be listed on one line.
    player-editing-allowed: false
  # The following example will add the pokemons type(s) next to its name if it is a fire-type Pokemon.
  # If the player tries to edit the nickname it will be allowed, but reformatted to match '%nickname%', including their custom nick.
  # Example: "[Fire Flying] Charizard (Big Guy)"
  fire-pokemon: 
    name: §f[§6%type1% %type2%§f] §a%form% %species% §e(§f%nickname%§e) # Section signs ("§") allow you to use color codes where you would normally put "&" in-game
    specs-to-match:
      - type:fire
    player-editing-allowed: true
  # The following example will simply update the name of the pokemon to "Christmas <species>" if the player tries to edit it or click "Reset", regardless of what they type.
  # Example: "Christmas Abomasnow"
  christmas-pokemon:
    name: Christmas %species%
    specs-to-match:
      - ribbon:christmas
    player-editing-allowed: true
lang-settings:
  editing-not-allowed-message: §cYou cannot edit the nickname of this Pokemon.
  notify-modified-message: '§eYour Pokemon''s name has been changed to: %nickname%'
  nickname-blacklist-triggered: §cThe name you entered (%nickname%) is not allowed.
```

# Supported Placeholders
The following placeholders will work in any `name` under the `force-nicknames` section of the config:

| Placeholder  | Description |
| ----- | ----- |
|`%palette%`|Replaced with the Pokemon's localized Palette name|
|`%form%`|Replaced with the Pokemon's localized Form name|
|`%species%`|Replaced with the Pokemon's localized Species name|
|`%type1%`|Replaced with the Pokemon's localized Main Type|
|`%type2%`|Replaced with the Pokemon's localized Secondary Type|
|`%nickname%`|Replaced with what the player has typed into the Pixelmon renaming UI|

The following placeholders will work apply only to lang-settings:

| Placeholder  | Description |
| ----- | ----- |
|`%nickname%`|Replaced with the Pokemons current nickname.|