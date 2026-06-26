# Dominion - Fabric Edition

A territory anti-grief mod for Minecraft Fabric servers, ported from the original Bukkit/Paper plugin.

> **⚠️ This is a community port.** For the official Bukkit/Paper version, visit the [original repository](https://github.com/LunaDeerMC/Dominion).

## Original Project

This is a **Fabric port** of the [Dominion](https://github.com/LunaDeerMC/Dominion) plugin originally developed by [LunaDeerMC](https://github.com/LunaDeerMC).

- **Original Author:** [LunaDeerMC](https://github.com/LunaDeerMC) / [zhangyuheng](https://github.com/ColdeZhang)
- **Original Repository:** https://github.com/LunaDeerMC/Dominion
- **Original License:** GPL-3.0
- **Documentation:** https://dominion.lunadeer.cn/

## Target Platform

- **Minecraft Version:** 26.1.2
- **Mod Loader:** Fabric (Loom 1.17)
- **Java Version:** 25+
- **Fabric API:** 0.153.0+26.1.2

## Features

- Territory/land protection system with 30+ configurable flags
- Multi-language support (English, Chinese, Japanese, Russian)
- Database support (SQLite, PostgreSQL, MySQL, MariaDB)
- Permission system with groups and templates
- Border display and visualization
- Text UI (TUI) for commands
- Comprehensive event protection (80+ protection types)

## Building

```bash
# Requires Java 25+
export JAVA_HOME=/path/to/java-25

# Build the mod JAR
./gradlew build

# Output: build/libs/Dominion-1.0.0.jar
```

## Installation

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 26.1.2
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) (required dependency)
3. Download the mod JAR from releases
4. Place the JAR in your server's `mods/` directory
5. Start the server - configuration files will be generated in `config/dominion/`

## Configuration

Configuration files are located in `config/dominion/`:
- `config.yml` - Main configuration (database, language, limits)
- `flags.yml` - Flag display names and defaults
- `languages/` - Language files
- `limitations/` - Per-group limitation files
- `world-wide/` - Per-world default settings

## Commands

All commands are under `/dominion`:

| Command | Description |
|---------|-------------|
| `/dominion create <name>` | Create a new dominion at your position |
| `/dominion auto_create <name>` | Auto-create with default radius |
| `/dominion delete <name> [force]` | Delete a dominion |
| `/dominion rename <name> <new>` | Rename a dominion |
| `/dominion info` | Show info about current dominion |
| `/dominion menu` | Open the main menu |
| `/dominion set_msg <name> <enter\|leave> <msg>` | Set enter/leave messages |
| `/dominion set_tp <name>` | Set teleport location |
| `/dominion set_map_color <name> <color>` | Set map display color |
| `/dominion give <name> <player> [force]` | Transfer ownership |
| `/dominion tp <name>` | Teleport to dominion |
| `/dominion expand <size>` | Expand dominion |
| `/dominion contract <size>` | Contract dominion |
| `/dominion set_env <name> <flag> <value>` | Set environment flag |
| `/dominion set_guest <name> <flag> <value>` | Set guest permission flag |
| `/dominion member_add <name> <player>` | Add a member |
| `/dominion member_remove <name> <player>` | Remove a member |
| `/dominion group_create <name> <group>` | Create a permission group |
| `/dominion group_delete <name> <group>` | Delete a permission group |
| `/dominion reload` | Reload configuration (admin) |

## Permissions

- `dominion.default` - Basic dominion usage (any player)
- `dominion.admin` - Administrative commands (OP level 4)

## Territory Protection

The mod protects territories from unauthorized actions. Protection includes:

**Block Interactions:** Chests, Barrels, Shulker Boxes, Anvils, Beacons, Beds, Brewing Stands, Crafting Tables, Enchanting Tables, Lecterns, Jukeboxes, Furnaces, Hoppers, Dispensers, Droppers, Buttons, Doors, Levers, Pressure Plates, Note Blocks, Repeaters, Comparators, and more.

**Entity Interactions:** Item Frames, Armor Stands, Villager Trading, Animal Feeding/Shearing/Dyeing, Lead Attachment, Riding, Honey Collection.

**Combat:** PVP, Animal/Monster/Villager killing.

## Known Limitations

- Chest UI (CUI) is not yet fully implemented - Text UI (TUI) is used instead
- PlaceholderAPI integration pending
- Economy integration (Vault equivalent) pending
- Residence migration tool pending
- Multi-server support pending

## Credits

- **Original Plugin:** [Dominion](https://github.com/LunaDeerMC/Dominion) by [LunaDeerMC](https://github.com/LunaDeerMC)
- **Original Author:** [zhangyuheng](https://github.com/ColdeZhang)
- **Fabric Port:** AI-assisted migration
- **Special Thanks:** [JetBrains](https://www.jetbrains.com/) for open source support

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE), same as the original Dominion plugin.

## Links

- [Original Dominion Repository](https://github.com/LunaDeerMC/Dominion)
- [Dominion Documentation](https://dominion.lunadeer.cn/)
- [Fabric Website](https://fabricmc.net/)
