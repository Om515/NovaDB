import cli.NovaShell;
import engine.DatabaseEngine;
import engine.QueryResult;

public class Test {
    public static void main(String[] args) {
        DatabaseEngine db = new DatabaseEngine("TestDB");
        
        // Clean up from previous runs if necessary
        try { db.execute("DROP INDEX idx_student_id"); } catch(Exception e) {}
        try { db.execute("DROP TABLE student"); } catch(Exception e) {}
        try { db.execute("DROP TABLE Student"); } catch(Exception e) {}
        
        System.out.println("TEST 1: Create Table with PRIMARY KEY");
        System.out.println(db.execute("CREATE TABLE student(id INT PRIMARY KEY, name STRING, age INT)"));
        
        System.out.println("\nTEST 2: Insert row 1");
        System.out.println(db.execute("INSERT INTO student VALUES(1,'Om',21)"));
        
        System.out.println("\nTEST 3: Insert row 2");
        System.out.println(db.execute("INSERT INTO student VALUES(2,'Rahul',22)"));
        
        System.out.println("\nTEST 4: Duplicate PK Insert (should fail)");
        try {
            System.out.println(db.execute("INSERT INTO student VALUES(1,'Amit',25)"));
        } catch (Exception e) {
            System.out.println("Caught Expected Error: " + e.getMessage());
        }
        
        System.out.println("\nTEST 5: NULL PK Insert (should fail)");
        try {
            System.out.println(db.execute("INSERT INTO student VALUES(NULL,'Om',21)"));
        } catch (Exception e) {
            System.out.println("Caught Expected Error: " + e.getMessage());
        }
        
        System.out.println("\nTEST 6: Duplicate PK Update (should fail)");
        try {
            System.out.println(db.execute("UPDATE student SET id = 2 WHERE id = 1"));
        } catch (Exception e) {
            System.out.println("Caught Expected Error: " + e.getMessage());
        }
        
        System.out.println("\nTEST 7: Valid PK Update");
        System.out.println(db.execute("UPDATE student SET id = 10 WHERE id = 1"));
        
        System.out.println("\nTEST 8: Select all");
        System.out.println(db.execute("SELECT * FROM student"));
    }
}
