# NovaDB Architecture Overview

## 1. Introduction
NovaDB is a custom-built, lightweight Relational Database Management System (RDBMS) implemented in Java. It provides core relational database capabilities including SQL parsing, a custom binary storage engine, B+ Tree-based indexing, schema and constraint management, and foundational concurrency and caching mechanisms.

This document serves as a comprehensive guide for understanding the project's codebase, architecture, and current state of implementation.

## 2. Package & Directory Structure
The source code is located in `d:\NovaDB\src\` and is modularized into several key packages:

- **`engine`**: The central coordinator (`DatabaseEngine`, `QueryEngine`). It orchestrates parsed queries, validates constraints, and interacts with storage and index managers.
- **`storage`**: Contains logic for reading from and writing to disk. Includes `FileStorageManager` (binary file `.db` storage), `MetadataManager`, `RecordSerializer`, and `RecordDeserializer`.
- **`index`**: Implements a native B+ Tree (`BPlusTree`, `BPlusNode`, `InternalNode`, `LeafNode`) for `O(log N)` index lookups and an `IndexManager` to coordinate them.
- **`schema`**: Handles structural definitions like `Schema`, `Column`, `DataType`, and constraints (e.g., `ForeignKeyConstraint`).
- **`model`**: Core logical data entities (`Database`, `Table`, `Record`, `Cell`).
- **`parser`**: Tokenizes and parses raw SQL into executable `Command` structures.
- **`command`**: Represents actionable SQL payloads (e.g., `InsertCommand`, `SelectCommand`, `CreateTableCommand`).
- **`optimizer`**: Basic query planner (`QueryOptimizer`, `ExecutionPlan`) capable of falling back to table scans or accelerating queries via `INDEX_SCAN`.
- **`cache`**: Memory management implementations, including `BufferPool` and `LRUCache`.
- **`concurrency`**: Handles thread pools and lock management (`LockManager`, `DatabaseExecutor`).
- **`cli`**: Contains `NovaShell` for interacting with the database via command line.
- **`exception`**: Custom domain exceptions (`DatabaseException`, `StorageException`, `QueryException`, etc.).

## 3. Core Subsystems

### 3.1. Query Engine (`engine`)
At the heart of NovaDB is the `DatabaseEngine` and `QueryEngine`.
- **`DatabaseEngine`** initializes the system state, loading metadata, indexes, and bootstrapping sub-managers.
- **`QueryEngine`** executes specific `Command` payloads. It enforces UNIQUE, NOT NULL, and FOREIGN KEY constraints before allowing data mutation.

### 3.2. Storage Layer (`storage`)
`FileStorageManager` stores table data in binary files inside the `database/tables/` directory.
- Rows are conceptualized as `Record` objects containing sequential `Cell` objects.
- `RecordSerializer` converts a `Record` into byte arrays, prefixing them with their length, and writes them contiguously to table `.db` files.
- The storage system uses a basic `sessionCache` Map to keep hot records in memory for quick retrieval.

### 3.3. Indexing Layer (`index`)
NovaDB uses an in-house generic `BPlusTree<K, V>` implementation.
- Provides `O(log N)` search limits.
- B+ Tree nodes correctly split and handle overflow across internal and leaf nodes.
- During DML operations (Insert/Update/Delete), `QueryEngine` notifies the `IndexManager` to update the corresponding keys in all associated indexes to prevent index staleness.

### 3.4. Caching & Buffer Pool (`cache`)
A newly implemented (Phase 7A) `BufferPool` manages memory frames and pages to decouple disk I/O operations from query processing using LRU caching strategies.

## 4. Workflows & Execution Paths

### 4.1. Data Insertion (INSERT)
1. **Parse:** `SQLParser` tokenizes the SQL string and builds an `InsertCommand`.
2. **Schema & Constraint Check:** `QueryEngine` validates that the inserted count matches the schema. It checks `NOT NULL`, `UNIQUE`/Primary Key constraints, and cross-references `ForeignKeyConstraint` conditions.
3. **Serialization:** `FileStorageManager` takes the new `Record`, serializes it, and appends it to the table's `.db` file, returning its record position (offset index).
4. **Index Update:** `IndexManager` inserts the indexed column values into their respective B+ Trees, tying the key to the newly acquired integer record position.

### 4.2. Data Retrieval (SELECT)
1. **Plan Selection:** `QueryOptimizer` evaluates the `SelectCommand`. If the query has a `WHERE` clause matching an indexed column, it selects `ExecutionPlan.INDEX_SCAN`. If not, it defaults to a full table scan.
2. **Index Scan Execution:** The `BPlusTree.search(key)` returns the physical record position. `FileStorageManager` directly fetches the record at that position in `O(1)` amortized I/O.
3. **Filtering:** Returns mapped rows.

## 5. Current Project Context & Known Behaviors
When working on this codebase, keep the following recent contexts in mind:
- **Duplicate Key Indexing Policy**: The B+ Tree implementation has been transitioned to support multi-value buckets for non-unique indexes and properly enforce data integrity.
- **Stale Index Bug Fixes**: The database has historically battled "Stale Index After Delete" bugs where shifting record positions upon file deletion invalidated index pointers. Ensuring synchronous index updating and utilizing logical indirection (or steady sequential positions) is critical.
- **Buffer Pool Integration**: `Page`, `PageFrame`, and `BufferPool` classes have been merged to pave the way for true page-oriented disk management as opposed to direct continuous file appends.

## 6. Contribution Guide for AI Agents
- The entry point for manual testing is typically `Main.java` or `cli.NovaShell`.
- Respect boundaries: Avoid side-stepping managers. For example, do not modify `.db` files bypassing the `FileStorageManager`.
- Ensure constraints are evaluated pre-mutation within `QueryEngine`.
- Use the detailed `exception` classes mapped to appropriate fault scenarios.
