#!/bin/bash

TARGET_DIR=/home/qufl/qufl/telechips4.4/new/jb1506/packages/apps/LuyuanIpod/

SOURCES="Android.mk AndroidManifest.xml assets proguard-project.txt project.properties res src"

rsync -avz --delete $SOURCES $TARGET_DIR
