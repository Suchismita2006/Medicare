#!/usr/bin/env bash
# ==========================================================
# MEDICARE - Build / Compile Script
# ==========================================================

# Navigate to the script's directory
cd "$(dirname "$0")"

# Detect Java compiler: check installed JDKs in /Library/Java or JAVA_HOME
if [ -d "/Library/Java/JavaVirtualMachines/jdk-26.jdk/Contents/Home/bin" ]; then
    export PATH="/Library/Java/JavaVirtualMachines/jdk-26.jdk/Contents/Home/bin:$PATH"
elif [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
    export PATH="$JAVA_HOME/bin:$PATH"
elif ls -d /Library/Java/JavaVirtualMachines/*/Contents/Home/bin 2>/dev/null | head -n 1 | grep -q bin; then
    JDK_BIN=$(ls -d /Library/Java/JavaVirtualMachines/*/Contents/Home/bin 2>/dev/null | head -n 1)
    export PATH="$JDK_BIN:$PATH"
fi

echo "========================================="
echo "   Compiling MEDICARE Java Application   "
echo "========================================="

# Compile all .java files including JAR dependencies in lib/
javac -cp ".:lib/*" *.java

if [ $? -eq 0 ]; then
    echo "[✓] Compilation successful! All .class files generated."
    echo "To run the system, execute: ./run.sh"
else
    echo "[X] Compilation failed. Please check the errors above."
    exit 1
fi
