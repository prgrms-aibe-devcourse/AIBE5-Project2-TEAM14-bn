package com.aiegoo.comicrental;

/**
 * Simple request parser for CLI commands.
 * It splits the raw input line into command and arguments.
 */
public class Rq {
    private final String command;
    private final String[] args;

    public Rq(String line) {
        if (line == null) {
            this.command = "";
            this.args = new String[0];
        } else {
            String[] parts = line.trim().split("\\s+");
            if (parts.length == 0) {
                this.command = "";
                this.args = new String[0];
            } else {
                this.command = parts[0];
                this.args = new String[parts.length - 1];
                System.arraycopy(parts, 1, this.args, 0, this.args.length);
            }
        }
    }

    /**
     * @return the primary command token (e.g. "comic-add", "rent").
     */
    public String getCommand() {
        return command;
    }

    /**
     * @return raw argument array after the command.
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Convenience method to get an argument by index, or null if absent.
     */
    public String getArg(int index) {
        if (index < 0 || index >= args.length) return null;
        return args[index];
    }
}
