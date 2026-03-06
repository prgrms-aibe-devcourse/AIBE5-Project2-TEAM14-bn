# Makefile for Docker Compose operations and convenient Java tasks

DOCKER_COMPOSE = docker-compose -f docker-compose.yml
JDBC_JAR ?= mysql-connector-java-8.0.xx.jar

.PHONY: up down restart logs exec build run dashboard

# compile Java sources into out/ directory
build:
	@echo "Compiling Java sources..."
	mkdir -p out
	javac -cp "$(TUI_JAR)" -d out \
		src/main/java/com/aiegoo/comicrental/*.java \
		src/main/java/com/aiegoo/comicrental/dao/*.java \
		src/main/java/com/aiegoo/comicrental/util/*.java \
		src/main/java/com/aiegoo/comicrental/tui/*.java

# run the CLI (requires JDBC jar on classpath)
run: build
	@echo "Launching CLI" \
	&& java -cp out:$(JDBC_JAR) com.aiegoo.comicrental.Main

# run the simple menu dashboard
dashboard: build
	@echo "Launching dashboard" \
	&& java -cp out:$(JDBC_JAR) com.aiegoo.comicrental.Main dashboard

# launch the richer text UI (requires JDBC + TUI jars)
tui: build
	@echo "Launching Lanterna-based TUI" \
	&& java -cp out:$(JDBC_JAR):$(TUI_JAR) com.aiegoo.comicrental.tui.Tui

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

exec:
	# open an interactive shell in the mysql service container
	$(DOCKER_COMPOSE) exec mysql bash
