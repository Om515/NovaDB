package command;

public class DropIndexCommand extends Command {
    private String indexName;

    public DropIndexCommand(String indexName) {
        super(CommandType.DROP_INDEX);
        this.indexName = indexName;
    }

    public String getIndexName() {
        return indexName;
    }
}
