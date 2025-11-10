---
layout: default
title: Getting Started
---

# Getting Started with MMDR

Welcome to MMDR! This guide will walk you through installation, setup, and your first development session with instant hot reloading.

---

## Table of Contents

1. [Installation](#installation)
2. [First Launch](#first-launch)
3. [Setting Up Hot Reload](#setting-up-hot-reload)
4. [Your First Hot Reload](#your-first-hot-reload)
5. [Using the Debug Console](#using-the-debug-console)
6. [Using the Inspector](#using-the-inspector)
7. [Recording Your First Test](#recording-your-first-test)
8. [Troubleshooting](#troubleshooting)

---

## Installation

### Prerequisites

Before installing MMDR, ensure you have:

- ✅ Minecraft 1.20.4
- ✅ Fabric Loader 0.15.3 or higher
- ✅ Fabric API mod
- ✅ Java 17 or higher
- ✅ A Java IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Download and Install

**Option 1: Build from Source**

```bash
# Clone the repository
git clone https://github.com/yourusername/MMDR.git
cd MMDR

# Build the mod
./gradlew build

# The built JAR will be in build/libs/
```
**Option 2: Download Release**
1. Go to Releases
2. Download the latest `mmdr-x.x.x.jar`

**Install the Mod**

1. Navigate to your Minecraft directory:

- Windows: `%APPDATA%\.minecraft`
- macOS: `~/Library/Application Support/minecraft`
- Linux: `~/.minecraft`

2. Place mmdr-x.x.x.jar in the mods folder

3. Launch Minecraft with the Fabric profile

## First Launch
### Verify Installation
1. **Launch Minecraft** with the Fabric profile
2. Wait for the main menu to load
3. Press ``` ` ``` (backtick key, usually above Tab)
4. **MMDR Debug Console** will show up:
```text
╔════════════════════════════════════════════════════════╗
║        MMDR Debug Console v1.0.0                       ║
║  Type Java/Groovy code to execute. Press ESC to close. ║
╚════════════════════════════════════════════════════════╝
```
>
5. Type `println("Hello MMDR!")` and press Enter
6. You should see the output: `Hello MMDR!`

**If the console opens, installation was successful! ✅**

### Configuration File

On first launch, MMDR creates a configuration file at:
```text
.minecraft/config/mmdr-config.json
```

You can edit this file to customize MMDR's behavior.

---
## Setting Up Hot Reload

Hot reload requires MMDR to watch your mod's compiled class files.

### Step 1: Configure Your IDE
**IntelliJ IDEA:**

1. Go to **Settings → Build, Execution, Deployment → Compiler**
2. Enable **"Build project automatically"**
3. Enable **"Compile independent modules in parallel"**
4. Click **Apply**

**Eclipse:**

1. Go to **Window → Preferences → General → Workspace**
2. Enable **"Build automatically"**
3. Click **Apply**

### Step 2: Configure Watch Paths

MMDR needs to know where your compiled classes are located.

Edit `.minecraft/config/mmdr-config.json`:
```JSON

{
  "hotReloadEnabled": true,
  "additionalWatchPaths": [
    "/path/to/your/mod/build/classes/java/main",
    "/path/to/your/mod/out/production/classes"
  ]
}
```
**Example paths:**

- IntelliJ: `C:/Projects/MyMod/out/production/classes`
- Gradle: `C:/Projects/MyMod/build/classes/java/main`
- Eclipse: `C:/Projects/MyMod/bin`

### Step 3: Verify Hot Reload is Active
1. Launch Minecraft with your mod
2. Open the console (``` ` ```)
3.  Type: `/reload status`
4. You should see:
```text

Hot Reload: ✓ ACTIVE
Watching: 2 directories
Reloaded: 0 classes
```

---
### Your First Hot Reload

Let's modify some code and see it update instantly!

### Step 1: Create a Test Class

In your mod, create a simple class:
```java

package com.example.mymod;

public class HotReloadTest {
    public static String getMessage() {
        return "Original message";
    }
}
```
### Step 2: Use It In-Game
1. Launch Minecraft
2. Open the debug console (``` ` ```)
3. Execute:
```java

import com.example.mymod.HotReloadTest
println(HotReloadTest.getMessage())
```
4. You should see: `Original message`

### Step 3: Modify the Code
In your IDE, change the message:
```Java
public static String getMessage() {
    return "HOT RELOADED! 🔥"; // Changed!
}
```
**Save the file** (Ctrl+S / Cmd+S)

### Step 4: See the Changes

In the console, run the same code again:
```java
println(HotReloadTest.getMessage())
```
You should now see: `HOT RELOADED! 🔥`

No restart needed! ✨

---

## Using the Debug Console

The console is a powerful REPL (Read-Eval-Print-Loop) environment.

### Basic Commands
```java

// Get the Minecraft client instance
mc

// Get the player
mc.player

// Get the world
mc.world

// Print player position
println(mc.player.getPos())

// Teleport player
mc.player.setPosition(0, 100, 0)

// Give items
import net.minecraft.item.Items
import net.minecraft.item.ItemStack
mc.player.giveItemStack(new ItemStack(Items.DIAMOND, 64))

// Change time
mc.world.setTimeOfDay(6000)  // Noon
mc.world.setTimeOfDay(18000) // Midnight

// Weather
mc.world.setWeather(0, 0, true, false)  // Clear
```
### Special Commands

Commands starting with `/` are MMDR-specific:
```java

/help       // Show all commands
/clear      // Clear console output
/vars       // List all variables
/reload     // Manually reload a class
/imports    // Show auto-imports
```
### Variables
The console provides these pre-defined variables:

| Variable | 	Type	       | Description     |
|:--------:|:-----------------:|:---------------:|
|`mc`	   | `MinecraftClient` | The game client |
|`mmdr`	   | `MMDR`	           | MMDR main class | 

### Command History
- **Up Arrow**: Previous command
- **Down Arrow**: Next command
- **Ctrl+R**: Search history

### Multi-line Input

For complex code, use multi-line mode:
```java
// Define a function
def teleportRandomly() {
    def x = (Math.random() - 0.5) * 1000
    def z = (Math.random() - 0.5) * 1000
    mc.player.setPosition(x, 100, z)
}

// Call it
teleportRandomly()
```
### Using the Inspector
The inspector shows detailed information about blocks and entities.

### Basic Usage
1. Press **F3** to enable the inspector
2. Look at any block or entity
3. Information appears on the left side of the screen
### Keyboard Shortcuts
| Key |	Function|
|---|---------------------------|
|F3	|Toggle inspector / cycle modes |
|F4	|Toggle NBT data display |
|F5	|Toggle packet monitor |
|F6	|Toggle event listeners |
|F7	|Toggle performance metrics |

### Inspector Modes

**BASIC Mode:**

- Block/entity type
- Position
- Basic properties

**DETAILED Mode:**
- All basic info
- NBT data
- Block state properties
- Entity health/velocity

**ADVANCED Mode:**
- Everything from detailed
- Network packets
- Event listeners
- Performance metrics

### Reading NBT Data

When NBT display is enabled (F4), you'll see:
```text
§6§lNBT Data:
§e{
  §eid§7: §b"minecraft:chest"
  §eitems§7: [
    §e{
      §eSlot§7: §b0
      §eid§7: §a"minecraft:diamond"
      §eCount§7: §b64
    §e}
  §e]
§e}
```
**Color codes:**
- 🟡 Yellow: Keys
- 🔵 Blue: Numbers
- 🟢 Green: Strings
- 🟣 Purple: Booleans

## Recording Your First Test
The test harness can automatically generate unit tests from your gameplay.

### Step 1: Start Recording
1. Press **F9** to start recording
2. You'll see a message: `[MMDR] Recording started`
3. A red dot appears in the corner
### Step 2: Perform Actions
Do some actions in the game:

- Break some blocks
- Place some blocks
- Use items
- Move around
### Step 3: Stop Recording
1. Press **F9** again
2. You'll see: `[MMDR] Recording stopped - X actions recorded`

### Step 4: Generate Test Code

Open the console and run:
```java
MMDR.getTestHarness().generateTestCodeFromCurrent("MyFirstTest")
```
### Step 5: View the Generated Test

The test is saved to:
```text

.minecraft/mmdr_recordings/MyFirstTest.java
```
Example generated code:
```Java
package com.example.tests;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MyFirstTest {
    
    @Test
    public void testRecordedSession() {
        // Action 0: Break block at (10, 64, 20)
        player.breakBlock(new BlockPos(10, 64, 20));
        
        // Action 1: Place block at (11, 64, 20)
        player.placeBlock(new BlockPos(11, 64, 20), Blocks.STONE);
        
        // Action 2: Move to 12.50, 64.00, 21.30
        player.setPosition(12.50, 64.00, 21.30);
    }
}
```
## Troubleshooting
### Console Won't Open

Problem: Pressing ``` ` ``` does nothing

**Solutions:**
1. Check for key conflicts in **Options → Controls**
2. Verify MMDR is installed: Look for MMDR in the Mods menu
3. Check `logs/latest.log` for errors
4. Try rebinding the key in controls

### Hot Reload Not Working

Problem: Code changes don't appear in-game

**Solutions:**

1. **Verify IDE is compiling:**
- IntelliJ: Check the "Build" output panel
- Eclipse: Check "Problems" view
- Look for `.class` files in your output directory

2. **Check watch paths:**
```java

// In console:
MMDR.getHotReloadManager().isRunning()
// Should return: true
```
3. **Verify the class isn't excluded:**
- Check `excludedPackages` in config
- Minecraft classes (`net.minecraft.*`) can't be reloaded

4. **Check the logs:**
```text

[MMDR] Detected change in class: com.example.MyClass
[MMDR] Successfully reloaded 1 classe(s)
```
## Inspector Shows Nothing
Problem: Inspector overlay is blank

**Solutions:**
1. Look directly at a block or entity
2. Toggle modes with F3
3. Check that `inspectorEnabled: true` in config
4. Restart Minecraft

## Performance Issues

Problem: Game runs slowly with MMDR

**Solutions:**

1. **Disable unused features:**
```JSON
{
  "hotReloadEnabled": false,
  "inspectorShowPackets": false
}
```
2. **Increase file watch interval:**
```JSON
{
  "fileWatchInterval": 2000
}
```
3. **Allocate more memory:**

    Add to JVM arguments: `-Xmx4G`

### Class Reload Fails
Problem: Error message when reloading a class

**Common causes:**

1. **Syntax errors:** Fix the error and save again
2. **Changed method signatures:** Some changes require restart
3. **Static initialization:** Static blocks run again on reload
4. **Class dependencies:** Dependent classes may need reload too

**Workaround:**
```java
// Force reload dependencies
/reload com.example.DependentClass
```

## Next Steps

Now that you're set up, explore more features:

- (To be added)📖 [Hot Reload Guide](docs/HOT_RELOAD_GUIDE.md) - Advanced hot reload techniques
- (To be added)💻 [Console Guide](docs/CONSOLE_GUIDE.md) - Master the debug console
- (To be added)🔍 [Inspector Guide](docs/INSPECTOR_GUIDE.md) - Deep dive into inspection
- (To be added)📚 [API Reference](docs/API_REFERENCE.md) - Complete API documentation

## Getting Help

- **Documentation:** Check the [docs](../) folder
- **Issues:** [GitHub Issues](https://github.com/ArliT1-F/MMDR/issues)
- **Discord:** Join our server (`we don't have one yet`)
- **Discussions:** [GitHub Discussions](https://github.com/ArliT1-F/MMDR/discussions)

Happy modding with MMDR! 🚀