# AIOPE Remote Infrastructure Architecture

## Overview

AIOPE Remote is a dual-transport distributed execution system designed for:
- SSH-based legacy infrastructure control
- HTTP daemon-based modern agent execution
- AI-driven tool orchestration layer

---

## SYSTEM LAYERS

### 1. Control Plane (Android Client)

Components:
- RemoteToolProvider
- RemoteExecutionRouter
- RemoteCapabilityRegistry
- ToolCapabilityFilter

Responsibilities:
- expose tools to AI
- route execution requests
- enforce capability constraints
- select execution backend

---

### 2. Execution Router Layer

Component:
- RemoteExecutionRouter

Responsibilities:
- unify execution API
- abstract SSH vs daemon
- fan-out execution (multi-server)
- future policy routing

---

### 3. Execution Gateways

#### SSH Gateway (Legacy)
- SshSessionManager
- SSHJ transport
- direct shell execution

#### Daemon Gateway (Modern)
- AiopeDaemonClient
- HTTP JSON API
- low-latency execution
- streaming job support

---

### 4. Capability System

Component:
- RemoteCapabilityRegistry
- ToolCapabilityFilter

Responsibilities:
- determine server capabilities
- enable/disable tools dynamically
- future-proof multi-runtime execution

Capabilities:
- ssh
- daemon
- docker
- systemd
- streaming

---

### 5. Remote Tool System

Component:
- RemoteToolProvider

Responsibilities:
- define AI-accessible tools
- enforce structured JSON schema
- delegate execution to router

---

### 6. Server-Side Daemon (AIOPE Agent)

Tech:
- Kotlin + Ktor
- Netty server

Endpoints:
- POST /api/v1/exec
- POST /api/v1/job/start
- GET /api/v1/job/{id}
- POST /api/v1/job/{id}/kill

Capabilities:
- command execution
- job lifecycle management
- streaming stdout/stderr
- process isolation

---

### 7. Job System

Components:
- JobStore
- StreamingExecutor

Features:
- async execution
- persistent in-memory job registry
- real-time stdout/stderr streaming
- killable processes
- status tracking

---

## EXECUTION FLOW

### Simple command:
Tool → Router → Daemon OR SSH → Server

### Background job:
Tool → Router → Daemon → JobStore → StreamingExecutor

---

## DESIGN GOALS

- transport abstraction (SSH vs HTTP daemon)
- AI tool safety via capability filtering
- deterministic execution routing
- scalable remote orchestration
- future multi-node clustering

---

## FUTURE EXTENSIONS (PLUG-IN READY)

- WebSocket streaming layer
- distributed job scheduler
- encrypted command channel
- multi-tenant server control
- AI policy firewall