# Design File System - Comprehensive Solution 📁

## **Problem Statement**

Design a hierarchical file system that can:
- Create files and directories
- Organize in a tree structure (parent-child relationships)
- Support recursive operations (size calculation, display, search)
- Treat files and directories uniformly
- Navigate paths (e.g., `/home/user/documents/file.txt`)
- Track metadata (creation time, modification time)
- Handle operations like add, remove, display tree

---

## **🎯 Our Approach**

### **1. Core Requirements Analysis**

**Functional Requirements:**
- ✅ Create files with content
- ✅ Create directories (can contain files/directories)
- ✅ Add/remove files and directories
- ✅ Navigate hierarchical structure
- ✅ Display tree representation
- ✅ Track timestamps

**Non-Functional Requirements:**
- ✅ Uniform interface for files and directories
- ✅ Easy to add new node types (symbolic links, shortcuts)
- ✅ Recursive operations should be natural
- ✅ Memory efficient
- ✅ Type-safe operations

**The Core Challenge:**
Files and directories are fundamentally different:
- **Files** are *leaf nodes* - cannot contain other nodes
- **Directories** are *composite nodes* - can contain files/directories

But they share many operations: getName(), display(), delete()

**How do we handle this elegantly?** → **Composite Pattern!**

---

## **🏗️ Architecture & Design Patterns**

### **The Star: Composite Pattern** ⭐⭐⭐

**What is Composite Pattern?**
> Composite pattern lets you compose objects into tree structures and allows clients to treat individual objects and compositions uniformly.

**The Problem It Solves:**

```java
// ❌ Without Composite Pattern - Client needs to know types
if (node instanceof File) {
    File file = (File) node;
    file.display();
} else if (node instanceof Directory) {
    Directory dir = (Directory) node;
    dir.display();
    for (Node child : dir.getChildren()) {
        // Repeat the if-else recursively... nightmare!
    }
}

// ✅ With Composite Pattern - Uniform interface
node.display(); // Works for both File and Directory!
// No type checking needed, polymorphism handles it
```

**Key Insight:** Both files and directories are "file system nodes" that can be displayed, deleted, and have metadata. The Composite pattern gives them a common interface.

---

### **Our Implementation Structure**

```
┌────────────────────────┐
│   FileSystemNode       │ (Component - Abstract Base)
│   ----------------     │
│   - name               │
│   - children: Map      │
│   - createdAt          │
│   - modifiedAt         │
│   ----------------     │
│   + addChild()         │
│   + removeChild()      │
│   + getChild()         │
│   + display() abstract │
│   + isFile() abstract  │
└───────────┬────────────┘
            │
     ┌──────┴──────┐
     │             │
┌────▼────┐   ┌───▼─────────┐
│  File   │   │  Directory  │ (Composite - Container)
│ (Leaf)  │   │             │
├─────────┤   ├─────────────┤
│-content │   │             │
│-ext     │   │   Can       │
├─────────┤   │  contain    │
│display()│   │   Files     │
│isFile() │   │    AND      │
│→ true   │   │ Directories │
└─────────┘   ├─────────────┤
              │display()    │
              │isFile()     │
              │→ false      │
              └─────────────┘
```

**Component (FileSystemNode):**
- Abstract base class
- Defines common interface
- Provides default implementations for child management

**Leaf (File):**
- Cannot have children (leaf node)
- Has specific data (content, extension)
- Implements abstract methods

**Composite (Directory):**
- Can have children
- Recursive operations on children
- Implements abstract methods

---

## **🔑 Key Design Decisions**

### **Decision 1: Abstract Class vs Interface for Component**

**What:** Used abstract class `FileSystemNode` instead of interface

**Why:**
```java
// Files and directories share STATE (not just behavior)
abstract class FileSystemNode {
    private String name;                    // Common state
    private Map<String, FileSystemNode> children;  // Common state
    private LocalDateTime createdAt;        // Common state

    // Default behavior
    public void addChild(String name, FileSystemNode child) {
        this.children.put(name, child);
    }

    // Must be overridden
    public abstract void display(int depth);
}
```

**Benefits:**
- ✅ Share state (name, timestamps, children map)
- ✅ Provide default implementations (addChild, removeChild)
- ✅ Subclasses only override what's specific to them

**Interview Question:**
> "Why not use interface with default methods (Java 8+)?"

**Answer:**
> "Interfaces can't have instance fields. FileSystemNode needs shared state like name, children, and timestamps. Abstract class is the right choice when we need both shared state and behavior. If we only needed behavior contracts, interface would work."

---

### **Decision 2: Uniform Interface (Polymorphism)**

**What:** Client code doesn't need to know if it's dealing with File or Directory

```java
// Client code - works for both!
public void printTree(FileSystemNode node) {
    node.display(0);  // Polymorphism!
}

// Works for File
printTree(new File("document.txt"));

// Works for Directory
printTree(new Directory("folder"));
```

**Why:**
- Simplifies client code (no type checking)
- Easy to add new node types (SymbolicLink, Shortcut)
- Recursive operations are natural

**Interview Question:**
> "What if files and directories have completely different operations?"

**Answer:**
> "Then Composite Pattern might not be ideal. For example, if files need `read()` but directories need `list()`, we'd have issues. However, for common operations like display, delete, move, permissions - they apply to both, making Composite perfect. For type-specific operations, we can use the Visitor pattern or check types when absolutely necessary."

---

### **Decision 3: Children Management in Base Class**

**What:** Put children map in base FileSystemNode, even though File doesn't use it

**Trade-off:**
```
✅ Pros:
   - Uniform interface (no type checking)
   - Simplified recursive algorithms
   - Easy to iterate over any node's children

❌ Cons:
   - Files waste memory on empty children map
   - Potential for misuse (adding child to a file)
```

**Why It's Worth It:**
- Memory overhead is minimal (one empty HashMap per file)
- The API simplicity gain is huge
- Can add safeguards in File class to prevent child operations

**Better Alternative for Production:**
```java
// Add safeguard in File
public class File extends FileSystemNode {
    @Override
    public void addChild(String name, FileSystemNode child) {
        throw new UnsupportedOperationException(
            "Cannot add child to a file");
    }
}
```

---

### **Decision 4: Recursive Display Implementation**

**How It Works:**

```java
// Directory.display()
public void display(int depth) {
    String indent = " ".repeat(depth * 2);
    System.out.println(indent + "📁 " + getName());

    // Recursive magic!
    for (FileSystemNode child : getChildren()) {
        child.display(depth + 1);  // Polymorphic call
    }
}

// File.display()
public void display(int depth) {
    String indent = " ".repeat(depth * 2);
    System.out.println(indent + "📄 " + getName());
    // No children to process (base case)
}
```

**Example Output:**
```
📁 root (3 items)
  📁 documents (2 items)
    📄 resume.pdf
    📄 cover_letter.docx
  📁 photos (1 items)
    📄 vacation.jpg
  📄 readme.txt
```

**Why It's Elegant:**
- Directories handle recursion
- Files provide base case
- Depth parameter tracks indentation
- No type checking needed!

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `File` - Only represents a file (content, extension)
- `Directory` - Only represents a directory (children management)
- `FileSystemNode` - Common behavior for all nodes

Each class has ONE reason to change.

### **O - Open/Closed**
Adding a new node type (e.g., SymbolicLink):
```java
public class SymbolicLink extends FileSystemNode {
    private String targetPath;

    @Override
    public void display(int depth) {
        String indent = " ".repeat(depth * 2);
        System.out.println(indent + "🔗 " + getName()
            + " -> " + targetPath);
    }

    @Override
    public boolean isFile() {
        return false; // Or depends on target
    }
}
```
✅ Added new feature WITHOUT modifying existing code!

### **L - Liskov Substitution**
Any `FileSystemNode` can be replaced by `File` or `Directory`:
```java
FileSystemNode node = new File("test.txt");
node.display(0);  // Works!

node = new Directory("folder");
node.display(0);  // Works!
```

Polymorphism works correctly, no surprises.

### **I - Interface Segregation**
- `FileSystemNode` has minimal interface
- Only methods common to all nodes
- File-specific methods (getContent) in File class
- Directory-specific methods in Directory class

### **D - Dependency Inversion**
Client depends on `FileSystemNode` abstraction:
```java
public class FileSystem {
    private FileSystemNode root;  // Depends on abstraction

    public void display() {
        root.display(0);  // Works for any concrete type
    }
}
```

---

## **🎭 Scenario Walkthrough**

### **Scenario: Create File System and Display Tree**

```java
// 1. Create root directory
Directory root = new Directory("root");

// 2. Create documents directory
Directory docs = new Directory("documents");

// 3. Create files
File resume = new File("resume.pdf");
resume.setContent("My resume content...");

File coverLetter = new File("cover_letter.docx");

// 4. Build structure
docs.addChild("resume.pdf", resume);
docs.addChild("cover_letter.docx", coverLetter);
root.addChild("documents", docs);

// 5. Add file to root
File readme = new File("readme.txt");
root.addChild("readme.txt", readme);

// 6. Display entire tree
root.display(0);
```

**Execution Flow for `root.display(0)`:**

```
Step 1: root.display(0)
├─ Print: "📁 root (2 items)"
├─ Iterate children: [documents, readme.txt]
│
├─ Step 2: documents.display(1)
│  ├─ Print: "  📁 documents (2 items)"
│  ├─ Iterate children: [resume.pdf, cover_letter.docx]
│  │
│  ├─ Step 3: resume.pdf.display(2)
│  │  └─ Print: "    📄 resume.pdf"
│  │
│  └─ Step 4: cover_letter.docx.display(2)
│     └─ Print: "    📄 cover_letter.docx"
│
└─ Step 5: readme.txt.display(1)
   └─ Print: "  📄 readme.txt"
```

**Output:**
```
📁 root (2 items)
  📁 documents (2 items)
    📄 resume.pdf
    📄 cover_letter.docx
  📄 readme.txt
```

**Key Observations:**
- Recursion is natural and clean
- No type checking needed
- Each node handles its own display logic
- Polymorphism makes it work seamlessly

---

## **🚀 Extensions & Enhancements**

### **1. Calculate Total Size**

```java
// In FileSystemNode (abstract)
public abstract long getSize();

// In File
@Override
public long getSize() {
    return content != null ? content.length() : 0;
}

// In Directory (recursive!)
@Override
public long getSize() {
    long totalSize = 0;
    for (FileSystemNode child : getChildren()) {
        totalSize += child.getSize();  // Polymorphic recursion
    }
    return totalSize;
}
```

### **2. Search by Name**

```java
// In FileSystemNode
public FileSystemNode search(String name) {
    if (this.getName().equals(name)) {
        return this;
    }

    for (FileSystemNode child : getChildren()) {
        FileSystemNode result = child.search(name);
        if (result != null) {
            return result;
        }
    }
    return null;
}
```

### **3. Permissions System**

```java
public abstract class FileSystemNode {
    private String owner;
    private Set<Permission> permissions;

    public boolean canRead(String user) {
        return owner.equals(user) ||
               permissions.contains(Permission.READ);
    }

    public boolean canWrite(String user) {
        return owner.equals(user) ||
               permissions.contains(Permission.WRITE);
    }
}
```

### **4. Symbolic Links**

```java
public class SymbolicLink extends FileSystemNode {
    private String targetPath;
    private FileSystemNode target;

    @Override
    public void display(int depth) {
        String indent = " ".repeat(depth * 2);
        System.out.println(indent + "🔗 " + getName()
            + " -> " + targetPath);
    }

    @Override
    public long getSize() {
        return target != null ? target.getSize() : 0;
    }
}
```

---

## **🎯 Interview Questions & Answers**

### **Q1: How would you implement tree traversal (DFS/BFS)?**

**Answer:**

**DFS (Depth-First Search):**
```java
public void traverseDFS(FileSystemNode node, Consumer<FileSystemNode> visitor) {
    visitor.accept(node);  // Visit current node

    for (FileSystemNode child : node.getChildren()) {
        traverseDFS(child, visitor);  // Recursive DFS
    }
}

// Usage
traverseDFS(root, node -> System.out.println(node.getName()));
```

**BFS (Breadth-First Search):**
```java
public void traverseBFS(FileSystemNode root) {
    Queue<FileSystemNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        FileSystemNode node = queue.poll();
        System.out.println(node.getName());  // Visit

        // Add all children to queue
        queue.addAll(node.getChildren());
    }
}
```

**When to use:**
- **DFS**: Finding deep paths, checking all files
- **BFS**: Finding closest match, level-order listing

---

### **Q2: How to handle file permissions (owner, group, others)?**

**Answer:**

```java
public enum Permission {
    READ(4), WRITE(2), EXECUTE(1);

    private int value;
    Permission(int value) { this.value = value; }
}

public class FileSystemNode {
    private String owner;
    private String group;
    private int ownerPerms;   // rwx = 7, rw- = 6, etc.
    private int groupPerms;
    private int otherPerms;

    public boolean canAccess(String user, Permission perm) {
        if (user.equals(owner)) {
            return (ownerPerms & perm.value) != 0;
        } else if (isInGroup(user, group)) {
            return (groupPerms & perm.value) != 0;
        } else {
            return (otherPerms & perm.value) != 0;
        }
    }

    // Unix-style: chmod 755 (owner:rwx, group:r-x, other:r-x)
    public void setPermissions(int owner, int group, int other) {
        this.ownerPerms = owner;
        this.groupPerms = group;
        this.otherPerms = other;
    }
}
```

**Extensions:**
- ACL (Access Control Lists) for fine-grained control
- Role-based access (admin, editor, viewer)
- Inherited permissions from parent directories

---

### **Q3: How would you handle symbolic links to prevent infinite loops?**

**Answer:**

**Problem:** Symbolic link can point to parent directory → infinite loop!

```
/home/user/documents -> /home  (creates cycle!)
```

**Solutions:**

**1. Track Visited Nodes (Set):**
```java
public void display(int depth, Set<FileSystemNode> visited) {
    if (visited.contains(this)) {
        System.out.println("🔗 [CYCLE DETECTED]");
        return;  // Stop recursion
    }

    visited.add(this);

    // Continue normal display...
    for (FileSystemNode child : getChildren()) {
        child.display(depth + 1, visited);
    }
}
```

**2. Limit Recursion Depth:**
```java
private static final int MAX_DEPTH = 100;

public void display(int depth) {
    if (depth > MAX_DEPTH) {
        throw new IllegalStateException("Max depth exceeded - possible cycle");
    }
    // Continue...
}
```

**3. Canonical Path Resolution:**
```java
public class SymbolicLink extends FileSystemNode {
    private String canonicalPath;  // Absolute resolved path

    // Before following link, check if it's an ancestor
    public boolean createsLoop(FileSystemNode current) {
        FileSystemNode node = current;
        while (node != null) {
            if (node.getPath().equals(canonicalPath)) {
                return true;  // Loop detected!
            }
            node = node.getParent();
        }
        return false;
    }
}
```

---

### **Q4: How to scale this to a distributed file system (like HDFS, GFS)?**

**Answer:**

**Challenges:**
1. **Single machine can't store all files**
2. **Need redundancy (replication)**
3. **Concurrent access from multiple clients**
4. **Fault tolerance**

**Design Approach:**

**1. Separate Metadata from Data:**
```java
// Master Server (NameNode in HDFS)
class MetadataServer {
    Map<String, FileMetadata> fileMetadata;

    class FileMetadata {
        String path;
        long size;
        List<String> blockLocations;  // Which servers have data
        int replicationFactor;
    }

    public List<String> getBlockLocations(String path) {
        return fileMetadata.get(path).blockLocations;
    }
}

// Data Servers (DataNodes in HDFS)
class DataServer {
    Map<String, byte[]> blocks;

    public byte[] readBlock(String blockId) {
        return blocks.get(blockId);
    }
}
```

**2. Client Flow:**
```
1. Client requests file from MetadataServer
   → Gets list of DataServer locations

2. Client reads blocks in parallel from DataServers
   → Assembles complete file

3. Client writes file:
   → MetadataServer allocates block IDs
   → Client writes to DataServers
   → DataServers replicate to others
   → Client confirms to MetadataServer
```

**3. Replication Strategy:**
- Store 3 copies of each block (configurable)
- Distribute across different racks (fault tolerance)
- MetadataServer monitors DataServer health
- Auto-replicate if server fails

**4. Consistency Model:**
- **Strong consistency**: All clients see same data (slower)
- **Eventual consistency**: Reads might be stale (faster)
- HDFS uses: Write-once, read-many (append-only)

---

### **Q5: Explain RAID systems and how they relate to file systems**

**Answer:**

**RAID (Redundant Array of Independent Disks):**

**RAID 0 (Striping):**
```
File: [A][B][C][D]
Disk 1: [A] [C]
Disk 2: [B] [D]

Performance: 2x faster (parallel I/O)
Redundancy: None (one disk fails = data loss)
Capacity: 100%
```

**RAID 1 (Mirroring):**
```
File: [A][B][C][D]
Disk 1: [A][B][C][D]  (primary)
Disk 2: [A][B][C][D]  (mirror)

Performance: Same read, half write
Redundancy: Can lose 1 disk
Capacity: 50%
```

**RAID 5 (Striping + Parity):**
```
File: [A][B][C][D] + Parity
Disk 1: [A] [D] [Parity]
Disk 2: [B] [Parity] [C]
Disk 3: [Parity] [C] [D]

Performance: Good (parallel reads)
Redundancy: Can lose 1 disk (rebuild from parity)
Capacity: (N-1)/N (e.g., 66% for 3 disks)
```

**How File Systems Use RAID:**
- File system operates on logical volume
- RAID controller handles physical distribution
- File system doesn't care about RAID level
- Trade-off: Performance vs Redundancy vs Capacity

**Modern Approaches:**
- **Erasure Coding** (used by HDFS, GFS)
- **Reed-Solomon codes** (e.g., 10+4 scheme)
- Better space efficiency than mirroring
- More CPU overhead for encoding/decoding

---

### **Q6: How to implement file versioning (like Git)?**

**Answer:**

```java
public class VersionedFile extends File {
    private List<FileVersion> versions;
    private int currentVersion;

    class FileVersion {
        int versionNumber;
        String content;
        LocalDateTime timestamp;
        String author;
        String commitMessage;
    }

    public void commit(String content, String message, String author) {
        FileVersion version = new FileVersion();
        version.versionNumber = versions.size() + 1;
        version.content = content;
        version.timestamp = LocalDateTime.now();
        version.author = author;
        version.commitMessage = message;

        versions.add(version);
        currentVersion = version.versionNumber;
    }

    public void rollback(int versionNumber) {
        if (versionNumber > 0 && versionNumber <= versions.size()) {
            currentVersion = versionNumber;
            setContent(versions.get(versionNumber - 1).content);
        }
    }

    public String diff(int v1, int v2) {
        // Implement diff algorithm (Myers, Patience, etc.)
        String content1 = versions.get(v1 - 1).content;
        String content2 = versions.get(v2 - 1).content;
        return calculateDiff(content1, content2);
    }
}
```

**Optimization (Git approach):**
- Store deltas instead of full copies
- Use hash-based content addressing
- Compress old versions
- Garbage collection for unreachable versions

---

### **Q7: How to make file operations thread-safe?**

**Answer:**

**Problem:** Concurrent modifications to directory structure

```java
// Thread 1: addChild("file1")
// Thread 2: removeChild("file2")
// Thread 3: getChild("file1")
// Race conditions!
```

**Solution 1: Synchronized Methods**
```java
public class FileSystemNode {
    private Map<String, FileSystemNode> children;

    public synchronized void addChild(String name, FileSystemNode child) {
        children.put(name, child);
    }

    public synchronized boolean removeChild(String name) {
        return children.remove(name) != null;
    }

    public synchronized FileSystemNode getChild(String name) {
        return children.get(name);
    }
}
```

**Solution 2: ReadWriteLock (Better Performance)**
```java
public class FileSystemNode {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Map<String, FileSystemNode> children;

    public void addChild(String name, FileSystemNode child) {
        lock.writeLock().lock();
        try {
            children.put(name, child);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public FileSystemNode getChild(String name) {
        lock.readLock().lock();
        try {
            return children.get(name);
        } finally {
            lock.readLock().unlock();
        }
    }
}
```

**Solution 3: ConcurrentHashMap (Recommended)**
```java
public class FileSystemNode {
    // Thread-safe without explicit locking!
    private Map<String, FileSystemNode> children = new ConcurrentHashMap<>();

    public void addChild(String name, FileSystemNode child) {
        children.put(name, child);  // Atomic operation
    }

    public boolean removeChild(String name) {
        return children.remove(name) != null;  // Atomic
    }
}
```

**Best Practice:**
- Use `ConcurrentHashMap` for simple operations
- Use `ReadWriteLock` for complex multi-step operations
- Avoid holding locks during I/O operations
- Consider lock-free data structures for high concurrency

---

### **Q8: What are the trade-offs of Composite Pattern?**

**Answer:**

**✅ Advantages:**
1. **Uniform interface** - Treat leaves and composites the same
2. **Easy to add new node types** - Extends without modifying existing code
3. **Natural recursion** - Tree operations are elegant
4. **Simplifies client code** - No type checking needed

**❌ Disadvantages:**
1. **Type safety concerns** - Can add children to leaves (need safeguards)
2. **Memory overhead** - Leaves have unused children map
3. **Performance** - Extra indirection through polymorphism
4. **Over-generalization** - Can make design too abstract if types are very different

**When NOT to use Composite:**
- If operations are too different between leaf and composite
- If performance is critical and polymorphism overhead matters
- If type safety is more important than uniform interface
- If you rarely need to treat objects uniformly

**Alternatives:**
- **Visitor Pattern** - For operations that vary by type
- **Strategy Pattern** - For interchangeable algorithms
- **Simple inheritance** - If composites don't need uniform treatment

---

## **⚠️ Known Limitations & Trade-offs**

1. **No Path Abstraction**
   - Current: Manual path traversal
   - Fix: Add `Path` class with methods like `resolvePath("/home/user/file.txt")`

2. **No Persistence**
   - Current: In-memory only
   - Fix: Add serialization, database storage, or file-based storage

3. **Memory Overhead for Files**
   - Current: Every file has empty children map
   - Fix: Create separate `LeafNode` without children

4. **No Parent Reference**
   - Current: Can't navigate upwards
   - Fix: Add `parent` field in FileSystemNode

5. **No Symbolic Link Support**
   - Current: Only files and directories
   - Fix: Add `SymbolicLink` class (see Extensions)

6. **Not Thread-Safe**
   - Current: Race conditions in concurrent access
   - Fix: Use `ConcurrentHashMap` or `ReadWriteLock`

---

## **📚 Key Takeaways**

**Design Pattern Used:**
- ✅ **Composite Pattern** (the star!) - Tree structures with uniform interface

**When to Use Composite:**
- Tree structures (file systems, org charts, UI components)
- Part-whole hierarchies
- Need to treat individuals and groups uniformly
- Recursive operations are common

**SOLID Principles:**
- ✅ All 5 principles demonstrated

**Core Concepts Mastered:**
- Recursive algorithms on trees
- Polymorphic recursion
- Composite vs Leaf distinction
- Uniform interface design

**Interview Focus Points:**
- Explain Composite Pattern clearly
- Show recursive operations elegance
- Discuss extensions (permissions, symbolic links)
- Demonstrate SOLID principles
- Handle concurrency questions

---

## **🎓 What You Should Master**

Before interview, ensure you can:
1. ✅ Draw the Composite Pattern class diagram
2. ✅ Explain why Composite Pattern is perfect for file systems
3. ✅ Implement recursive operations (size, search, display)
4. ✅ Add new node type (SymbolicLink) in 3 minutes
5. ✅ Handle symbolic link cycles
6. ✅ Discuss distributed file system architecture
7. ✅ Explain RAID systems
8. ✅ Implement thread-safe operations
9. ✅ Compare Composite vs other patterns
10. ✅ Answer all Q&A sections confidently

**Time to master:** 1.5-2 hours of practice

**Difficulty:** ⭐⭐ (Easy-Medium)

**Interview Frequency:** ⭐⭐⭐ (Common - Great pattern demonstration)

**Pro Tip:** This problem is excellent for explaining design patterns in interviews because:
- Everyone understands file systems
- Composite Pattern is very visual
- Easy to extend and discuss trade-offs
- Shows recursive thinking
- Demonstrates SOLID principles naturally

Practice drawing the class diagram and explaining it verbally - that's what interviewers love to see! 🎯
