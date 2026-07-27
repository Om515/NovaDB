package parser;

import command.*;
import exception.ParserException;
import schema.Column;
import schema.DataType;
import schema.Schema;

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
            schema.addColumn(new Column(colName, type));
            index++;

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

        if (!tokens.get(4).equals("("))
            throw new ParserException();

        List<Object> values = new ArrayList<>();
        int index = 5;
        while (index < tokens.size() && !tokens.get(index).equals(")")) {
            String valStr = tokens.get(index);
            values.add(parseLiteral(valStr));
            index++;

            if (index < tokens.size() && tokens.get(index).equals(",")) {
                index++;
            }
        }

        return new InsertCommand(tableName, values);
    }

    private Command parseSelect(List<String> tokens) {
        if (tokens.size() < 4 || !tokens.get(2).toUpperCase().equals("FROM")) {
            throw new ParserException();
        }

        boolean selectAll = tokens.get(1).equals("*");
        String tableName = tokens.get(3);

        if (tokens.size() > 4 && tokens.get(4).toUpperCase().equals("WHERE")) {
            if (tokens.size() < 8)
                throw new ParserException();
            String whereCol = tokens.get(5);
            if (!tokens.get(6).equals("="))
                throw new ParserException();
            Object whereVal = parseLiteral(tokens.get(7));
            return new SelectCommand(tableName, selectAll, whereCol, whereVal);
        }

        return new SelectCommand(tableName, selectAll, null, null);
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
        if (valStr.startsWith("'") && valStr.endsWith("'")) {
            return valStr.substring(1, valStr.length() - 1);
        }
        if (valStr.equalsIgnoreCase("true") || valStr.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(valStr);
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
