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

This is a **work-in-progress** migration from Bukkit/Paper to Fabric. The core architecture and most subsystems have been ported, but some features may not be fully functional yet. Contributions are welcome!

### Completed
- [x] Project structure and build system
- [x] Database/storage layer (HikariCP, MyBatis, Flyway)
- [x] API DTOs and flag system
- [x] Cache system with sector-based spatial indexing
- [x] Configuration and language system
- [x] Command framework (Brigadier)
- [x] Event handler framework
- [x] TUI framework
- [x] CUI framework (basic)
- [x] NMS/display entity system

### In Progress
- [ ] Full command implementations
- [ ] All event handler registrations
- [ ] Complete CUI integration
- [ ] PlaceholderAPI integration (pb4)
- [ ] Economy integration

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
