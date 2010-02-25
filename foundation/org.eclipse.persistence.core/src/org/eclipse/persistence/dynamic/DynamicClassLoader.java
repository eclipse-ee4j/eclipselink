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
 *     dclarke, mnorman - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *
 ******************************************************************************/
package org.eclipse.persistence.dynamic;

//javase imports
import java.util.HashMap;
import java.util.Map;

//EclipseLink imports
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.sessions.Session;

/**
 * This custom ClassLoader provides support for dynamically generating classes
 * within an EclipseLink application using byte codes created using a
 * {@link DynamicClassWriter}. A DynamicClassLoader requires a parent or
 * delegate class-loader which is provided to the constructor. This delegate
 * class loader handles the lookup and storage of all created classes.
 * 
 * @author dclarke, mnorman
 * @since EclipseLink 1.2
 */
public class DynamicClassLoader extends ClassLoader {

    /**
     * Map of {@link DynamicClassWriter} used to dynamically create a class in
     * the {@link #findClass(String)} call. The application must register
     * classes using addClass or createDynameClass prior to the
     * {@link #findClass(String)} being invoked.
     * <p>
     * The map of writers is maintained for the life of this DynamicClassLoader
     * instance to ensure additional requests to create dynamic classes of the
     * same name are properly verified. Duplicate requests for dynamic classes
     * of the same name, same writer type, and the same parent class are
     * permitted but different parent classes or different writer types are not.
     */
    protected Map<String, DynamicClassWriter> classWriters = new HashMap<String, DynamicClassWriter>();

    /**
     * Default writer to use if one is not specified.
     */
    public DynamicClassWriter defaultWriter = new DynamicClassWriter();

    /**
     * Create a DynamicClassLoader providing the delegate loader and leaving the
     * defaultWriter as {@link DynamicClassWriter}
     */
    public DynamicClassLoader(ClassLoader delegate) {
        super(delegate);
    }

    /**
     * Create a DynamicClassLoader providing the delegate loader and a default
     * {@link DynamicClassWriter}.
     */
    public DynamicClassLoader(ClassLoader delegate, DynamicClassWriter writer) {
        this(delegate);
        this.defaultWriter = writer;
    }

    public DynamicClassWriter getDefaultWriter() {
        return this.defaultWriter;
    }

    protected Map<String, DynamicClassWriter> getClassWriters() {
        return this.classWriters;
    }

    public DynamicClassWriter getClassWriter(String className) {
        return getClassWriters().get(className);
    }

    /**
     * Register a class to be dynamically created using the default
     * {@link DynamicClassWriter}.
     * 
     * @see #addClass(String, DynamicClassWriter)
     */
    public void addClass(String className) {
        addClass(className, getDefaultWriter());
    }

    /**
     * Register a class to be dynamically created using a copy of default
     * {@link DynamicClassWriter} but specifying a different parent class.
     * 
     * @see #addClass(String, DynamicClassWriter)
     */
    public void addClass(String className, Class<?> parentClass) {
        addClass(className, getDefaultWriter().createCopy(parentClass));
    }

    /**
     * Register a class to be dynamically created using the provided
     * {@link DynamicClassWriter}. The registered writer is used when the
     * {@link #findClass(String)} method is called back on this loader from the
     * {@link #loadClass(String)} call.
     * <p>
     * If a duplicate request is made for the same className and the writers are
     * not compatible a {@link DynamicException} will be thrown. If the
     * duplicate request contains a compatible writer then the second request is
     * ignored as the class may already have been generated.
     * 
     * @see #findClass(String)
     */
    public void addClass(String className, DynamicClassWriter writer) throws DynamicException {
        DynamicClassWriter existingWriter = getClassWriter(className);

        // Verify that the existing writer is compatible with the requested
        if (existingWriter != null) {
            if (!existingWriter.isCompatible(writer)) {
                throw DynamicException.incompatibleDuplicateWriters(className, existingWriter, writer);
            }
        } else {
            getClassWriters().put(className, writer == null ? getDefaultWriter() : writer);
        }
    }

    /**
     * Create a dynamic class registering a writer and then forcing the provided
     * class name to be loaded.
     * 
     */
    public Class<?> createDynamicClass(String className, DynamicClassWriter writer) throws DynamicException {
        addClass(className, writer);

        try {
            return loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("DyanmicClassLoader could not create class: " + className);
        }
    }

    /**
     * Create a new dynamic entity type for the specified name assuming the use
     * of the default writer and its default parent class.
     * 
     * @see #creatDynamicClass(String, DynamicClassWriter)
     */
    public Class<?> createDynamicClass(String className) {
        return createDynamicClass(className, getDefaultWriter());
    }

    /**
     * Create a new dynamic entity type for the specified name with the
     * specified parent class.
     * 
     * @see #creatDynamicClass(String, DynamicClassWriter)
     */
    public Class<?> createDynamicClass(String className, Class<?> parentClass) {
        return createDynamicClass(className, new DynamicClassWriter(parentClass));
    }

    /**
     * Create a new dynamic class if a ClassWriter is registered for the
     * provided className. This code is single threaded to ensure only one class
     * is created for a given name and that the ClassWriter is removed
     * afterwards.
     */
    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        DynamicClassWriter writer = getClassWriter(className);

        if (writer != null) {
            try {
                byte[] bytes = writer.writeClass(this, className);
                return defineClass(className, bytes, 0, bytes.length);
            } catch (ClassFormatError cfe) {
                throw new ClassNotFoundException(className, cfe);
            }
        }

        return super.findClass(className);
    }

    /**
     * Lookup the DynamicConversionManager for the given session. If the
     * existing ConversionManager is not an instance of DynamicConversionManager
     * then create a new one and replace the existing one.
     * 
     * @param session
     * @return
     */
    public static DynamicClassLoader lookup(Session session) {
        ConversionManager cm = null;

        if (session == null) {
            cm = ConversionManager.getDefaultManager();
        } else {
            cm = session.getPlatform().getConversionManager();
        }

        if (cm.getLoader() instanceof DynamicClassLoader) {
            return (DynamicClassLoader) cm.getLoader();
        }

        DynamicClassLoader dcl = new DynamicClassLoader(cm.getLoader());
        cm.setLoader(dcl);

        if (session == null) {
            ConversionManager.setDefaultLoader(dcl);
        }

        return dcl;
    }

}