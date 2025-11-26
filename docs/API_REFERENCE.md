# MMDR API Reference (Hot Reload Core)

This is a high-level overview of the hot reload–related public APIs available in this snapshot of the repository.

## Package `hotreload`

### Class `FileWatcher`
- Watches one or more directories using `java.nio.file.WatchService`.
- Notifies a `FileWatcher.Listener` when files are created, modified, or deleted.
- Methods:
  - `FileWatcher(Listener listener)` – create a new watcher for the default file system.
  - `void watchDirectory(Path dir)` – start watching a directory.
  - `void start()` / `void stop()` – control the background watcher thread.
  - `boolean isRunning()` – query running state.
  - `Set<Path> getWatchedRoots()` – inspect watched directories.

### Class `HotReloadManager`
- High-level coordinator that connects `FileWatcher`, `BytecodeTransformer`, and `ClassReloader`.
- Methods:
  - `void addWatchDirectory(Path dir)` – register a compiled class output directory.
  - `void start()` / `void stop()` – enable or disable hot reload.
  - `boolean isRunning()` – query running state.
  - `long getReloadedClasses()` – number of successfully reloaded classes.
  - `Set<Path> getWatchedDirectories()` – view configured directories.
  - `ClassReloader getClassReloader()` – access the underlying reloader.

### Class `ClassReloader`
- Defines new versions of classes in a throwaway `ClassLoader`.
- Methods:
  - `Class<?> reloadClass(String className, byte[] bytecode)` – define or redefine a class.
  - `Class<?> getLatestClass(String className)` – get the latest loaded version.
  - `Object newInstance(String className)` – construct a new instance via the no-arg constructor.

### Class `BytecodeTransformer`
- Optional extension point for ASM-powered transformations.
- Methods:
  - `byte[] transform(String internalClassName, byte[] original)` – transform or return the original bytes.
  - `protected boolean shouldTransform(String internalClassName)` – override to decide when to transform.
  - `protected ClassVisitor createVisitor(ClassVisitor next, String internalClassName)` – customize visitor chain.