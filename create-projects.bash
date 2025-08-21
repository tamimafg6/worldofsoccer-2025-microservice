#!/usr/bin/env bash

spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=match-service \
--package-name=com.worldofsoccer.match \
--groupId=com.worldofsoccer.match \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
match-service

spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=teams-service \
--package-name=com.worldofsoccer.teams \
--groupId=com.worldofsoccer.teams \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
teams-service

spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=league-service \
--package-name=com.worldofsoccer.league \
--groupId=com.worldofsoccer.league \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
league-service

spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=location-service \
--package-name=com.worldofsoccer.location \
--groupId=com.worldofsoccer.location \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
location-service

spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=api-gateway \
--package-name=com.worldofsoccer.apigateway \
--groupId=com.worldofsoccer.apigateway \
--dependencies=web,webflux,validation,hateoas \
--version=1.0.0-SNAPSHOT \
api-gateway
