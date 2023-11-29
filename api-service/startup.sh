#!/bin/bash
set -e  # Exit immediately if some command (simple or compound) returns a non-zero status

# ++++++++++++++++
# UTIL FUNCTIONS
# ++++++++++++++++

function print_help() {
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  up      Startup the API service and the frontend application"
    echo "  down    Stop all the services"
    echo ""
    echo "Pre-requisites:"
    echo "  - Unix-like operating system (macOS or Linux)"
    echo "  - Docker"
    echo "  - npm"
}

check_prerequisites() {
    if [[ "$OSTYPE" != "linux-gnu"* && "$OSTYPE" != "darwin"* ]]; then # Check if the host is a Unix-like operating system
        echo "Error: this script requires a Unix-like operating system (macOS or Linux)."
        exit 1
    elif ! command -v docker &> /dev/null; then # Check if Docker is installed
        echo "Error: Docker is not installed. Please install Docker before running this script."
        exit 1
    elif ! command -v npm &> /dev/null; then # Check if npm is installed
        echo "Error: npm is not installed. Please install npm before running this script."
        exit 1
    fi
}

# Check if the container is running
is_container_running() {
    local container_name_or_id=$1
    docker ps --format "{{.Names}} {{.Status}}" | grep "$container_name_or_id" | grep "Up" &> /dev/null
}

# Launch a container and wait for it to be ready
launch_container() {
  container_name=$1
  echo "Launching $container_name"
  docker-compose up -d --build $container_name
  echo "Check for $container_name to be ready..."
  while ! is_container_running $container_name; do
    sleep 1
  done
}

copy_keys() {
  target_folder=$1
  if [ ! -d "$target_folder/secrets" ]; then
    mkdir -p "$target_folder/secrets"
    echo "Directory "$target_folder/secrets" created."
  fi
  echo "Copying test keys in $target_folder/secrets"
  cp -rf ./.test_secrets/* $target_folder/secrets
}


# ++++++++++++++++
# MAIN
# +++++++++++++++
export $(cat ../config.env | xargs)
export ARTIFACTS_DIR=$(pwd)/../$ARTIFACTS_DIR

# Startup and configure the API-server
startup() {
  check_prerequisites
  docker-compose down

  # Launch the containers
  echo "STEP 1: Copying test keys in the projects"
  copy_keys api
  copy_keys auth
  copy_keys common

  echo "STEP 2: Launching common registry and publish dependencies"
  launch_container verdaccio
  sleep 5

  # shellcheck disable=SC2164
  cd common
  # Check if an user is already logged in verdaccio, if not, proceed to login
  if [ ! -f ~/.npmrc ]; then
      echo "No user logged in verdaccio, logging in..."
      npm login --registry http://localhost:4873
  fi
  npm unpublish --force
  npm install
  npm run build
  npm publish --registry http://localhost:4873

  cd ..

  echo "STEP 3: Launching the mongodb and redis containers"
  if [ ! -d "dbdata" ]; then
      mkdir -p "dbdata"
      echo "Directory 'dbdata' created."
  fi
  launch_container mongodb

  if [ ! -d "cache" ]; then
      mkdir -p "cache"
      echo "Directory 'cache' created."
  fi
  launch_container redis


  sleep 5
  echo "STEP 4: Launching api and auth containers"
  launch_container api-server
  launch_container auth-server
}

# Shutdown the API-server
shutdown() {
    echo "Stopping all services..."
    docker compose down
}

if [ "$1" == "up" ]; then
    startup
elif [ "$1" == "down" ]; then
    shutdown
else
    print_help
fi






