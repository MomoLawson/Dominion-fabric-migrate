package cn.lunadeer.dominion.utils.command;

import java.util.List;

/**
 * Fabric port: option argument with fixed possible values.
 */
public class Option extends Argument {
    private final List<String> options;

    public Option(List<String> options) {
        super(options.get(0), true);
        this.options = options;
    }

    public Option(List<String> options, String defaultValue) {
        super(options.get(0), defaultValue);
        this.options = options;
    }

    public List<String> getOptions() { return options; }

    @Override
    public Option copy() {
        Option copy = new Option(options, getValue());
        copy.setValue(this.getValue());
        return copy;
    }
}
