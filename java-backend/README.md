# Legacy Monolith - Kept as Reference During Migration

This module is the original monolithic Spring Boot application.
It is **no longer part of the Maven multi-module build**.

## New Architecture

See the following modules for the microservice architecture:
- `novaflow-common/` — Shared DDD kernel
- `novaflow-auth-service/` — Authentication service (port 8081)
- `novaflow-video-service/` — Video processing service (port 8082)
- `novaflow-recommendation-service/` — AI recommendation service (port 8083)
- `novaflow-feedback-service/` — User feedback service (port 8084)
- `novaflow-gateway/` — API Gateway (port 8080)

## Design Document

See `docs/superpowers/specs/2026-05-27-spring-cloud-microservices-design.md`
