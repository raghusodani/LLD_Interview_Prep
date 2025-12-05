# LLD Problems - Quick Reference Index 📖

All 12 projects are **compiled and runnable!** ✅

---

## **🎮 Interactive Programs (Need User Input)**

### **01. Tic Tac Toe**
```bash
./run.sh 01
```
**Features:**
- 3x3 board game
- Two players (X and O)
- Turn-based gameplay
- Win detection

**Patterns:** State Pattern, Strategy Pattern

---

### **02. Chess Game**
```bash
./run.sh 02
```
**Features:**
- Full chess board (8x8)
- All piece movements
- Turn management
- Check/Checkmate detection

**Patterns:** State Pattern, Factory Pattern, Strategy Pattern

---

## **🏢 System Design Programs (Automated Demos)**

### **03. Snake and Food Game**
```bash
./run.sh 03
```
**Features:**
- Snake movement simulation
- Food generation
- Collision detection
- Score tracking

**Patterns:** State Pattern, Observer Pattern

---

### **04. Parking Lot** ⭐
```bash
./run.sh 04
```
**Features:**
- Multi-level parking
- Vehicle types (Car, Bike, Other)
- Pricing strategies (Hourly, Premium)
- Payment methods (Cash, Card)
- Entry/Exit management

**Patterns:** Strategy Pattern, Factory Pattern

**Key Classes:**
- `ParkingLot` - Main controller
- `ParkingSpot` - Abstract spot class
- `Vehicle` - Factory pattern
- `ParkingFeeStrategy` - Pricing strategies
- `PaymentStrategy` - Payment methods

---

### **05. Elevator System**
```bash
./run.sh 05
```
**Features:**
- Multiple elevators
- Request handling
- Floor management
- Direction logic
- Scheduling algorithm

**Patterns:** State Pattern, Observer Pattern

---

### **06. Inventory Management**
```bash
./run.sh 06
```
**Features:**
- Product catalog
- Stock management
- Low stock alerts
- CRUD operations

**Patterns:** Factory Pattern, Strategy Pattern

---

### **07. Car Rental**
```bash
./run.sh 07
```
**Features:**
- Vehicle booking
- Availability checking
- Pricing calculation
- Reservation management
- Return processing

**Patterns:** Strategy Pattern, Factory Pattern

---

### **08. Vending Machine** ⭐
```bash
./run.sh 08
```
**Features:**
- Coin insertion
- Product selection
- Change calculation
- Inventory management
- State transitions

**Patterns:** State Pattern

**States:**
- IdleState → HasMoneyState → SelectionState → DispenseState

---

### **09. File System**
```bash
./run.sh 09
```
**Features:**
- Hierarchical structure
- File/Directory operations
- Path navigation
- Size calculation

**Patterns:** Composite Pattern

**Key Concepts:**
- Tree structure
- Recursive operations
- Uniform interface for files/folders

---

### **10. Logging System** ⭐
```bash
./run.sh 10
```
**Features:**
- Multiple log levels (DEBUG, INFO, WARN, ERROR)
- Multiple outputs (Console, File)
- Singleton logger
- Thread-safe logging

**Patterns:** Singleton Pattern, Observer Pattern

---

### **11. Splitwise** ⭐
```bash
./run.sh 11
```
**Features:**
- Expense sharing
- Equal/Exact/Percentage splits
- Debt simplification
- Balance calculation
- User management

**Patterns:** Strategy Pattern, Factory Pattern

**Key Classes:**
- `ExpenseService` - Main controller
- `Split` - Split interface
- `EqualSplit`, `ExactSplit`, `PercentageSplit` - Strategies
- `DebtSimplifier` - Balance optimization

---

### **12. ATM Machine**
```bash
./run.sh 12
```
**Features:**
- Cash withdrawal
- Denomination handling
- Balance checking
- PIN validation
- Transaction history

**Patterns:** State Pattern, Chain of Responsibility

---

## **📊 Quick Compilation Check**

```bash
# Test all projects compile
./run.sh all

# Result: ✅ 12/12 succeeded
```

---

## **🎯 Recommended Study Order**

### **Week 1: Fundamentals**
1. **Logging System** - Singleton pattern
2. **File System** - Composite pattern
3. **Tic Tac Toe** - Basic State pattern

### **Week 2: Core Patterns**
4. **Vending Machine** - Advanced State pattern
5. **Parking Lot** - Strategy + Factory
6. **Splitwise** - Complex algorithms

### **Week 3: Complex Systems**
7. **Elevator System** - Scheduling
8. **Car Rental** - Booking system
9. **ATM Machine** - Chain of Responsibility

### **Week 4: Games**
10. **Snake Game** - Real-time logic
11. **Chess** - Complex rules
12. **Inventory** - CRUD operations

---

## **💡 Quick Tips**

### **To Run Non-Interactive:**
```bash
cd LLD_Problems/04_Design_Parking_Lot/src
javac Main.java
java Main
# Runs automatically, no input needed
```

### **To Run Interactive:**
```bash
cd LLD_Problems/01_Design_Tic_Tac_Toe/src
javac Main.java
java Main
# Enter row/column when prompted
# Example: 0 0 (top-left corner)
```

### **To Study Code:**
```bash
# View structure
tree 04_Design_Parking_Lot/src

# Read Main.java first
cat 04_Design_Parking_Lot/src/Main.java
```

---

## **🔥 Most Interview-Relevant**

### **Top 5 for Interviews:**
1. ⭐⭐⭐ **Parking Lot** - Most common!
2. ⭐⭐⭐ **Splitwise** - Complex algorithms
3. ⭐⭐⭐ **Elevator System** - Scheduling
4. ⭐⭐ **Vending Machine** - State pattern
5. ⭐⭐ **ATM Machine** - State + Chain of Responsibility

---

## **✅ Setup Complete!**

- ✅ 12 LLD problems copied
- ✅ All compile successfully
- ✅ Helper script created
- ✅ Documentation ready
- ✅ Ready to study!

**Next:** Pick any problem number and run `./run.sh <number>` to start learning! 🚀
