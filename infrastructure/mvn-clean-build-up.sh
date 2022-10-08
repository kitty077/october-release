#!/bin/sh

chmod a+x ./stop-remove_images.sh
./stop-remove_images.sh
chmod a+x ./show_containers_images.sh
./show_containers_images.sh

cd ../

mvn dependency:tree
mvn clean package
