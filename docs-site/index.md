---
layout: home
title: Home
---

# MMDR - Minecraft Mod Development Runtime

**Stop wasting time waiting for restarts. Start coding at the speed of thought.**

MMDR eliminates the painful edit-compile-restart cycle by providing hot reload capabilities, an interactive debug console, visual inspection tools, and automatic test generation.

[Get Started →](/MMDR/guides/getting-started){: .btn .btn-primary}
[View on GitHub →](https://github.com/ArliT1-F/MMDR){: .btn .btn-secondary}

---

<div class="features" markdown="1">

<div class="feature" markdown="1">

### 🔥 Hot Reload System

Modify Java code and see changes **instantly** without restarting Minecraft. Save hours every day with intelligent class reloading and state preservation.

</div>

<div class="feature" markdown="1">

### 💻 Interactive Debug Console

Execute Java/Groovy code directly in-game with full access to Minecraft internals. REPL environment with command history and auto-completion.

</div>

<div class="feature" markdown="1">

### 🔍 Visual Inspector

Inspect blocks, entities, NBT data, and network packets in real-time with a beautiful overlay. Three modes: Basic, Detailed, and Advanced.

</div>

<div class="feature" markdown="1">

### 🧪 Automatic Test Generator

Record your gameplay and automatically generate JUnit tests. Export as JUnit, TestNG, or Cucumber features. Testing has never been easier.

</div>

</div>

---

## 📊 Performance Impact

MMDR is designed to be **lightweight and efficient**:

| Feature | Performance Impact |
|---------|-------------------|
| Hot Reload | ~1-2% CPU overhead |
| Console | 0% when closed |
| Inspector | ~0.5 FPS drop |
| Test Recording | ~1-2% overhead |

---

## 🚀 Quick Example

```java
// 1. Make a change to your mod
public String getMessage() {
    return "Hello from hot reload!"; // ✨ Change this
}

// 2. Save the file (Ctrl+S)
// 3. See changes instantly in-game!
```
Press ``` ` ``` in-game to open the debug console:
```groovy
// Teleport instantly
mc.player.setPosition(0, 100, 0)

// Give yourself items
mc.player.giveItemStack(new ItemStack(Items.DIAMOND, 64))

// Change time of day
mc.world.setTimeOfDay(6000)
```

## Why MMDR?
<div class="features" markdown="1"><div class="feature" markdown="1">

**During Develpment**
- Test code changes instantly
- Debug issues with interactivee console
- Inspect game state in real-time
</div><div class="feature" markdown="1">

**For Testing**
- Record gameplay sessions as tests
- Generate test code automatically
- Run tests in mock environments
</div><div class="feature" markdown="1">

**For Learning**
- Experiment with Minecraft internals
- Learn by inspecting how things work
- Try code snippets safely
</div></div>


## Documentation
Comprehensive guides to help you master MMDR:
- [Getting Started](/MMDR/guides/getting-started) - Install and run your first hot reload
- [Hot Reload Guide](/MMDR/guides/hot-reload) - Master instant code reloading
- [Console Guide](/MMDR/guides/console-guide) - Learn the interactive debug console
- [Inspector Guide](/MMDR/guides/inspector-guide) - Use the visual inspector overlay
- [API Reference](/MMDR/guides/api-reference) - Complete API documentation


## Contact Us

[**Report a Bug**](https://github.com/ArliT1-F/MMDR/issues/new?template=bug_report.md)

[**Request a Feature**](https://github.com/ArliT1-F/MMDR/issues/new?template=feature_request.md)

[**Contributing Guide**](https://github.com/ArliT1-F/MMDR/blob/main/CONTRIBUTING.md)

## 📜 License
MMDR is open source software licensed under the MIT License.

<p class="text-center"> Made with ❤️ by the MMDR Team<br> <small>Saving mod developers thousands of hours, one reload at a time.</small> </p>