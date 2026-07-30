import cli.NovaShell;
import engine.DatabaseEngine;
import engine.QueryResult;

public class Test {
    public static void main(String[] args) {
        DatabaseEngine db = new DatabaseEngine("TestDB");
        
        // Clean up from previous runs if necessary
        try { db.execute("DROP TABLE Student"); } catch(Exception e) {}
        try { db.execute("DROP TABLE Department"); } catch(Exception e) {}
        
        System.out.println("TEST 1: Create Tables with FOREIGN KEY");
        System.out.println(db.execute("CREATE TABLE Department(deptId INT PRIMARY KEY, name STRING NOT NULL)"));
        System.out.println(db.execute("CREATE TABLE Student(id INT PRIMARY KEY, name STRING NOT NULL, deptId INT, FOREIGN KEY(deptId) REFERENCES Department(deptId))"));
        
        System.out.println("\nTEST 2: Insert into parent table");
        System.out.println(db.execute("INSERT INTO Department VALUES(10,'IT')"));
        System.out.println(db.execute("INSERT INTO Department VALUES(20,'HR')"));
        System.out.println(db.execute("INSERT INTO Department VALUES(30,'Finance')"));
        
        System.out.println("\nTEST 3: Valid Insert into child table");
        System.out.println(db.execute("INSERT INTO Student VALUES(1,'Om',10)"));
        
        System.out.println("\nTEST 4: Invalid Insert into child table (should fail)");
        try {
            System.out.println(db.execute("INSERT INTO Student VALUES(2,'Rahul',99)"));
        } catch (Exception e) {
            System.out.println("Caught Expected Error: " + e.getMessage());
        }
        
        System.out.println("\nTEST 5: Valid Update child table");
        System.out.println(db.execute("UPDATE Student SET deptId = 20 WHERE id = 1"));
        
        System.out.println("\nTEST 6: Invalid Update child table (should fail)");
        try {
            System.out.println(db.execute("UPDATE Student SET deptId = 99 WHERE id = 1"));
        } catch (Exception e) {
            System.out.println("Caught Expected Error: " + e.getMessage());
        }
        
        System.out.println("\nTEST 7: Invalid Delete from parent table (should fail)");
        try {
            System.out.println(db.execute("DELETE FROM Department WHERE deptId = 20"));
        } catch (Exception e) {
            System.out.println("Caught Expected Error: " + e.getMessage());
        }
        
        System.out.println("\nTEST 8: Valid Delete from parent table");
        System.out.println(db.execute("DELETE FROM Department WHERE deptId = 30"));
        
        System.out.println("\nTEST 9: Select all from child");
        System.out.println(db.execute("SELECT * FROM Student"));
    }
}
