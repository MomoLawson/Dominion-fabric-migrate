package cn.lunadeer.dominion.utils.command;

public class NoPermissionException extends RuntimeException {
    public NoPermissionException(String permission) {
        super("You do not have permission " + permission + " to do this.");
    }
}
