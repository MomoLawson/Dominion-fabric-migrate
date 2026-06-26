package cn.lunadeer.dominion.utils.command;

import java.util.List;
import java.util.function.Function;

/**
 * Fabric port: represents a command argument. In Brigadier, arguments are defined
 * inline when building the command tree, so this class is a compatibility stub.
 */
public class Argument {
    private final String name;
    private final boolean required;
    private String value = "";

    public Argument(String name, boolean required) {
        this.name = name;
        this.required = required;
    }

    public Argument(String name, String defaultValue) {
        this.name = name;
        this.required = false;
        this.value = defaultValue;
    }

    public Argument(String name, boolean required, Suggestion suggestion) {
        this.name = name;
        this.required = required;
    }

    public String getName() { return name; }
    public boolean isRequired() { return required; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public Argument copy() {
        Argument copy = new Argument(name, required);
        copy.value = this.value;
        return copy;
    }

    @Override
    public String toString() {
        return required ? "<" + name + ">" : "[" + name + "]";
    }
}
