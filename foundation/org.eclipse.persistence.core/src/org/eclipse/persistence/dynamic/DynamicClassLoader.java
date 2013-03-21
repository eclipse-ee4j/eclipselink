/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.Helper;
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
    protected Map<String, EclipseLinkClassWriter> classWriters = new HashMap<String, EclipseLinkClassWriter>();
    protected Map<String, EnumInfo> enumInfoRegistry = new HashMap<String, EnumInfo>();

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

    protected Map<String, EclipseLinkClassWriter> getClassWriters() {
        return this.classWriters;
    }

    public EclipseLinkClassWriter getClassWriter(String className) {
        return getClassWriters().get(className);
    }

    public void addEnum(String className, Object... literalLabels) {
        EnumInfo enumInfo = enumInfoRegistry.get(className);
        if (enumInfo == null) {
            enumInfo = new EnumInfo(className);
            enumInfoRegistry.put(className, enumInfo);
        }
        if (literalLabels != null) {
            for (Object literalLabel : literalLabels) {
                if (literalLabel != null) {
                    enumInfo.addLiteralLabel(literalLabel.toString());
                }
            }
        }
        addClass(className);
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
    public void addClass(String className, EclipseLinkClassWriter writer) throws DynamicException {
        EclipseLinkClassWriter existingWriter = getClassWriter(className);

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
    public Class<?> createDynamicClass(String className, DynamicClassWriter writer) {
        addClass(className, writer);
        Class<?> newDynamicClass = null;
        try {
            newDynamicClass = loadClass(className);
        }
        catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException("DyanmicClassLoader: could not create class " + className);
        }
        return checkAssignable(newDynamicClass);
    }

    protected Class<?> checkAssignable(Class<?> clz) {
        EclipseLinkClassWriter assignedClassWriter = getClassWriters().get(clz.getName());
        if ((assignedClassWriter.getParentClass() == null && !assignedClassWriter.getParentClassName().equals(clz.getName())) || !assignedClassWriter.getParentClass().isAssignableFrom(clz)) {
            throw new IllegalArgumentException("DynamicClassLoader: " + clz.getName() + " not compatible with parent class " + assignedClassWriter.getParentClass().getName());
        }
        return clz;
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
        EclipseLinkClassWriter writer = getClassWriter(className);

        if (writer != null) {
            try {
                byte[] bytes = writer.writeClass(this, className);
                if (bytes != null) {
                    String outputPath = System.getProperty(SystemProperties.WEAVING_OUTPUT_PATH, "");

                    if (!outputPath.equals("")) {
                        Helper.outputClassFile(className, bytes, outputPath);
                    }
                }
                return defineDynamicClass(className, bytes);
            } catch (ClassFormatError cfe) {
                throw new ClassNotFoundException(className, cfe);
            } catch (ClassCircularityError cce) {
                throw new ClassNotFoundException(className, cce);
            }
        }

        return super.findClass(className);
    }

    /**
     * Converts an array of bytes into an instance of class <tt>Class</tt>.
     * Before the <tt>Class</tt> can be used it must be resolved.
     * 
     * @param name
     * @param b
     * @throws ClassFormatError
     */
    protected Class<?> defineDynamicClass(String name, byte[] b)  {
        return defineClass(name, b, 0, b.length);
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

    public static class EnumInfo {
        String className;
        List<String> literalLabels = new ArrayList<String>();

        public EnumInfo(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }

        public String[] getLiteralLabels() {
            return literalLabels.toArray(new String[literalLabels.size()]);
        }

        public void addLiteralLabel(String literalLabel) {
            if (!literalLabels.contains(literalLabel) && literalLabel != null) {
                literalLabels.add(literalLabel);
            }
        }
    }
}