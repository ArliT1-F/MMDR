package hotreload;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class ClassReloaderTest {

    /** Simple dummy class used to test reloading. */
    public static class Dummy {
        public String hello() { return "hi"; }
    }

    @Test
    void reloadsClassFromBytecodeAndCreatesInstance() throws Exception {
        // Load the compiled bytes of Dummy from the classpath
        String resourceName = ClassReloaderTest.class.getSimpleName() + "$Dummy.class";
        try (InputStream in = ClassReloaderTest.class.getResourceAsStream(resourceName)) {
            assertNotNull(in, "Could not locate Dummy.class resource");
            byte[] bytes = in.readAllBytes();

            ClassReloader reloader = new ClassReloader();
            String fqcn = ClassReloaderTest.Dummy.class.getName();

            Class<?> reloaded = reloader.reloadClass(fqcn, bytes);
            assertNotNull(reloaded);
            assertEquals(fqcn, reloaded.getName());

            Object instance = reloader.newInstance(fqcn);
            assertEquals("hi", reloaded.getMethod("hello").invoke(instance));
        }
    }
}
