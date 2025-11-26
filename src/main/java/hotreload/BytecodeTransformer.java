package hotreload;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * Extension point for bytecode transformations.
 *
 * <p>By default this transformer is a no-op and simply returns the
 * original bytes. You can subclass it or add additional visitors to
 * inject logging, metrics, or other instrumentation.</p>
 */
public class BytecodeTransformer {

    /**
     * Transform the given class bytecode.
     *
     * @param internalClassName JVM internal class name (e.g. com/example/MyClass)
     * @param original          original bytecode
     * @return transformed bytecode (may be the same array)
     */
    public byte[] transform(String internalClassName, byte[] original) {
        // Fast path – no transforms configured.
        if (!shouldTransform(internalClassName)) {
            return original;
        }

        ClassReader reader = new ClassReader(original);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = createVisitor(writer, internalClassName);
        reader.accept(cv, 0);
        return writer.toByteArray();
    }

    /** Hook for subclasses to decide if a class should be transformed. */
    protected boolean shouldTransform(String internalClassName) {
        return false;
    }

    /** Hook for subclasses to provide a visitor chain. */
    protected ClassVisitor createVisitor(ClassVisitor next, String internalClassName) {
        return next;
    }
}