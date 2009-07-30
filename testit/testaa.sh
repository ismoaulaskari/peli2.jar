#!/bin/sh

cd ../dist && \
cp ../testit/genhtml/*tnmt .
for TNMT in `ls *tnmt` 
do
  echo "Luetaan $TNMT ja generoidaan ${TNMT}.html"
  java -jar peli2.jar $TNMT headless >"${TNMT}.html"
  diff  "${TNMT}.html" "../testit/genhtml/${TNMT}.html"
done
