#!/bin/sh

docker stop app && docker rm -f app && docker rmi -f app && docker-compose up -d --no-deps web hello-world