package cn.lunadeer.dominion.utils.command;

public class InvalidArgumentException extends RuntimeException {
    public InvalidArgumentException(String usage) {
        super("Invalid arguments, usage e.g. " + usage + ".");
    }
}
