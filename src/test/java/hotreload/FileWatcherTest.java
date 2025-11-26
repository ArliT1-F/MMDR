package hotreload;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileWatcherTest {

    private FileWatcher watcher;

    @AfterEach
    void tearDown() {
        if (watcher != null) {
            watcher.close();
        }
    }

    @Test
    void detectsFileCreationInWatchedDirectory() throws Exception {
        Path tempDir = Files.createTempDirectory("mmdr-fw-test");
        List<Path> events = new CopyOnWriteArrayList<>();

        watcher = new FileWatcher((path, kind) -> {
            if (path != null) {
                events.add(path);
            }
        });
        watcher.watchDirectory(tempDir);
        watcher.start();

        // Trigger a create event
        Path created = tempDir.resolve("test.txt");
        Files.writeString(created, "hello");

        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (System.nanoTime() < deadline && events.isEmpty()) {
            Thread.sleep(25);
        }

        assertFalse(events.isEmpty(), "Expected at least one file event");
        assertTrue(events.stream().anyMatch(p -> p.equals(created.toAbsolutePath())),
                "Expected event for created file");
    }
}
