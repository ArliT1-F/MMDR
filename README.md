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


See [API Reference](docs/API_REFERENCE.md) for all options

## Keybindings
| ***Key***           | ***Action***                |
|:-------------------:|:---------------------------:|
| ``` ` ```(Backtick) | Toggle debug console        |
| `F3`                | Cycle inspector mode        |
| `F4`                | Toggle NBT display          |
| `F5`                | Toggle packet monitor       |
| `F6`                | Toggle event listeners      |
| `F7`                | Toggle performance metrics  |
| `F9`                | Start/stop action recording |

*All keybindings are configurable in Minecraft's controls menu.*

## 🛠️ Development
### Building from Source
```bash
git clone https://github.com/ArliT1-F/MMDR.git
cd MMDR
./gradlew build
```
### Running Tests
```bash
./gradlew test
```
### Development Setup
1. Import the project into IntelliJ IDEA or Eclipse
2. Run `./gradlew genSources` to generate Minecraft sources
3. Use the Fabric run configurations to launch the game
### Project Structure
```text
MMDR/
├── src/main/java/com/mmdr/
│   ├── MMDR.java              # Main mod class
│   ├── hotreload/             # Hot reload system
│   ├── console/               # Debug console
│   ├── inspector/             # Visual inspector
│   ├── testing/               # Test harness
│   └── util/                  # Utilities
├── docs/                      # Documentation
└── build.gradle               # Build configuration
```
## 🤝 Contributing
We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### How to Contribute
1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request
### Areas We Need Help
- [ ] Adding support for Forge (currently Fabric-only)
- [ ] Improving bytecode transformation for better hot reload
- [ ] Adding more test generation templates
- [ ] Expanding the inspector with more data visualizations
- [ ] Writing additional documentation and tutorials
### 📊 Performance Impact
MMDR is designed to have minimal performance impact when features are not actively used:

- **Hot Reload:** ~1-2% CPU overhead when watching files
- **Console:** No overhead when closed
- **Inspector:** ~0.5 FPS drop when enabled
- **Test Recording:** ~1-2% overhead when recording

All features can be individually disabled in the configuration.

## 🐛 Troubleshooting
### Hot Reload Not Working
- Ensure your IDE is set to auto-compile on save
- Check that MMDR is watching the correct directories
- Look for errors in the game log (`logs/latest.log`)
- Verify the class is not excluded in `mmdr-config.json`
### Console Won't Open
- Check keybinding conflicts in Minecraft settings
- Ensure `consoleEnabled` is `true` in config
- Try restarting Minecraft
### Inspector Shows Wrong Data
- Update MMDR to the latest version
- Clear the config file and let MMDR regenerate it
- Report the issue on GitHub with screenshots

See [Troubleshooting Guide](docs/GETTING_STARTED.md#troubleshooting) for more help.

## 📜 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments
Fabric Team - For the excellent modding framework
ASM - Bytecode manipulation library
Groovy - Scripting engine for the REPL
Minecraft Modding Community - For inspiration and support
## 📞 Contact & Support
Issues: GitHub Issues
Discussions: GitHub Discussions
Discord: Join our Discord
## 🗺️ Roadmap
### Version 1.1 (Planned)
- [ ] Forge support
- [ ] Better state migration during hot reload
- [ ] GUI configuration editor
- [ ] Network inspector with packet editing
### Version 1.2 (Planned)
- [ ] Profiler integration
- [ ] Breakpoint debugging support
- [ ] Live variable editing
- [ ] Collaborative development features
### Future
- [ ] Multi-version support (1.19+)
- [ ] Cloud recording storage
- [ ] AI-assisted test generation
- [ ] Plugin system for extensions

## ⭐ Star History
If you find MMDR useful, please consider giving it a star on GitHub!

<p align="center"> <b>Made with ❤️ by the MMDR Team</b><br> <i>Saving mod developers thousands of hours, one reload at a time.</i> </p>