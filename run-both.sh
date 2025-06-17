#!/bin/bash

# Function to kill process using a specific port
kill_process_on_port() {
  local port=$1
  echo "Checking if port $port is in use..."

  # For macOS (which you appear to be using based on your file paths)
  if command -v lsof &> /dev/null; then
    local pid=$(lsof -i :$port -t)
    if [ -n "$pid" ]; then
      echo "Killing process using port $port (PID: $pid)"
      kill -9 $pid
      sleep 1
    else
      echo "Port $port is not in use"
    fi
  # For Linux (as a fallback)
  elif command -v fuser &> /dev/null; then
    if fuser $port/tcp &> /dev/null; then
      echo "Killing process using port $port"
      fuser -k $port/tcp
      sleep 1
    else
      echo "Port $port is not in use"
    fi
  else
    echo "Neither lsof nor fuser commands found. Cannot check for processes on ports."
  fi
}

# Kill processes on the ports used by our applications
kill_process_on_port 8081  # Platform Thread server port
kill_process_on_port 8082  # Virtual Thread server port

echo "Starting servers..."

# Run platform-thread in the background
(cd platform-thread && ../mvnw spring-boot:run) &

# Run virtual-thread in the foreground
cd virtual-thread && ../mvnw spring-boot:run