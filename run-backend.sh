#!/bin/bash

# Use source command to load the environment variables in the current shell
export DB_URL="jdbc:postgresql://localhost:5432/cupcake"
export DB_USERNAME="cupcake_user"

if [[ -z ${DB_PASSWORD} ]]; then
  # Exportar a senha de forma segura:
  read -rsp "Digite a senha do usuário ${DB_USERNAME} no PostgreSQL: " DB_PASSWORD
  echo
  export DB_PASSWORD
fi


export SPRING_PROFILES_ACTIVE=dev


cd /projects/java/cupcake-unicsul

# ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# ou se compilado em jar (./mvnw clean compile), use:

if [ ! -f target/cupcake-0.0.1-SNAPSHOT.jar ]; then
  ./mvnw clean compile
fi

java -jar target/cupcake-0.0.1-SNAPSHOT.jar
# java -jar target/cupcake-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# # RUN FRONTEND
#
# cd /projects/java/cupcake-unicsul
#
# python3 -m http.server 5500 --directory frontend
