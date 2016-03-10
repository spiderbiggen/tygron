/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tytech.util.logger.TLogger;

/**
 * ObjectUtils
 * <p>
 * ObjectUtils can create new object and clone objects.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public class ObjectUtils {

    /**
     * Case in sensitive comparator for Objects (not only Strings)
     */
    public final static Comparator<Object> CASE_INSENSITIVE_ORDER = (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.toString(),
            o2.toString());

    /**
     * Constructs am object from a class with given parameters and values.
     *
     * @param <T> Class/Object type
     * @param classz Class of the to be created object
     * @param parameters Class parameters for constructor
     * @param values Values for constructor
     * @return the new Object T
     */
    @SuppressWarnings("unchecked")
    public final static <T> T constructObject(final Class<T> classz, final Class<?>[] parameters, final Object... values) {

        try {

            /**
             * TODO: we need to check this for other event param types aswell!!!
             */

            // catch java classes
            if (classz.getSuperclass() != null && classz.getSuperclass().equals(Number.class)) {
                // invoke the valueOf method
                final Method valueOf = classz.getMethod("valueOf", String.class);
                return (T) valueOf.invoke(classz, "0");
            } else if (classz.equals(Boolean.class)) {
                return (T) new Boolean(false);
            } else if (classz.equals(Calendar.class)) {
                return (T) Calendar.getInstance();
            } else if (classz.isEnum()) {
                return classz.getEnumConstants()[0];
            }

            if (Modifier.isAbstract(classz.getModifiers())) {
                TLogger.severe("Cannot instantiate abstract class " + classz.getSimpleName());
                return null;
            }

            final Constructor<T> constructor = classz.getConstructor(parameters);
            return constructor.newInstance(values);

        } catch (Exception exp) {

            String message = "Failed to construct object: " + classz.getSimpleName()
                    + ". It should contain a constructor with the following arguments: ";
            for (Class<?> parameter : parameters) {
                message += parameter.getSimpleName() + ", ";
            }
            if (parameters.length == 0) {
                message += "NONE";
            }
            TLogger.exception(exp, message);
            return null;
        }
    }

    /**
     * Create a clone of the object. The objects are serialized and de-serialized. During this process a deep copy is created of the object.
     * This procedure is time consuming but takes time away from the programmer.
     *
     * @param orginal Orginal object to be cloned
     * @return deep-copy clone of orginal.
     */
    @SuppressWarnings("unchecked")
    public final static <T> T deepCopy(final T original) {

        // null's are directly returned.
        if (original == null) {
            return null;
        }

        T clone = null;

        try {
            // create out stream
            final ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            final ObjectOutputStream outStream = new ObjectOutputStream(byteArrayStream);

            // serialize and pass the object
            outStream.writeObject(original);
            outStream.flush();

            // create in stream
            final ByteArrayInputStream bin = new ByteArrayInputStream(byteArrayStream.toByteArray());
            final ObjectInputStream inStream = new ObjectInputStream(bin);

            // read cloned object, and cast it to T, this is unsafe!
            clone = (T) inStream.readObject();

            // close streams
            outStream.close();
            inStream.close();

        } catch (NotSerializableException exp) {
            TLogger.exception(exp, "Cannot clone object, it must be serializable: " + original.getClass().getCanonicalName());
        } catch (IOException exp) {
            TLogger.exception(exp, "Clone operation failed due to a Stream IO error.");
        } catch (ClassNotFoundException exp) {
            TLogger.exception(exp, "Clone operation failed due to a class not found.");
        } catch (Exception exp) {
            TLogger.exception(exp, "Clone operation failed.");
        }
        return clone;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T getEnumAnnotation(Enum enumm, Class clasz) {
        String fieldName = enumm.name();
        try {
            return (T) enumm.getClass().getField(fieldName).getAnnotation(clasz);
        } catch (Exception e) {
            nl.tytech.util.logger.TLogger.exception(e);
        }
        return null;
    }

    /**
     * Returns all interfaces of this class and its super's. If the class is an interface itself this is also returned.
     *
     * @param type Class to check
     * @return List of interfaces
     */
    public final static List<Class<?>> getInterfaces(final Class<?> type) {

        // get all the interfaces of the class
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        if (type.isInterface()) {
            interfaces.add(type);
        } else {
            Class<?> loop = type;
            while (!loop.equals(Object.class)) {
                for (Class<?> interfacez : loop.getInterfaces()) {
                    interfaces.add(interfacez);
                }
                loop = loop.getSuperclass();
            }
        }
        return interfaces;
    }

    public static <T> T morphObjectToClass(Object source, Class<T> targetClass) {

        TLogger.info("Moving from: " + source.getClass().getSimpleName() + " to " + targetClass.getSimpleName());
        List<Field> newFields = new ArrayList<Field>();
        List<Field> oldFields = new ArrayList<Field>();

        Class<?> oldClass = source.getClass();
        while (oldClass != null) {
            Field[] fieldArray = oldClass.getDeclaredFields();
            oldFields.addAll(Arrays.asList(fieldArray));
            oldClass = oldClass.getSuperclass();
        }

        Class<?> newClass = targetClass;
        while (newClass != null) {
            Field[] fieldArray = newClass.getDeclaredFields();
            newFields.addAll(Arrays.asList(fieldArray));
            newClass = newClass.getSuperclass();
        }

        // Here we go
        try {
            final T newItem = targetClass.newInstance();
            for (Field oldField : oldFields) {
                if (Modifier.isFinal(oldField.getModifiers()) || Modifier.isStatic(oldField.getModifiers())) {
                    continue;
                }
                if (!newFields.contains(oldField)) {
                    continue;
                }
                oldField.setAccessible(true);
                Object value = oldField.get(source);
                oldField.set(newItem, value);

            }

            return newItem;

        } catch (Exception ec) {
            TLogger.exception(ec);
        }

        return null;
    }

    public static <T> T newInstanceForArgs(Class<T> classz, Object[] args) {

        try {
            if (args == null || args.length == 0) {
                return classz.newInstance();
            }
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                parameterTypes[i] = arg.getClass();
            }
            Constructor<T> constructor = classz.getConstructor(parameterTypes);
            return constructor.newInstance(args);

        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    /**
     * Check if this is an array list otherwise create one.
     * @param list
     * @return
     */
    public final static <T> ArrayList<T> toArrayList(final List<T> list) {

        if (list instanceof ArrayList) {
            // cast to it
            return (ArrayList<T>) list;
        } else {
            // create new
            return new ArrayList<>(list);
        }
    }

    public final static <T> ArrayList<T> toArrayList(T[] array) {
        return ObjectUtils.toArrayList(Arrays.asList(array));
    }

    public final static <K, V> HashMap<K, V> toHashMap(final Map<K, V> map) {

        if (map instanceof HashMap) {
            // cast to it
            return (HashMap<K, V>) map;
        } else {
            // create new
            return new HashMap<>(map);
        }
    }

    public static double[] toNativeDoubleArray(Double[] objectArray) {
        if (objectArray == null) {
            return new double[0];
        }

        double[] nativeArray = new double[objectArray.length];

        for (int i = 0; i < objectArray.length; i++) {
            nativeArray[i] = objectArray[i].doubleValue();
        }

        return nativeArray;
    }

    public static float[] toNativeFloatArray(Float[] objectArray) {
        if (objectArray == null) {
            return new float[0];
        }

        float[] nativeArray = new float[objectArray.length];

        for (int i = 0; i < objectArray.length; i++) {
            nativeArray[i] = objectArray[i].floatValue();
        }

        return nativeArray;
    }

    public static Double[] toObjectDoubleArray(double[] nativeArray) {
        if (nativeArray == null) {
            return new Double[0];
        }

        Double[] objectArray = new Double[nativeArray.length];

        for (int i = 0; i < nativeArray.length; i++) {
            objectArray[i] = new Double(nativeArray[i]);
        }

        return objectArray;
    }

    public static Float[] toObjectFloatArray(float[] nativeArray) {
        if (nativeArray == null) {
            return new Float[0];
        }

        Float[] objectArray = new Float[nativeArray.length];

        for (int i = 0; i < nativeArray.length; i++) {
            objectArray[i] = new Float(nativeArray[i]);
        }

        return objectArray;
    }

    // do not instantiate
    private ObjectUtils() {

    }
}
