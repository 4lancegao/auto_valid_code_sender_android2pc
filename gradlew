#!/usr/bin/env sh
##############################################################################
##
##  Lightweight Gradle launcher:
##  - If project wrapper jar exists, use standard Gradle wrapper.
##  - If not, fallback to system `gradle`/`./gradlew` command.
##
##############################################################################

APP_HOME="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ -f "$WRAPPER_JAR" ]; then
  if [ -z "$JAVA_HOME" ] ; then
    JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null || true)
  fi
  if [ -x "$JAVA_HOME/bin/java" ]; then
    exec "$JAVA_HOME/bin/java" -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
  fi
fi

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "No Gradle runtime found. Install JDK and Gradle, or add gradle-wrapper.jar under gradle/wrapper/."
exit 1
