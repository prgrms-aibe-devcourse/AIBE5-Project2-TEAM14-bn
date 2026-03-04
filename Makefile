# Makefile for Docker Compose operations

DOCKER_COMPOSE = docker-compose -f docker-compose.yml

.PHONY: up down restart logs

up:
	@echo "Starting services..."
	$(DOCKER_COMPOSE) up -d

down:
	@echo "Stopping and removing containers..."
	$(DOCKER_COMPOSE) down -v

restart: down up
	@echo "Restarted all services."

logs:
	$(DOCKER_COMPOSE) logs -f
