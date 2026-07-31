package command;

public class JoinCondition {
    private String joinType;
    private String targetTable;
    private String targetAlias;
    private String leftColumn;
    private String rightColumn;

    public JoinCondition(String joinType, String targetTable, String targetAlias, String leftColumn, String rightColumn) {
        this.joinType = joinType;
        this.targetTable = targetTable;
        this.targetAlias = targetAlias;
        this.leftColumn = leftColumn;
        this.rightColumn = rightColumn;
    }

    public String getJoinType() {
        return joinType;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public String getTargetAlias() {
        return targetAlias;
    }

    public String getLeftColumn() {
        return leftColumn;
    }

    public String getRightColumn() {
        return rightColumn;
    }

    @Override
    public String toString() {
        return joinType + " JOIN " + targetTable + (targetAlias != null ? " " + targetAlias : "") + " ON " + leftColumn + " = " + rightColumn;
    }
}
