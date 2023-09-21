#!/bin/bash
#
# Utilities functions

set -e  # Exit immediately if some command (simple or compound) returns a non-zero status

function in_array() {
    local to_search="$1"
    local array=("${@:2}")
    for element in "${array[@]}"; do
        if [ "$element" = "$to_search" ]; then
            return 0
        fi
    done
    return 1
}
