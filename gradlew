#!/bin/sh
# Gradle start script bootstrap — CI / local will download wrapper jar if missing
DIR="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_JAR="$DIR/gradle/wrapper/gradle-wrapper.jar"
PROPS="$DIR/gradle/wrapper/gradle-wrapper.properties"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "gradle-wrapper.jar missing — downloading..."
  mkdir -p "$DIR/gradle/wrapper"
  # Use gradle to generate wrapper if available, else curl known jar is fragile;
  # recommend: run `gradle wrapper` once locally or let GitHub Actions cache it.
  if command -v gradle >/dev/null 2>&1; then
    (cd "$DIR" && gradle wrapper --gradle-version 8.5)
  else
    echo "Install Gradle or open project in Android Studio to generate wrapper."
    exit 1
  fi
fi

exec java -jar "$WRAPPER_JAR" "$@"
