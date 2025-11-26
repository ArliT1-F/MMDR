# Hot Reload Guide

This guide explains how MMDR's hot reload system works under the hood and how to configure it for your development workflow.

## Architecture Overview

The hot reload subsystem is composed of three core classes:
- `hotreload.FileWatcher` – watches compiled class directories for file changes.
- `hotreload.HotReloadManager` – reacts to file changes and orchestrates reloads.
- `hotreload.ClassReloader` – defines new versions of classes in a custom class loader.

Optionally, `hotreload.BytecodeTransformer` can transform class bytecode before it is loaded.

## How It Works
1. MMDR configures one or more output directories to watch (typically Gradle or IDE class output paths).
2. `FileWatcher` receives file system events when a `.class` file is created or modified.
3. `HotReloadManager` reads the updated `.class` file, converts its path to a JVM class name, and passes the bytes through `BytecodeTransformer`.
4. `ClassReloader` defines the updated class in a fresh child class loader and tracks the latest version.
5. The MMDR console and other systems resolve and use the latest version when executing user code.

## Configuration Tips
- Point MMDR at your IDE or Gradle build output (e.g. `build/classes/java/main`).
- Exclude large or noisy directories if they generate too many class changes.
- Increase the file watch interval in `mmdr-config.json` if you see performance issues.

## Limitations
- Some structural changes (e.g. changing method signatures used by existing code) may still require a full game restart.
- Static initializers run again when a class is reloaded; avoid heavy work in static blocks.
- Minecraft core classes are typically not safe to reload.