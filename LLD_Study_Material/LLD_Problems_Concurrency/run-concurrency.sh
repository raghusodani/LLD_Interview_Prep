#!/bin/bash

# Helper script to compile and run LLD Concurrency problems

BASE_DIR="/Users/raghurrs/.leetcode/LLD_Problems_Concurrency"

get_problem_name() {
    case "$1" in
        "01") echo "01_Design_Movie_Ticket_Booking_System" ;;
        "02") echo "02_Design_Pub_Sub_Model_Kafka" ;;
        "03") echo "03_Design_Cache_System" ;;
        "04") echo "04_Design_Rate_Limiter" ;;
        *) echo "" ;;
    esac
}

compile_and_run() {
    local num=$1
    local project=$(get_problem_name "$num")

    if [ -z "$project" ]; then
        echo "❌ Invalid problem number: $num"
        echo "Usage: ./run-concurrency.sh [01-04] or 'all'"
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
    echo "🏗️  Compiling All Concurrency Problems"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

    local success=0
    local failed=0

    for num in "01" "02" "03" "04"; do
        local project=$(get_problem_name "$num")

        echo ""
        echo "📦 [$num/04] Compiling: $project"

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
    echo "📚 Available Concurrency Problems"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""

    for num in "01" "02" "03" "04"; do
        local project=$(get_problem_name "$num")
        echo "  $num → $project"
    done

    echo ""
    echo "Usage: ./run-concurrency.sh <number>"
    echo "Example: ./run-concurrency.sh 04"
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
    01|02|03|04)
        compile_and_run "$1"
        ;;
    *)
        echo "❌ Invalid argument: $1"
        list_problems
        exit 1
        ;;
esac
