---
layout: home
title: Home
---

# MMDR - Minecraft Mod Development Runtime

**Stop wasting time waiting for restarts. Start coding at the speed of thought.**

MMDR is a revolutionary development environment that brings hot reload, interactive debugging, and automatic test generation to Minecraft mod development.

## ✨ Key Features

<div class="features">
  <div class="feature">
    <h3>🔥 Hot Reload</h3>
    <p>Modify your code and see changes instantly without restarting Minecraft. Save hours every day.</p>
  </div>
  
  <div class="feature">
    <h3>💻 Debug Console</h3>
    <p>Execute Java/Groovy code directly in-game with full access to Minecraft internals.</p>
  </div>
  
  <div class="feature">
    <h3>🔍 Visual Inspector</h3>
    <p>Inspect blocks, entities, NBT data, and network packets in real-time with a beautiful overlay.</p>
  </div>
  
  <div class="feature">
    <h3>🧪 Test Generator</h3>
    <p>Record your gameplay and automatically generate JUnit tests. Testing has never been easier.</p>
  </div>
</div>

## 🚀 Quick Start

```bash
# 1. Download MMDR
git clone https://github.com/yourusername/MMDR.git
cd MMDR


# 2. Build the mod
./gradlew build

# 3. Install to Minecraft
cp build/libs/mmdr-1.0.0.jar ~/.minecraft/mods/

# 4. Launch and press ` (backtick) to open console
```
[Get Started](/guides/getting-started) → {: .btn .btn-primary}

[View on GitHub](https://github.com/ArliT1-F/MMDR) → {: .btn .btn-secondary}

## 📊 Performance Impact
MMDR is designed to be lightweight and efficient:

| Feature	     | Performance Impact |
|:---------------|:-------------------|
|Hot Reload	     | ~1-2% CPU overhead |
|Console	     | 0% when closed     |
|Inspector	     | ~0.5 FPS drop when enabled |
|Test Recording	 | ~1-2% overhead |

## 💡 Use Cases
### During Development
- Test code changes instantly without restarting
- Debug issues with the interactive console
- Inspect game state in real-time
### For Testing
- Record gameplay sessions as tests
- Generate test code automatically
- Run tests in mock environments
### For Learning
- Experiment with Minecraft internals
- Learn by inspecting how things work
- Try code snippets without consequences
## 📖 Documentation
<div class="doc-links"> <a href="/guides/getting-started" class="doc-link"> <h4>Getting Started</h4> <p>Install MMDR and run your first hot reload</p> </a> <a href="/guides/hot-reload" class="doc-link"> <h4>Hot Reload Guide</h4> <p>Master instant code reloading</p> </a> <a href="/guides/console" class="doc-link"> <h4>Console Guide</h4> <p>Learn the interactive debug console</p> </a> <a href="/guides/inspector" class="doc-link"> <h4>Inspector Guide</h4> <p>Use the visual inspector overlay</p> </a> <a href="/guides/api-reference" class="doc-link"> <h4>API Reference</h4> <p>Complete API documentation</p> </a> </div>

## 🤝 Contributing
We welcome contributions! Whether it's bug reports, feature requests, or code contributions, we'd love your help.

[**Report a Bug**](https://github.com/ArliT1-F/MMDR/issues/new?template=bug_report.md)

[**Request a Feature**](https://github.com/ArliT1-F/MMDR/issues/new?template=feature_request.md)

[**Contributing Guide**](https://github.com/ArliT1-F/MMDR/blob/main/CONTRIBUTING.md)

## 📜 License
MMDR is open source software licensed under the MIT License.

<p class="text-center"> Made with ❤️ by the MMDR Team<br> <small>Saving mod developers thousands of hours, one reload at a time.</small> </p>