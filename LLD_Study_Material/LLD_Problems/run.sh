#!/bin/bash

# Helper script to compile and run LLD problems

BASE_DIR="/Users/raghurrs/.leetcode/LLD_Study_Material/LLD_Problems"

# Problem mapping (using strings to avoid octal issues)
get_problem_name() {
    case "$1" in
        "01") echo "01_Design_Tic_Tac_Toe" ;;
        "02") echo "02_Design_Chess_Game" ;;
        "03") echo "03_Design_Snake_and_Food_Game" ;;
        "04") echo "04_Design_Parking_Lot" ;;
        "05") echo "05_Design_Elevator_System" ;;
        "06") echo "06_Design_Inventory_Management_System" ;;
        "07") echo "07_Design Car rental" ;;
        "08") echo "08_Design_Vending_Machine" ;;
        "09") echo "09_Design File System" ;;
        "10") echo "10_Design_Logging_System" ;;
        "11") echo "11_Design_Splitwise" ;;
        "12") echo "12_Design_ATM_Machine" ;;
        "13") echo "13_Design_Fantasy_Sports" ;;
        "14") echo "14_Design_Job_Scheduler" ;;
        "15") echo "15_Design_Cab_Booking" ;;
        "16") echo "16_Design_Social_Media_Feed" ;;
        "17") echo "17_Design_Smart_Locker" ;;
        *) echo "" ;;
    esac
}

compile_and_run() {
    local num=$1
    local project=$(get_problem_name "$num")

    if [ -z "$project" ]; then
        echo "❌ Invalid problem number: $num"
        echo "Usage: ./run.sh [01-17] or 'all'"
        return 1
    fi

    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "🚀 Running: $project"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

    cd "$BASE_DIR/$project/src" || {
        echo "❌ Failed to navigate to $project/src"
        return 1
    }

    # Compile
    echo "📦 Compiling..."
    if javac Main.java 2>&1 | grep -q "error:"; then
        echo "❌ Compilation failed!"
        javac Main.java
        return 1
    fi

    echo "✅ Compilation successful!"
    echo ""
    echo "▶️  Running..."
    echo ""

    # Run
    java Main

    echo ""
    echo "✅ Execution complete!"
}

compile_all() {
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "🏗️  Compiling All LLD Problems"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

    local success=0
    local failed=0

    for num in "01" "02" "03" "04" "05" "06" "07" "08" "09" "10" "11" "12" "13" "14" "15" "16" "17"; do
        local project=$(get_problem_name "$num")

        echo ""
        echo "📦 [$num/17] Compiling: $project"

        cd "$BASE_DIR/$project/src" || {
            echo "   ❌ Directory not found"
            failed=$((failed + 1))
            continue
        }

        if javac Main.java 2>/dev/null; then
            echo "   ✅ Success"
            success=$((success + 1))
        else
            echo "   ❌ Failed"
            failed=$((failed + 1))
        fi
    done

    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "📊 Summary: ✅ $success succeeded, ❌ $failed failed"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
}

list_problems() {
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "📚 Available LLD Problems"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""

    for num in "01" "02" "03" "04" "05" "06" "07" "08" "09" "10" "11" "12" "13" "14" "15" "16" "17"; do
        local project=$(get_problem_name "$num")
        echo "  $num → $project"
    done

    echo ""
    echo "Usage: ./run.sh <number>"
    echo "Example: ./run.sh 04"
}

# Main script
if [ $# -eq 0 ]; then
    list_problems
    exit 0
fi

case "$1" in
    all)
        compile_all
        ;;
    list)
        list_problems
        ;;
    01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17)
        compile_and_run "$1"
        ;;
    *)
        echo "❌ Invalid argument: $1"
        list_problems
        exit 1
        ;;
esac
