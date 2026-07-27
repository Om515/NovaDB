package model;

/**
 * A single data cell corresponding to a specific value in a record.
 */
public class Cell {
    private Object value;

    public Cell(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "value=" + value +
                '}';
    }
}
