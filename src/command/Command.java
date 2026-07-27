package command;

/**
 * Base abstract class for all actionable commands in the database.
 */
public abstract class Command {
    private CommandType type;

    public Command(CommandType type) {
        this.type = type;
    }

    public CommandType getType() {
        return type;
    }
}
