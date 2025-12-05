# Low Level Design Problems 🏗️

A collection of **12 comprehensive LLD interview problems** with clean, runnable implementations demonstrating various design patterns and OOP principles.

---

## **📚 Problem List**

| # | Problem | Design Patterns | Key Concepts |
|---|---------|----------------|--------------|
| 01 | **Tic Tac Toe** | State, Strategy | Game logic, Win detection |
| 02 | **Chess Game** | State, Factory, Strategy | Complex rules, Piece movement |
| 03 | **Snake and Food Game** | State, Observer | Real-time game, Collision detection |
| 04 | **Parking Lot** | Strategy, Factory | Resource allocation, Pricing |
| 05 | **Elevator System** | State, Observer | Scheduling, Optimization |
| 06 | **Inventory Management** | Factory, Strategy | Stock tracking, CRUD operations |
| 07 | **Car Rental** | Strategy, Factory | Booking, Pricing, Availability |
| 08 | **Vending Machine** | State | State transitions, Payment handling |
| 09 | **File System** | Composite | Tree structure, Path navigation |
| 10 | **Logging System** | Singleton, Observer | Log levels, Multiple outputs |
| 11 | **Splitwise** | Strategy, Factory | Debt simplification, Expense splitting |
| 12 | **ATM Machine** | State, Chain of Responsibility | Cash dispensing, Transaction handling |

---

## **🚀 How to Run Each Project**

### **Method 1: Individual Compilation (Recommended)**

```bash
cd /Users/raghurrs/.leetcode/LLD_Problems

# Navigate to specific problem
cd 01_Design_Tic_Tac_Toe/src

# Compile
javac Main.java

# Run
java Main
```

### **Method 2: Using Helper Script**

```bash
# Run specific problem
./run.sh 01

# Run all tests
./run.sh all
```

---

## **📁 Common Project Structure**

```
XX_Design_Problem_Name/
├── src/
│   ├── Main.java                    # Entry point
│   ├── CommonEnum/                  # Enums
│   ├── Controller/                  # Main logic controllers
│   ├── [Pattern]Pattern/            # Design pattern implementations
│   │   ├── Interface.java           # Pattern interface
│   │   └── Concrete*/               # Concrete implementations
│   └── Utility/                     # Helper classes
└── README.md (optional)
```

---

## **🎨 Design Patterns Used**

### **1. State Pattern** 🔄
**Used in:** Vending Machine, ATM, Elevator, Games
```
Different behavior based on current state
States: Idle → Processing → Complete
```

### **2. Strategy Pattern** 💡
**Used in:** Parking (pricing), Payment methods, Player strategies
```
Interchangeable algorithms
Example: CashPayment vs CreditCardPayment
```

### **3. Factory Pattern** 🏭
**Used in:** Vehicle creation, Split types, Pieces
```
Object creation logic encapsulated
Example: VehicleFactory.createVehicle(type)
```

### **4. Observer Pattern** 👁️
**Used in:** Logging, Elevator notifications
```
Publish-subscribe mechanism
Observers notified on state changes
```

### **5. Composite Pattern** 🌲
**Used in:** File System
```
Tree structure (files/directories)
Uniform interface for leaf and composite nodes
```

### **6. Singleton Pattern** 🎯
**Used in:** Logging System, System-wide resources
```
Single instance throughout application
Global access point
```

---

## **🎓 Learning Path**

### **Beginner Level:**
1. ✅ **Tic Tac Toe** - Simple state management
2. ✅ **Logging System** - Singleton, basic design
3. ✅ **File System** - Composite pattern basics

### **Intermediate Level:**
4. ✅ **Parking Lot** - Multiple patterns, realistic system
5. ✅ **Vending Machine** - State pattern deep dive
6. ✅ **Splitwise** - Complex algorithms with design

### **Advanced Level:**
7. ✅ **Chess Game** - Complex rules, multiple patterns
8. ✅ **Elevator System** - Scheduling algorithms
9. ✅ **Car Rental** - End-to-end booking system

---

## **🔧 Troubleshooting**

### **Compilation Errors:**

**Error:** `package does not exist`
```bash
# Solution: Compile from src/ directory
cd project/src
javac Main.java
```

**Error:** `class not found`
```bash
# Solution: Run from src/ directory
cd project/src
java Main
```

### **Runtime Issues:**

**Interactive programs** (Tic Tac Toe, Chess):
- Require user input
- Use Scanner for keyboard input
- Press Ctrl+C to exit

**Non-interactive programs** (Parking Lot, Splitwise):
- Run predefined scenarios
- Output to console
- Complete automatically

---

## **📝 Next Steps**

1. ✅ **Copied** - All 12 LLD problems
2. ✅ **Verified** - Compilation works
3. ⏳ **Study** - Go through each one
4. ⏳ **Understand** - Learn design patterns
5. ⏳ **Enhance** - Add your improvements

---

## **💡 Study Approach**

For each problem:
1. **Run it** - See the behavior
2. **Read Main.java** - Understand workflow
3. **Trace execution** - Follow state transitions
4. **Identify patterns** - Spot design patterns used
5. **Diagram it** - Draw class relationships
6. **Improve it** - Add features or optimize

---

## **🎯 Interview Preparation**

These problems cover:
- ✅ SOLID principles
- ✅ Design patterns (6+ patterns)
- ✅ Object-oriented design
- ✅ Real-world systems
- ✅ Code organization
- ✅ Extensibility

**Ready for interviews!** 🚀
