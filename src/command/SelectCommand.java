package command;

import java.util.ArrayList;
import java.util.List;

/**
 * Command representing a SELECT operation.
 */
public class SelectCommand extends Command {
    private String tableName;
    private String tableAlias;
    private String whereColumn;
    private Object whereValue;
    private boolean selectAll;
    private List<String> selectedColumns;
    private List<JoinCondition> joins;

    public SelectCommand(String tableName, String tableAlias, boolean selectAll, List<String> selectedColumns, String whereColumn, Object whereValue, List<JoinCondition> joins) {
        super(CommandType.SELECT);
        this.tableName = tableName;
        this.tableAlias = tableAlias;
        this.selectAll = selectAll;
        this.selectedColumns = selectedColumns != null ? selectedColumns : new ArrayList<>();
        this.whereColumn = whereColumn;
        this.whereValue = whereValue;
        this.joins = joins != null ? joins : new ArrayList<>();
    }

    public SelectCommand(String tableName, boolean selectAll, String whereColumn, Object whereValue) {
        this(tableName, null, selectAll, new ArrayList<>(), whereColumn, whereValue, new ArrayList<>());
    }

    public String getTableName() {
        return tableName;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public boolean isSelectAll() {
        return selectAll;
    }

    public List<String> getSelectedColumns() {
        return selectedColumns;
    }

    public String getWhereColumn() {
        return whereColumn;
    }

    public Object getWhereValue() {
        return whereValue;
    }

    public List<JoinCondition> getJoins() {
        return joins;
    }
    
    @Override
    public String toString() {
        return "SelectCommand{tableName='" + tableName + "', selectAll=" + selectAll + ", whereColumn='" + whereColumn + "', whereValue=" + whereValue + ", joins=" + joins + "}";
    }
}
