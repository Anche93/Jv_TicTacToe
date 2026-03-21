#!/bin/bash

UUID_FILE="$(pwd)/.game.uuid"

if [ $# -eq 0 ]; then
  echo "Некорректный ввод. Нужно ввести параметры:"
  echo "create - создать игру"
  echo "get - получить данные об игре"
  echo "2 числа от 0 до 2, при условии, что игра уже создана!"
  exit 1
fi

if [[ $1 == "create" ]]; then
  CREATE_INFO=$(curl -X POST http://localhost:8080/game/create)
  UUID=$(echo "$CREATE_INFO" | jq -r '.uuid')
  echo "$UUID" > "$UUID_FILE"
  exit 0
fi

UUID=$(cat "$UUID_FILE")

if [[ $1 == "get" ]]; then
  curl "http://localhost:8080/game/$UUID"
  exit 0
fi

ROW=$1
COL=$2

if [[ -n "$UUID" ]]; then
  curl -X POST "http://localhost:8080/game/$UUID" \
    -H "Content-Type: application/json" \
    -d "{\"row\": $ROW, \"col\": $COL}"
    exit 0
fi
