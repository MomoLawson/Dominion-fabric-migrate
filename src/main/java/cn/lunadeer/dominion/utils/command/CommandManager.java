package cn.lunadeer.dominion.utils.command;

/**
 * Fabric-ported command manager. In Fabric, actual command registration uses Brigadier
 * via CommandRegistrationCallback in InitCommands. This class is a stub for compatibility.
 */
public class CommandManager {

    private static String rootCommand = "/dominion";

    public static String getRootCommand() {
        return rootCommand;
    }

    public static void setRootCommand(String cmd) {
        rootCommand = "/" + cmd;
    }
}
