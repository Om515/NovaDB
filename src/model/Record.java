package model;

import java.util.ArrayList;
import java.util.List;

/**
 * An individual row or record of data in a table.
 */
public class Record {
    private List<Cell> cells;

    public Record() {
        this.cells = new ArrayList<>();
    }

    public void addCell(Cell cell) {
        this.cells.add(cell);
    }

    public Cell getCell(int index) {
        return this.cells.get(index);
    }

    public List<Cell> getCells() {
        return cells;
    }

    public int size() {
        return this.cells.size();
    }

    @Override
    public String toString() {
        return "Record{" +
                "cells=" + cells +
                '}';
    }
}
