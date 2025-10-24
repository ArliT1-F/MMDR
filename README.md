# MMDR - Minecraft Mod Development Runtime

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.4-green.svg)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-0.15.3-orange.svg)](https://fabricmc.net/)

**A revolutionary development environment for Minecraft mod developers.**

MMDR eliminates the painful edit-compile-restart cycle by providing hot reload capabilities, an interactive debug console, visual inspection tools, and automatic test generation. Stop wasting time waiting for restarts—see your changes instantly!

---

## ✨ Features

### 🔥 Hot Reload System
- **Instant Code Updates**: Modify Java code and see changes immediately without restarting Minecraft
- **Intelligent Class Reloading**: Automatically detects and reloads changed classes
- **State Preservation**: Maintains game state during reloads when possible
- **File Watching**: Real-time monitoring of your source files

### 💻 Interactive Debug Console
- **REPL Environment**: Execute Java/Groovy code directly in-game
- **Game Object Access**: Direct access to Minecraft internals (`mc`, world, player, etc.)
- **Command History**: Navigate through previous commands with arrow keys
- **Auto-completion**: Smart suggestions for variables and methods

### 🔍 Visual Inspector
- **Block & Entity Inspection**: Hover over anything to see detailed information
- **NBT Data Viewer**: Hierarchical display of NBT data with syntax highlighting
- **Packet Monitor**: Real-time visualization of network traffic
- **Performance Metrics**: FPS, memory usage, entity counts, and more

### 🧪 Automatic Test Harness
- **Action Recording**: Record your gameplay as you develop
- **Test Generation**: Automatically convert recordings to JUnit tests
- **Mock Environment**: Lightweight testing without full Minecraft instance
- **Multiple Formats**: Export as JUnit, TestNG, or Cucumber features

---

## Installation

### Requirements
    - Minecraft 1.20.4
    - Fabric Loader 0.15.3+
    - Fabric API
    - Java 17+


### Steps
1. **Download MMDR**
```bash
# Clone the repository
git clone https://github.com/ArliT1-F/MMDR.git
cd MMDR
```
2. **Build the mod**
```bash
./gradlew build
```
3. **Install**
    - Copy `build/libs/mmdr-1.0.0.jar` to your `.minecraft/mods` folder
    - Launch Minecraft with Fabric

4. **Verify Installation**
    - Press ``` ` ```(backtick) to open the debug console
    - You should see the MMDR welcome message


---
## Quick Start
### Hot Reload in Action
1. **Start Minecraft** with MMDR installed
2. **Make changes** to your mod's code in your IDE
3. **Save the file** - MMDR automatically detects and reloads it
4. **See changes instantly** in-game without restarting!
```java
// Example: Modify a method in your mod
public void onUse() {
    // Old code: player.sendMessage("Hello!");
    player.sendMessage("Hello from hot reload!");
    // Save the file and it updates instantly!
}
```
### Using the Debug Console
Press ``` ` ``` to open the onsole and try these commands:
```java
// Get the player's position
mc.player.getPos()

// Teleport the player
mc.player.setPosition(0, 100,0)

// Spawn an item
mc.player.giveItemStack(new ItemStack(Items.DIAMOND, 64))

// Change time of day
mc.world.setTimeofDay(6000)

// Execute custom code
for (int i = 0; i < 10; i++) {
    println("Loop iteration: " + i)
}
```

### Using the Inspector
1. Press **F3** (or configured key) to toggle the inspector
2. **Look at any block or entity** - information appears on screen
3. Press **F4** to toggle NBT data view
4. Press **F5** to show packet monitor
5. Press **F7** to display performance metrics


### Recording and Generating Tests
1. Press **F9** to start recording your actions
2. Play the game normally (break blocks, use items, etc.)
3. Press **F9** again to stop recording
4. Open console and run:
    ```java
    MMDR.getTestHarness().generateTestCodeFromCurrent("MyGeneratedTest")
    ```
5. Find the generated test in `mmdr_recordings/MyGeneratedTest.java`

## Documentation
- Getting started Guide - Detailed setup and first steps
- Hot reload Guide - Complete hot reload system documentation
- Console Guide - Debug console commands and scripting
- Inspector Guide - Using the visual inspector
API Reference - Complete API documentation

## Configuration
MMDR creates a configuration file at `.minecraft/config/mmdr-config.json`.

### Example Configuration
```json
{
    "hotReloadEnabled": true,
    "autoCompileEnabled": false,
    "fileWatchInterval": 1000,
    "consoleEnabled": true,
    "inspectorEnabled": true,
    "debugLogging": false
}
```
### Key Settings

| ***Setting***         | ***Description***                          | ***Default*** |
| :---------------------|:-------------------------------------------|:--------------|
| `hotReloadEnabled`    | Enable/disable hot reload system           | `true`        |
| `autoCompileEnabled`  | Automatically compile changed source files | `false`       |
| `fileWatchInterval`   | File check interval in milliseconds        | `1000`        |
| `consoleEnabled`      | Enable debug console                       | `true`        |
| `inspectorEnabled`    | Enable visual inspector                    | `true`        |
| `debugLogging`        | Enable verbose debug logs                  | `false`       |


See API Reference for all options

## Keybindings
| ***Key***           | ***Action***                |
|:--------------------|----------------------------:|
| ``` ` ``` (Backtick) | Toggle debug console        |
| `F3`                | Cycle inspector mode        |
| `F4`                | Toggle NBT display          |
| `F5`                | Toggle packet monitor       |
| `F6`                | Toggle event listeners      |
| `F7`                | Toggle performance metrics  |
| `F9`                | Start/stop action recording |