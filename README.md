# Dominion - Fabric Edition

A territory anti-grief mod for Minecraft Fabric servers, ported from the original Bukkit/Paper plugin.

## Original Project

This is a **Fabric port** of the [Dominion](https://github.com/LunaDeerMC/Dominion) plugin originally developed by [LunaDeerMC](https://github.com/LunaDeerMC).

- **Original Author:** [LunaDeerMC](https://github.com/LunaDeerMC) / [zhangyuheng](https://github.com/ColdeZhang)
- **Original Repository:** https://github.com/LunaDeerMC/Dominion
- **Original License:** GPL-3.0
- **Documentation:** https://dominion.lunadeer.cn/

## Target Platform

- **Minecraft Version:** 26.1.2
- **Mod Loader:** Fabric
- **Java Version:** 25+

## Features

- Territory/land protection system
- Multi-language support (English, Chinese, Japanese, Russian)
- Database support (SQLite, PostgreSQL, MySQL, MariaDB)
- Permission system with groups and templates
- Border display and visualization
- Chest UI and Text UI interfaces
- Residence migration support

## Building

```bash
# Set Java 25 as JAVA_HOME
export JAVA_HOME=/path/to/java-25

# Build the mod
./gradlew build

# The output JAR will be in build/libs/
```

## Installation

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 26.1.2
2. Download the mod JAR from releases
3. Place the JAR in your server's `mods/` directory
4. Start the server - configuration files will be generated in `config/dominion/`

## Configuration

Configuration files are located in `config/dominion/`:
- `config.yml` - Main configuration
- `languages/` - Language files
- `limitations/` - Per-group limitation files
- `world-wide/` - Per-world default settings

## Commands

All commands are under `/dominion`:
- `/dominion create <name>` - Create a new dominion
- `/dominion menu` - Open the main menu
- `/dominion info` - Show dominion info
- `/dominion delete <name>` - Delete a dominion
- `/dominion member_add <dominion> <player>` - Add a member
- `/dominion set_env <dominion> <flag> <value>` - Set environment flags
- And many more...

## Permissions

- `dominion.default` - Basic dominion usage
- `dominion.admin` - Administrative commands

## Migration Status

This is a **Fabric port** of the Dominion plugin. The core architecture has been migrated and the project compiles successfully for Minecraft 26.1.2.

### Build Status
- ✅ **Compilation:** Successful
- ✅ **Build:** Successful (JAR output: `build/libs/Dominion-1.0.0.jar`)

### Completed
- [x] Project structure and build system (Gradle 9.5.1, Fabric Loom 1.17)
- [x] Database/storage layer (HikariCP, MyBatis, Flyway)
- [x] API DTOs and flag system (all 30+ flags)
- [x] Cache system with sector-based spatial indexing
- [x] Configuration and language system (SnakeYAML)
- [x] Command framework (Brigadier) with full command registration
- [x] Event handler framework with territory protection
- [x] TUI framework (Text UI)
- [x] CUI framework (Chest UI - basic)
- [x] NMS/display entity system
- [x] All source files migrated (230+ Java files)
- [x] DominionProvider with full CRUD operations
- [x] Territory protection events (block break, place, use, PVP, etc.)

### In Progress / TODO
- [ ] Complete CUI integration
- [ ] PlaceholderAPI integration (pb4)
- [ ] Economy integration (Vault equivalent)
- [ ] Residence migration
- [ ] Multi-server support
- [ ] Server testing

## Credits

- **Original Plugin:** [Dominion](https://github.com/LunaDeerMC/Dominion) by [LunaDeerMC](https://github.com/LunaDeerMC)
- **Fabric Port:** AI-assisted migration
- **Special Thanks:** [JetBrains](https://www.jetbrains.com/) for open source support

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE), same as the original Dominion plugin.

## Links

- [Original Dominion Repository](https://github.com/LunaDeerMC/Dominion)
- [Dominion Documentation](https://dominion.lunadeer.cn/)
- [Fabric Website](https://fabricmc.net/)
