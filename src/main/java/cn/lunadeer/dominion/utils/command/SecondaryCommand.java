package cn.lunadeer.dominion.utils.command;

/**
 * Fabric-ported command framework: simplified secondary command base class for Brigadier.
 * In Fabric/Brigadier, commands are registered via CommandManager.registerCommand() which
 * builds a Brigadier tree. This base class is retained for organizational parity with the
 * Bukkit source, but the actual Brigadier nodes are constructed in CommandManager.
 */
public abstract class SecondaryCommand {
    private final String command;
    private final String description;
    private boolean dynamic = false;

    public SecondaryCommand(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public SecondaryCommand(String command) {
        this(command, "");
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public SecondaryCommand setDescription(String description) {
        return this;
    }

    public SecondaryCommand dynamic() {
        dynamic = true;
        return this;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    /**
     * Registers this secondary command with the Fabric CommandManager.
     * In the Fabric port this is a no-op placeholder; actual registration
     * happens in InitCommands.java via Brigadier.
     */
    public SecondaryCommand register() {
        // In Fabric, registration is done centrally in InitCommands.
        // This method is kept for source-level compatibility so that
        // static field initializers in command classes compile without changes.
        return this;
    }
}
