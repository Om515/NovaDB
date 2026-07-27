package optimizer;

import command.SelectCommand;
import index.Index;
import index.IndexManager;
import model.Table;

import java.util.List;

/**
 * Responsible for analyzing parsing conditions to select the optimal runtime execution path.
 */
public class QueryOptimizer {
    private IndexManager indexManager;

    public QueryOptimizer(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    /**
     * Determines whether to perform an index lookup or full linear scan based on condition matching.
     */
    public ExecutionPlan choosePlan(Table table, SelectCommand command) {
        if (command.getWhereColumn() == null) {
            return ExecutionPlan.FULL_TABLE_SCAN;
        }

        List<Index> indices = indexManager.getIndicesForTable(table.getTableName());
        for (Index idx : indices) {
            if (idx.getColumnName().equals(command.getWhereColumn())) {
                return ExecutionPlan.INDEX_SCAN;
            }
        }

        return ExecutionPlan.FULL_TABLE_SCAN;
    }
}
