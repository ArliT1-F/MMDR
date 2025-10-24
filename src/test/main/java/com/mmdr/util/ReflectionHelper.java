package com.mmdr.util;

import com.mmdr.MMDR;

import java.lang.reflect.*;
import java.util.*;

/**
 * Utility class for reflection operations.
 * 
 * Provides safe and convenient methods for:
 * - Field access and modification
 * - Method invocation
 * - Constructor invocation
 * - Class analysis
 * - Generic type resolution
 * 
 * @author MMDR Team
 */
public class ReflectionHelper {
    
    /**
     * Get a field value from an object
     */
    public static Object getFieldValue(Object obj, String fieldName) throws ReflectionException {
        try {
            Field field = findField(obj.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new ReflectionException("Failed to get field value: " + fieldName, e);
        }
    }
    
    /**
     * Set a field value on an object
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) throws ReflectionException {
        try {
            Field field = findField(obj.getClass(), fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new ReflectionException("Failed to set field value: " + fieldName, e);
        }
    }
    
    /**
     * Get a static field value
     */
    public static Object getStaticFieldValue(Class<?> clazz, String fieldName) throws ReflectionException {
        try {
            Field field = findField(clazz, fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            throw new ReflectionException("Failed to get static field value: " + fieldName, e);
        }
    }
    
    /**
     * Set a static field value
     */
    public static void setStaticFieldValue(Class<?> clazz, String fieldName, Object value) throws ReflectionException {
        try {
            Field field = findField(clazz, fieldName);
            field.setAccessible(true);
            field.set(null, value);
        } catch (Exception e) {
            throw new ReflectionException("Failed to set static field value: " + fieldName, e);
        }
    }
    
    /**
     * Find a field in a class or its superclasses
     */
    public static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> current = clazz;
        
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        
        throw new NoSuchFieldException("Field not found: " + fieldName + " in " + clazz.getName());
    }
    
    /**
     * Invoke a method on an object
     */
    public static Object invokeMethod(Object obj, String methodName, Object... args) throws ReflectionException {
        try {
            Class<?>[] paramTypes = getParameterTypes(args);
            Method method = findMethod(obj.getClass(), methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw new ReflectionException("Failed to invoke method: " + methodName, e);
        }
    }
    
    /**
     * Invoke a static method
     */
    public static Object invokeStaticMethod(Class<?> clazz, String methodName, Object... args) throws ReflectionException {
        try {
            Class<?>[] paramTypes = getParameterTypes(args);
            Method method = findMethod(clazz, methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(null, args);
        } catch (Exception e) {
            throw new ReflectionException("Failed to invoke static method: " + methodName, e);
        }
    }
    
    /**
     * Find a method in a class or its superclasses
     */
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Class<?> current = clazz;
        
        while (current != null) {
            try {
                return current.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                current = current.getSuperclass();
            }
        }
        
        throw new NoSuchMethodException("Method not found: " + methodName + " in " + clazz.getName());
    }
    
    /**
     * Create an instance of a class
     */
    public static <T> T createInstance(Class<T> clazz, Object... args) throws ReflectionException {
        try {
            Class<?>[] paramTypes = getParameterTypes(args);
            Constructor<T> constructor = clazz.getDeclaredConstructor(paramTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new ReflectionException("Failed to create instance of: " + clazz.getName(), e);
        }
    }
    
    /**
     * Get parameter types from arguments
     */
    private static Class<?>[] getParameterTypes(Object... args) {
        if (args == null || args.length == 0) {
            return new Class<?>[0];
        }
        
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i] != null ? args[i].getClass() : Object.class;
        }
        return types;
    }
    
    /**
     * Get all fields from a class including superclasses
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        
        while (current != null) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        
        return fields;
    }
    
    /**
     * Get all methods from a class including superclasses
     */
    public static List<Method> getAllMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        Class<?> current = clazz;
        
        while (current != null) {
            methods.addAll(Arrays.asList(current.getDeclaredMethods()));
            current = current.getSuperclass();
        }
        
        return methods;
    }
    
    /**
     * Get fields with a specific annotation
     */
    public static List<Field> getFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Field> result = new ArrayList<>();
        
        for (Field field : getAllFields(clazz)) {
            if (field.isAnnotationPresent(annotationClass)) {
                result.add(field);
            }
        }
        
        return result;
    }
    
    /**
     * Get methods with a specific annotation
     */
    public static List<Method> getMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Method> result = new ArrayList<>();
        
        for (Method method : getAllMethods(clazz)) {
            if (method.isAnnotationPresent(annotationClass)) {
                result.add(method);
            }
        }
        
        return result;
    }
    
    /**
     * Check if a class has a specific annotation
     */
    public static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return clazz.isAnnotationPresent(annotationClass);
    }
    
    /**
     * Get the generic type of a field
     */
    public static Type getGenericType(Field field) {
        return field.getGenericType();
    }
    
    /**
     * Copy fields from one object to another
     */
    public static void copyFields(Object source, Object target) throws ReflectionException {
        if (source == null || target == null) {
            throw new ReflectionException("Source and target must not be null");
        }
        
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();
        
        for (Field field : getAllFields(sourceClass)) {
            try {
                field.setAccessible(true);
                Object value = field.get(source);
                
                Field targetField = findField(targetClass, field.getName());
                targetField.setAccessible(true);
                targetField.set(target, value);
            } catch (Exception e) {
                MMDR.LOGGER.debug("Failed to copy field: " + field.getName(), e);
            }
        }
    }
    
    /**
     * Check if a class is a primitive or wrapper type
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || 
               clazz == Boolean.class ||
               clazz == Byte.class ||
               clazz == Character.class ||
               clazz == Short.class ||
               clazz == Integer.class ||
               clazz == Long.class ||
               clazz == Float.class ||
               clazz == Double.class ||
               clazz == String.class;
    }
    
    /**
     * Get the wrapper class for a primitive type
     */
    public static Class<?> getWrapperClass(Class<?> primitiveClass) {
        if (!primitiveClass.isPrimitive()) {
            return primitiveClass;
        }
        
        if (primitiveClass == boolean.class) return Boolean.class;
        if (primitiveClass == byte.class) return Byte.class;
        if (primitiveClass == char.class) return Character.class;
        if (primitiveClass == short.class) return Short.class;
        if (primitiveClass == int.class) return Integer.class;
        if (primitiveClass == long.class) return Long.class;
        if (primitiveClass == float.class) return Float.class;
        if (primitiveClass == double.class) return Double.class;
        
        return primitiveClass;
    }
    
    /**
     * Check if a method is a getter
     */
    public static boolean isGetter(Method method) {
        if (!method.getName().startsWith("get") && !method.getName().startsWith("is")) {
            return false;
        }
        
        if (method.getParameterCount() != 0) {
            return false;
        }
        
        return method.getReturnType() != void.class;
    }
    
    /**
     * Check if a method is a setter
     */
    public static boolean isSetter(Method method) {
        if (!method.getName().startsWith("set")) {
            return false;
        }
        
        return method.getParameterCount() == 1;
    }
    
    /**
     * Get all interfaces implemented by a class
     */
    public static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
        Set<Class<?>> interfaces = new HashSet<>();
        
        while (clazz != null) {
            interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
            clazz = clazz.getSuperclass();
        }
        
        return interfaces;
    }
    
    /**
     * Check if a class implements an interface
     */
    public static boolean implementsInterface(Class<?> clazz, Class<?> interfaceClass) {
        return interfaceClass.isAssignableFrom(clazz);
    }
    
    /**
     * Custom exception for reflection errors
     */
    public static class ReflectionException extends Exception {
        public ReflectionException(String message) {
            super(message);
        }
        
        public ReflectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}