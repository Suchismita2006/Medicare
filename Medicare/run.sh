#!/usr/bin/env bash
# ==========================================================
# MEDICARE - Run / Launch Script
# ==========================================================

# Navigate to the script's directory
cd "$(dirname "$0")"

# Detect Java runtime: check installed JDKs in /Library/Java or JAVA_HOME
if [ -d "/Library/Java/JavaVirtualMachines/jdk-26.jdk/Contents/Home/bin" ]; then
    export PATH="/Library/Java/JavaVirtualMachines/jdk-26.jdk/Contents/Home/bin:$PATH"
elif [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    export PATH="$JAVA_HOME/bin:$PATH"
elif ls -d /Library/Java/JavaVirtualMachines/*/Contents/Home/bin 2>/dev/null | head -n 1 | grep -q bin; then
    JDK_BIN=$(ls -d /Library/Java/JavaVirtualMachines/*/Contents/Home/bin 2>/dev/null | head -n 1)
    export PATH="$JDK_BIN:$PATH"
fi

# Run the Main class with classpath including current directory and lib/
java -cp ".:lib/*" Main
