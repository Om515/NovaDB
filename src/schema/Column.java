package schema;

/**
 * Represents a specific column configuration within a Schema.
 */
public class Column {
    private String name;
    private DataType type;
    private boolean isPrimaryKey;

    public Column(String name, DataType type) {
        this(name, type, false);
    }

    public Column(String name, DataType type, boolean isPrimaryKey) {
        this.name = name;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", isPrimaryKey=" + isPrimaryKey +
                '}';
    }
}
