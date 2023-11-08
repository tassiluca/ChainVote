#!/bin/bash

# ++++++++++++++++
# UTIL FUNCTIONS
# ++++++++++++++++

# Check if the container is running
is_container_running() {
    local container_name_or_id=$1
    docker ps --format "{{.Names}} {{.Status}}" | grep "$container_name_or_id" | grep "Up" &> /dev/null
}

# Launch a container and wait for it to be ready
launch_container() {
  container_name=$1
  echo "Launching $container_name"
  docker-compose up -d $container_name
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
    npm adduser --registry http://localhost:4873
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



