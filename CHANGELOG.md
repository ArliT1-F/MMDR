# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - Initial hot reload core
- Added hotreload.FileWatcher for efficient directory watching.
- Added hotreload.ClassReloader for class loader–based reloading.
- Added hotreload.BytecodeTransformer as an ASM-based extension point.
- Added hotreload.HotReloadManager to coordinate file watching and class reloading.


## [1.0.1]
**Fixed: Gradle Build Failure - Shadow plugin Configuration**
- **Issue**: `Could not determine the depencencies of tasl ':shadowJar'` - implementation configuration not resolvable

- **Fix**:
    - Created custom `shade` configuration with `canBeResolved = true`
    - Migrated bundled dependencies from `implementation` to `shade`
    - Updated `shadowJar` task to use `configurations.shade` instead of `configurations.implementation`


**Fixed: Incorrect Mod Initializer Interface**
- **Issue**: `Class com.mmdr.MMDR connot be cast to net.fabricmc.api.ClientModInitializer`
- **Fix**:
    - Changed main class from `implements ModInitializer` to `implements ClientModInitializer`
    - Updated initialization method from `onInitialize()` to `onInitializeClient()`
    - Updated import to `net.fabricmc.api.ClientModInitializer`


**Fixed: Duplicate Entrypoint Declaration**
- **Issue**: `Class com.mmdr.MMDR cannot be cast to net.fabricmc.api.ModInitializer` (main entrypoint)
- **Fix**:
    - Removed `"main"` entrypoint from `fabric.mod.json`
    - Kept only `"client"` entrypoint since MMDR is a client-side development tool

### Technical Details
- **Build Tool**: Gradle 8.5 with Fabric Loom 1.5.8
- **Shadow Plugin**: 8.1.1
- **Minecraft**: 1.20.4
- **Fabric Loader**: 0.18.1
