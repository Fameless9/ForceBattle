package net.fameless.core.command.framework;

public enum CallerType {

    CONSOLE("command.console_command"),
    PLAYER("command.not_a_player"),
    NONE("error.no_caller_type");

    private final String errorMessageKey;

    CallerType(String errorMessageKey) {
        this.errorMessageKey = errorMessageKey;
    }

    public boolean allows(CommandCaller caller) {
        if (this == CallerType.NONE) {
            return true;
        }
        return this == caller.callerType();
    }

    public String getErrorMessageKey() {
        return errorMessageKey;
    }
}
