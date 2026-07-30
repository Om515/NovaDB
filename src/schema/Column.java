package schema;

/**
 * Represents a specific column configuration within a Schema.
 */
public class Column {
    private String name;
    private DataType type;
    private boolean isPrimaryKey;
    private boolean isUnique;
    private boolean isNotNull;

    public Column(String name, DataType type) {
        this(name, type, false, false, false);
    }

    public Column(String name, DataType type, boolean isPrimaryKey) {
        this(name, type, isPrimaryKey, false, false);
    }

    public Column(String name, DataType type, boolean isPrimaryKey, boolean isUnique, boolean isNotNull) {
        this.name = name;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
        this.isUnique = isUnique;
        this.isNotNull = isNotNull;
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

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean unique) {
        isUnique = unique;
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public void setNotNull(boolean notNull) {
        isNotNull = notNull;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", isPrimaryKey=" + isPrimaryKey +
                ", isUnique=" + isUnique +
                ", isNotNull=" + isNotNull +
                '}';
    }
}
