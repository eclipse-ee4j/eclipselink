/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.dynamic.dynamicclassloader;

//javase imports
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Constructor;

//JUnit4 imports
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicClassWriter;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.internal.dynamic.DynamicTypeImpl;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.SerializationHelper;
import static org.eclipse.persistence.exceptions.DynamicException.INCOMPATIBLE_DYNAMIC_CLASSWRITERS;

public class DynamicClassLoaderTestSuite {

    public static final String PACKAGE_PREFIX = 
        DynamicClassLoaderTestSuite.class.getPackage().getName();
    static final String MY_CLASSNAME = PACKAGE_PREFIX + ".MyClass";
    
    @Test
    public void nullParrent() throws Exception {
        DynamicClassLoader dcl = new DynamicClassLoader(null);
        assertNull(dcl.getParent());
    }

    @Test
    public void coreClass() throws Exception {
        DynamicClassLoader dcl = new DynamicClassLoader(null);
        Class<?> stringClass = dcl.createDynamicClass("java.lang.String");
        assertTrue("core class java.lang.String not found", String.class == stringClass);
    }
    
    @Test(expected=NoClassDefFoundError.class)
    public void noClass() {
        DynamicClassLoader dcl = new DynamicClassLoader(null);
        dcl.createDynamicClass(MY_CLASSNAME);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void loadClass_DynamicEntityImpl() throws Exception {
        DynamicClassLoader dcl = new DynamicClassLoader(DynamicClassLoaderTestSuite.class.getClassLoader());

        dcl.addClass(MY_CLASSNAME);
        Class<?> dynamicClass = dcl.loadClass(MY_CLASSNAME);

        assertNotNull(dynamicClass);
        assertEquals(MY_CLASSNAME, dynamicClass.getName());
        assertSame(DynamicEntityImpl.class, dynamicClass.getSuperclass());
        assertSame(dynamicClass, dcl.loadClass(MY_CLASSNAME));

        ConversionManager.setDefaultLoader(dcl);
        ConversionManager.getDefaultManager().setLoader(dcl);

        assertSame(dynamicClass, ConversionManager.getDefaultManager().convertClassNameToClass(MY_CLASSNAME));
        assertSame(dynamicClass, ConversionManager.getDefaultManager().convertObject(MY_CLASSNAME, Class.class));
        assertSame(dynamicClass, ConversionManager.getDefaultLoader().loadClass(MY_CLASSNAME));
        assertSame(dynamicClass, ConversionManager.loadClass(MY_CLASSNAME));

        InstantiationException instEx = null;
        try {
            dynamicClass.newInstance();
        } catch (InstantiationException ie) {
            instEx = ie;
        }
        assertNotNull("InstantiationException not thrown as expected for default constructor", instEx);

        Constructor<DynamicEntity>[] constructors = 
            (Constructor<DynamicEntity>[])dynamicClass.getConstructors();
        assertEquals(1, constructors.length);
        assertEquals(1, constructors[0].getParameterTypes().length);
        assertEquals(DynamicTypeImpl.class, constructors[0].getParameterTypes()[0]);

        Constructor<DynamicEntity> constructor = 
            (Constructor<DynamicEntity>)dynamicClass.getDeclaredConstructor(new Class[] { DynamicTypeImpl.class });
        assertNotNull(constructor);
        constructor = (Constructor<DynamicEntity>)dynamicClass.getConstructor(new Class[] { DynamicTypeImpl.class });
        assertNotNull(constructor);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createDynamicClass_DynamicEntityImpl() throws Exception {
        DynamicClassLoader dcl = new DynamicClassLoader(DynamicClassLoaderTestSuite.class.getClassLoader());

        Class<?> dynamicClass = dcl.createDynamicClass(MY_CLASSNAME);

        assertNotNull(dynamicClass);
        assertEquals(MY_CLASSNAME, dynamicClass.getName());
        assertSame(DynamicEntityImpl.class, dynamicClass.getSuperclass());
        assertSame(dynamicClass, dcl.loadClass(MY_CLASSNAME));

        ConversionManager.setDefaultLoader(dcl);
        ConversionManager.getDefaultManager().setLoader(dcl);

        assertSame(dynamicClass, ConversionManager.getDefaultManager().convertClassNameToClass(MY_CLASSNAME));
        assertSame(dynamicClass, ConversionManager.getDefaultManager().convertObject(MY_CLASSNAME, Class.class));
        assertSame(dynamicClass, ConversionManager.getDefaultLoader().loadClass(MY_CLASSNAME));
        assertSame(dynamicClass, ConversionManager.loadClass(MY_CLASSNAME));

        InstantiationException instEx = null;
        try {
            dynamicClass.newInstance();
        } catch (InstantiationException ie) {
            instEx = ie;
        }
        assertNotNull("InstantiationException not thrown as expected for default constructor", instEx);

        Constructor<DynamicEntity>[] constructors = 
            (Constructor<DynamicEntity>[])dynamicClass.getConstructors();
        assertEquals(1, constructors.length);
        assertEquals(1, constructors[0].getParameterTypes().length);
        assertEquals(DynamicTypeImpl.class, constructors[0].getParameterTypes()[0]);

        Constructor<DynamicEntity> constructor = 
            (Constructor<DynamicEntity>)dynamicClass.getDeclaredConstructor(new Class[]{DynamicTypeImpl.class });
        assertNotNull(constructor);
        constructor = 
            (Constructor<DynamicEntity>)dynamicClass.getConstructor(new Class[]{DynamicTypeImpl.class });
        assertNotNull(constructor);
    }

    @Test
    public void createDynamicClass_Twice() throws Exception {
        DynamicClassLoader dcl = new DynamicClassLoader(DynamicClassLoaderTestSuite.class.getClassLoader());

        assertNull(dcl.getClassWriter(MY_CLASSNAME));
        Class<?> dynamicClass = dcl.createDynamicClass(MY_CLASSNAME);

        assertNotNull(dynamicClass);
        assertEquals(MY_CLASSNAME, dynamicClass.getName());

        DynamicClassWriter writer = dcl.getClassWriter(MY_CLASSNAME);
        assertNotNull(writer);

        Class<?> dynamicClass2 = dcl.createDynamicClass(MY_CLASSNAME);

        assertSame(dynamicClass, dynamicClass2);

        DynamicClassWriter writer2 = dcl.getClassWriter(MY_CLASSNAME);
        assertNotNull(writer);
        assertSame(writer, writer2);
    }

    @Test
    public void defaultWriter() throws Exception {
        DynamicClassLoader dcl = new DynamicClassLoader(DynamicClassLoaderTestSuite.class.getClassLoader());

        assertEquals(DynamicClassWriter.class, dcl.getDefaultWriter().getClass());
    }

    @Test
    public void loadClass_DefaultConstructor() throws Exception {
        DynamicClassLoader dcl = new DynamicClassLoader(DynamicClassLoaderTestSuite.class.getClassLoader());

        dcl.addClass(MY_CLASSNAME, DefaultConstructor.class);
        Class<?> dynamicClass = dcl.loadClass(MY_CLASSNAME);

        assertNotNull(dynamicClass);
        assertSame(dynamicClass, dcl.loadClass(MY_CLASSNAME));
        assertSame(DefaultConstructor.class, dynamicClass.getSuperclass());

        DefaultConstructor entity = (DefaultConstructor) dynamicClass.newInstance();

        assertNotNull(entity);
    }

    @Test
    public void loadClass_StringConstructor() throws Exception {
        DynamicClassLoader dcl = new DynamicClassLoader(DynamicClassLoaderTestSuite.class.getClassLoader());

        dcl.addClass(MY_CLASSNAME, StringConstructor.class);
        Class<?> dynamicClass = dcl.loadClass(MY_CLASSNAME);

        assertNotNull(dynamicClass);
        assertSame(dynamicClass, dcl.loadClass(MY_CLASSNAME));
        assertSame(StringConstructor.class, dynamicClass.getSuperclass());

        InstantiationException instEx = null;
        try {
            dynamicClass.newInstance();
        } catch (InstantiationException ie) {
            instEx = ie;
        }
        assertNotNull("InstantiationException not thrown as expected for default constructor", instEx);

        Constructor<?>[] constructors = dynamicClass.getConstructors();
        assertEquals(1, constructors.length);
        assertEquals(1, constructors[0].getParameterTypes().length);
        assertEquals(String.class, constructors[0].getParameterTypes()[0]);
    }

    @Test
    public void loadClass_WriteReplace() throws Exception {
        DynamicClassLoader dcl = new DynamicClassLoader(DynamicClassLoaderTestSuite.class.getClassLoader());

        dcl.addClass(MY_CLASSNAME, WriteReplace.class);
        Class<?> dynamicClass = dcl.loadClass(MY_CLASSNAME);

        assertNotNull(dynamicClass);
        assertEquals(MY_CLASSNAME, dynamicClass.getName());
        assertSame(WriteReplace.class, dynamicClass.getSuperclass());
        assertSame(dynamicClass, dcl.loadClass(MY_CLASSNAME));

        WriteReplace entity = (WriteReplace) dynamicClass.newInstance();

        assertNotNull(entity);

        byte[] entityBytes = SerializationHelper.serialize(entity);
        byte[] stringBytes = SerializationHelper.serialize(entity.getClass().getName());

        assertEquals(stringBytes.length, entityBytes.length);
        for (int index = 0; index < stringBytes.length; index++) {
            assertEquals(stringBytes[index], entityBytes[index]);
        }

        Object deserializedValue = SerializationHelper.deserialize(entityBytes);

        assertNotNull(deserializedValue);
        assertEquals(String.class, deserializedValue.getClass());
        assertEquals(dynamicClass.getName(), deserializedValue);
    }

    @Test
    public void createDynamicClass_WriteReplace() throws Exception {
        DynamicClassLoader dcl = new DynamicClassLoader(DynamicClassLoaderTestSuite.class.getClassLoader());

        Class<?> dynamicClass = dcl.createDynamicClass(MY_CLASSNAME, WriteReplace.class);

        assertNotNull(dynamicClass);
        assertEquals(MY_CLASSNAME, dynamicClass.getName());
        assertSame(WriteReplace.class, dynamicClass.getSuperclass());
        assertSame(dynamicClass, dcl.loadClass(MY_CLASSNAME));

        WriteReplace entity = (WriteReplace) dynamicClass.newInstance();

        assertNotNull(entity);

        byte[] entityBytes = SerializationHelper.serialize(entity);
        byte[] stringBytes = SerializationHelper.serialize(entity.getClass().getName());

        assertEquals(stringBytes.length, entityBytes.length);
        for (int index = 0; index < stringBytes.length; index++) {
            assertEquals(stringBytes[index], entityBytes[index]);
        }

        Object deserializedValue = SerializationHelper.deserialize(entityBytes);

        assertNotNull(deserializedValue);
        assertEquals(String.class, deserializedValue.getClass());
        assertEquals(dynamicClass.getName(), deserializedValue);
    }

    @Test
    public void duplicateAddClassWithSameParent() throws Exception {
        DynamicClassLoader dcl = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());

        dcl.addClass(MY_CLASSNAME, DefaultConstructor.class);
        Class<?> dynamicClass = dcl.loadClass(MY_CLASSNAME);

        assertNotNull(dynamicClass);
        assertSame(dynamicClass, dcl.loadClass(MY_CLASSNAME));
        assertSame(DefaultConstructor.class, dynamicClass.getSuperclass());
        DynamicClassWriter firstWriter = dcl.getClassWriter(MY_CLASSNAME);

        DefaultConstructor entity = (DefaultConstructor) dynamicClass.newInstance();

        assertNotNull(entity);
        assertNotNull("DCL does not contain expected writer", dcl.getClassWriter(MY_CLASSNAME));

        dcl.addClass(MY_CLASSNAME, DefaultConstructor.class);
        DynamicClassWriter secondWriter = dcl.getClassWriter(MY_CLASSNAME);

        assertSame(firstWriter, secondWriter);
    }

    /**
     * Verify that a second request to create a class with the same name and
     * different parents fails.
     */
    @Test
    public void duplicateAddClassWithDifferentParent() throws Exception {
        DynamicClassLoader dcl = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());

        dcl.addClass(MY_CLASSNAME, DefaultConstructor.class);
        Class<?> dynamicClass = dcl.loadClass(MY_CLASSNAME);

        assertNotNull(dynamicClass);
        assertSame(dynamicClass, dcl.loadClass(MY_CLASSNAME));
        assertSame(DefaultConstructor.class, dynamicClass.getSuperclass());

        DefaultConstructor entity = (DefaultConstructor) dynamicClass.newInstance();

        assertNotNull(entity);
        assertNotNull("DCL does not contain expected writer", dcl.getClassWriter(MY_CLASSNAME));

        try {
            dcl.addClass(MY_CLASSNAME, WriteReplace.class);
        } catch (DynamicException de) {
            String errorMessage = de.getMessage();
            int errorCode = de.getErrorCode();

            assertTrue("Incorrect dynamic exception", errorMessage.startsWith("\r\nException Description: Duplicate addClass request with incompatible writer:"));
            assertEquals("Unexpected error code", INCOMPATIBLE_DYNAMIC_CLASSWRITERS, errorCode);
            return;
        }
        fail("No DynamicException thrown for duplicate addClass with different parent");
    }

    public static class DefaultConstructor {
    }

    public static class StringConstructor {
        public StringConstructor(String arg) {
        }
    }

    public static class WriteReplace implements Serializable {
        protected Object writeReplace() throws ObjectStreamException {
            return getClass().getName();
        }
    }
}
