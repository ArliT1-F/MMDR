package hotreload;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HotReloadManagerTest {

    private HotReloadManager manager;

    @AfterEach
    void tearDown() {
        if (manager != null) {
            manager.close();
        }
    }

    @Test
    void canStartAndStopWithWatchDirectoryConfigured() throws IOException {
        Path tempDir = Files.createTempDirectory("mmdr-hrm-test");

        manager = new HotReloadManager();
        manager.addWatchDirectory(tempDir);

        manager.start();
        assertTrue(manager.isRunning());
        assertTrue(manager.getWatchedDirectories().contains(tempDir.toAbsolutePath().normalize()));

        manager.stop();
        assertFalse(manager.isRunning());
    }
}
