package parser;

import command.*;
import exception.ParserException;
import schema.Column;
import schema.DataType;
import schema.Schema;

import schema.ForeignKeyConstraint;

import java.util.ArrayList;
import java.util.List;

/**
 * Translates SQL-like strings into Command objects.
 */
public class SQLParser {
    private Tokenizer tokenizer;

    public SQLParser() {
        this.tokenizer = new Tokenizer();
    }

    /**
     * Parses the incoming SQL string and resolves it into a Command.
     */
    public Command parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new ParserException();
        }

        List<String> tokens = tokenizer.tokenize(input.trim());
        if (tokens.isEmpty()) {
            throw new ParserException();
        }

        String keyword = tokens.get(0).toUpperCase();

        switch (keyword) {
            case "CREATE":
                return parseCreate(tokens);
            case "INSERT":
                return parseInsert(tokens);
            case "SELECT":
                return parseSelect(tokens);
            case "UPDATE":
                return parseUpdate(tokens);
            case "DELETE":
                return parseDelete(tokens);
            case "SHOW":
                return parseShow(tokens);
            case "DROP":
                return parseDrop(tokens);
            default:
                throw new ParserException();
        }
    }

    private Command parseCreate(List<String> tokens) {
        if (tokens.size() > 1 && tokens.get(1).toUpperCase().equals("INDEX")) {
            return parseCreateIndex(tokens);
        }
        if (tokens.size() < 4 || !tokens.get(1).toUpperCase().equals("TABLE")) {
            throw new ParserException("Invalid CREATE command");
        }
        String tableName = tokens.get(2);
        Schema schema = new Schema();

        if (!tokens.get(3).equals("(")) {
            throw new ParserException();
        }

        int index = 4;
        while (index < tokens.size() && !tokens.get(index).equals(")")) {
            String token = tokens.get(index).toUpperCase();
            if (token.equals("FOREIGN") && index + 1 < tokens.size() && tokens.get(index + 1).toUpperCase().equals("KEY")) {
                index += 2;
                if (!tokens.get(index).equals("(")) throw new ParserException();
                index++;
                String childCol = tokens.get(index);
                index++;
                if (!tokens.get(index).equals(")")) throw new ParserException();
                index++;
                
                if (!tokens.get(index).toUpperCase().equals("REFERENCES")) throw new ParserException();
                index++;
                String parentTable = tokens.get(index);
                index++;
                if (!tokens.get(index).equals("(")) throw new ParserException();
                index++;
                String parentCol = tokens.get(index);
                index++;
                if (!tokens.get(index).equals(")")) throw new ParserException();
                index++;
                
                schema.addForeignKey(new ForeignKeyConstraint(childCol, parentTable, parentCol));
                
                if (index < tokens.size() && tokens.get(index).equals(",")) {
                    index++;
                }
                continue;
            }

            String colName = tokens.get(index);
            index++;
            if (index >= tokens.size())
                throw new ParserException();
            String colTypeStr = tokens.get(index).toUpperCase();
            DataType type;
            try {
                type = DataType.valueOf(colTypeStr);
            } catch (IllegalArgumentException e) {
                throw new ParserException();
            }
            index++;
            
            boolean isPrimaryKey = false;
            boolean isUnique = false;
            boolean isNotNull = false;
            
            while (index < tokens.size() && !tokens.get(index).equals(",") && !tokens.get(index).equals(")")) {
                String constraintToken = tokens.get(index).toUpperCase();
                if (constraintToken.equals("PRIMARY") && index + 1 < tokens.size() && tokens.get(index + 1).toUpperCase().equals("KEY")) {
                    isPrimaryKey = true;
                    index += 2;
                } else if (constraintToken.equals("UNIQUE")) {
                    isUnique = true;
                    index++;
                } else if (constraintToken.equals("NOT") && index + 1 < tokens.size() && tokens.get(index + 1).toUpperCase().equals("NULL")) {
                    isNotNull = true;
                    index += 2;
                } else {
                    throw new ParserException("Unknown constraint or token: " + constraintToken);
                }
            }

            schema.addColumn(new Column(colName, type, isPrimaryKey, isUnique, isNotNull));

            if (index < tokens.size() && tokens.get(index).equals(",")) {
                index++;
            }
        }

        return new CreateTableCommand(tableName, schema);
    }

    private Command parseInsert(List<String> tokens) {
        if (tokens.size() < 6 || !tokens.get(1).toUpperCase().equals("INTO")
                || !tokens.get(3).toUpperCase().equals("VALUES")) {
            throw new ParserException();
        }
        String tableName = tokens.get(2);

        List<List<Object>> valuesList = new ArrayList<>();
        int index = 4;
        while (index < tokens.size()) {
            if (!tokens.get(index).equals("("))
                throw new ParserException();
            
            index++;
            List<Object> values = new ArrayList<>();
            while (index < tokens.size() && !tokens.get(index).equals(")")) {
                String valStr = tokens.get(index);
                values.add(parseLiteral(valStr));
                index++;

                if (index < tokens.size() && tokens.get(index).equals(",")) {
                    index++;
                }
            }
            
            if (index >= tokens.size() || !tokens.get(index).equals(")"))
                throw new ParserException();
                
            valuesList.add(values);
            index++; // skip ")"
            
            if (index < tokens.size() && tokens.get(index).equals(",")) {
                index++; // skip ","
            } else {
                break;
            }
        }

        return new InsertCommand(tableName, valuesList);
    }

    private Command parseSelect(List<String> tokens) {
        if (tokens.size() < 4) {
            throw new ParserException();
        }

        boolean selectAll = tokens.get(1).equals("*");
        List<String> selectedColumns = new ArrayList<>();
        int fromIndex = -1;
        for (int i = 1; i < tokens.size(); i++) {
            if (tokens.get(i).toUpperCase().equals("FROM")) {
                fromIndex = i;
                break;
            }
        }
        if (fromIndex == -1 || fromIndex + 1 >= tokens.size()) {
            throw new ParserException("Expected FROM clause after SELECT projection");
        }

        if (!selectAll) {
            // tokens from 1 to fromIndex - 1 are projection columns, separated by comma.
            for (int i = 1; i < fromIndex; i++) {
                if (!tokens.get(i).equals(",")) {
                    selectedColumns.add(tokens.get(i));
                }
            }
        }

        String tableName = tokens.get(fromIndex + 1);

        String tableAlias = null;
        int index = fromIndex + 2;

        if (index < tokens.size()) {
            String token = tokens.get(index).toUpperCase();
            if (!token.equals("WHERE") && !token.equals("JOIN") && !token.equals("INNER") && !token.equals("LEFT") && !token.equals("RIGHT") && !token.equals("CROSS")) {
                if (token.equals("AS")) {
                    index++;
                    if (index >= tokens.size()) throw new ParserException();
                }
                tableAlias = tokens.get(index);
                index++;
            }
        }

        List<command.JoinCondition> joins = new ArrayList<>();

        while (index < tokens.size()) {
            String token = tokens.get(index).toUpperCase();
            if (token.equals("JOIN") || token.equals("INNER") || token.equals("LEFT") || token.equals("RIGHT") || token.equals("CROSS")) {
                String joinType = "INNER";
                if (token.equals("INNER") || token.equals("LEFT") || token.equals("RIGHT") || token.equals("CROSS")) {
                    joinType = token;
                    index++;
                    if (index < tokens.size() && tokens.get(index).toUpperCase().equals("OUTER")) {
                        index++;
                    }
                    if (index >= tokens.size() || !tokens.get(index).toUpperCase().equals("JOIN")) {
                        throw new ParserException("Expected JOIN after " + joinType);
                    }
                }
                index++;
                if (index >= tokens.size()) throw new ParserException();
                String targetTable = tokens.get(index);
                index++;

                String targetAlias = null;
                if (index < tokens.size()) {
                    String nextTok = tokens.get(index).toUpperCase();
                    if (!nextTok.equals("ON") && !nextTok.equals("JOIN") && !nextTok.equals("INNER") && !nextTok.equals("LEFT") && !nextTok.equals("RIGHT") && !nextTok.equals("CROSS") && !nextTok.equals("WHERE")) {
                        if (nextTok.equals("AS")) {
                            index++;
                            if (index >= tokens.size()) throw new ParserException();
                        }
                        targetAlias = tokens.get(index);
                        index++;
                    }
                }

                String leftCol = null;
                String rightCol = null;

                if (!joinType.equals("CROSS")) {
                    if (index >= tokens.size() || !tokens.get(index).toUpperCase().equals("ON")) {
                        throw new ParserException("Expected ON after JOIN target");
                    }
                    index++;

                    if (index >= tokens.size()) throw new ParserException();
                    leftCol = tokens.get(index);
                    index++;

                    if (index >= tokens.size() || !tokens.get(index).equals("=")) {
                        throw new ParserException("Expected '=' in JOIN condition");
                    }
                    index++;

                    if (index >= tokens.size()) throw new ParserException();
                    rightCol = tokens.get(index);
                    index++;
                }

                joins.add(new command.JoinCondition(joinType, targetTable, targetAlias, leftCol, rightCol));
            } else {
                break;
            }
        }

        String whereCol = null;
        Object whereVal = null;
        if (index < tokens.size() && tokens.get(index).toUpperCase().equals("WHERE")) {
            index++;
            if (index + 2 > tokens.size()) throw new ParserException();
            whereCol = tokens.get(index);
            index++;
            if (!tokens.get(index).equals("=")) throw new ParserException();
            index++;
            whereVal = parseLiteral(tokens.get(index));
        }

        return new SelectCommand(tableName, tableAlias, selectAll, selectedColumns, whereCol, whereVal, joins);
    }

    private Command parseUpdate(List<String> tokens) {
        if (tokens.size() < 10 || !tokens.get(2).toUpperCase().equals("SET")) {
            throw new ParserException();
        }

        String tableName = tokens.get(1);
        String colName = tokens.get(3);
        if (!tokens.get(4).equals("="))
            throw new ParserException();
        Object newValue = parseLiteral(tokens.get(5));

        if (!tokens.get(6).toUpperCase().equals("WHERE"))
            throw new ParserException();
        String whereCol = tokens.get(7);
        if (!tokens.get(8).equals("="))
            throw new ParserException();
        Object whereVal = parseLiteral(tokens.get(9));

        return new UpdateCommand(tableName, colName, newValue, whereCol, whereVal);
    }

    private Command parseDelete(List<String> tokens) {
        if (tokens.size() < 7 || !tokens.get(1).toUpperCase().equals("FROM")
                || !tokens.get(3).toUpperCase().equals("WHERE")) {
            throw new ParserException();
        }

        String tableName = tokens.get(2);
        String whereCol = tokens.get(4);
        if (!tokens.get(5).equals("="))
            throw new ParserException();
        Object whereVal = parseLiteral(tokens.get(6));

        return new DeleteCommand(tableName, whereCol, whereVal);
    }

    private Command parseShow(List<String> tokens) {
        if (tokens.size() < 2 || !tokens.get(1).toUpperCase().equals("TABLES")) {
            throw new ParserException();
        }
        return new ShowTablesCommand();
    }

    private Command parseDrop(List<String> tokens) {
        if (tokens.size() > 1 && tokens.get(1).toUpperCase().equals("INDEX")) {
            return parseDropIndex(tokens);
        }
        if (tokens.size() < 3 || !tokens.get(1).toUpperCase().equals("TABLE")) {
            throw new ParserException();
        }
        return new DropTableCommand(tokens.get(2));
    }

    private Command parseCreateIndex(List<String> tokens) {
        // CREATE INDEX idxName ON tableName ( colName )
        if (tokens.size() < 8 || !tokens.get(3).toUpperCase().equals("ON") || !tokens.get(5).equals("(")) {
            throw new ParserException();
        }
        String indexName = tokens.get(2);
        String tableName = tokens.get(4);
        String colName = tokens.get(6);
        return new CreateIndexCommand(indexName, tableName, colName);
    }

    private Command parseDropIndex(List<String> tokens) {
        if (tokens.size() < 3) {
            throw new ParserException();
        }
        return new DropIndexCommand(tokens.get(2));
    }

    private Object parseLiteral(String valStr) {
        if ((valStr.startsWith("'") && valStr.endsWith("'")) || 
            (valStr.startsWith("\"") && valStr.endsWith("\""))) {
            return valStr.substring(1, valStr.length() - 1);
        }
        if (valStr.equalsIgnoreCase("true") || valStr.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(valStr);
        }
        if (valStr.equalsIgnoreCase("null")) {
            return null;
        }
        try {
            if (valStr.contains(".")) {
                return Double.parseDouble(valStr);
            } else {
                return Integer.parseInt(valStr);
            }
        } catch (NumberFormatException e) {
            return valStr;
        }
    }
}
