package command;

/**
 * Command representing a SHOW TABLES operation.
 */
public class ShowTablesCommand extends Command {
    public ShowTablesCommand() {
        super(CommandType.SHOW_TABLES);
    }
    
    @Override
    public String toString() {
        return "ShowTablesCommand{}";
    }
}
