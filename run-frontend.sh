#!/bin/bash

# RUN FRONTEND

cd /projects/java/cupcake-unicsul

python3 -m http.server 5500 --directory frontend
