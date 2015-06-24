/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     19/04/2014-2.6 Lukas Jungmann
 *       - 429992: JavaSE 8/ASM 5.0.1 support (EclipseLink silently ignores Entity classes with lambda expressions)
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.weaving;

// J2SE imports
import java.lang.instrument.IllegalClassFormatException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.ProtectionDomain;
import java.util.Map;

import javax.persistence.spi.ClassTransformer;

import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.libraries.asm.ClassReader;
import org.eclipse.persistence.internal.libraries.asm.ClassVisitor;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.commons.SerialVersionUIDAdder;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetClassLoaderFromCurrentThread;
import org.eclipse.persistence.internal.weaving.WeaverLogger;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;

/**
 * INTERNAL:
 * This class performs dynamic byte code weaving: for each attribute
 * mapped with One To One mapping with Basic Indirection it substitutes the
 * original attribute's type for ValueHolderInterface.
 */
public class PersistenceWeaver implements ClassTransformer {

    public static final String EXCEPTION_WHILE_WEAVING = "exception_while_weaving";

    protected Session session; // for logging
    /** Class name in JVM '/' format to {@link ClassDetails} map. */
    protected Map classDetailsMap;

    /**
     * INTERNAL:
     * Creates an instance of dynamic byte code weaver.
     * @param session         EclipseLink session.
     * @param classDetailsMap Class name to {@link ClassDetails} map.
     */
    public PersistenceWeaver(final Session session, final Map classDetailsMap) {
        this.session = session;
        this.classDetailsMap = classDetailsMap;
    }

    /**
     * INTERNAL:
     * Allow the weaver to be clear to release its referenced memory.
     * This is require because the class loader reference to the transformer will never gc.
     */
    public void clear() {
        this.session = null;
        this.classDetailsMap = null;
    }

    /**
     * INTERNAL:
     * Get Class name in JVM '/' format to {@link ClassDetails} map.
     * @return Class name in JVM '/' format to {@link ClassDetails} map.
     */
    public Map getClassDetailsMap() {
        return classDetailsMap;
    }

    // @Override: well, not precisely. I wanted the code to be 1.4 compatible,
    // so the method is written without any Generic type <T>'s in the signature
    /**
     * INTERNAL:
     * Perform dynamic byte code weaving of class.
     * @param loader              The defining loader of the class to be transformed, may be {@code null}
     *                            if the bootstrap loader.
     * @param className           The name of the class in the internal form of fully qualified class and interface names.
     * @param classBeingRedefined If this is a redefine, the class being redefined, otherwise {@code null}.
     * @param protectionDomain    The protection domain of the class being defined or redefined.
     * @param classfileBuffer     The input byte buffer in class file format (must not be modified).
     * @return  A well-formed class file buffer (the result of the transform), or {@code null} if no transform is performed
     */
    @Override
    public byte[] transform(final ClassLoader loader, final String className,
            final Class classBeingRedefined, final ProtectionDomain protectionDomain,
            final byte[] classfileBuffer) throws IllegalClassFormatException {
        // PERF: Is finest logging turned on?
        final boolean shouldLogFinest = WeaverLogger.shouldLog(SessionLog.FINEST);
        final Map classDetailsMap = this.classDetailsMap;
        final Session session = this.session;
        // Check if cleared already.
        if ((classDetailsMap == null) || (session == null)) {
            return null;
        }
        try {
            /*
             * The ClassFileTransformer callback - when called by the JVM's
             * Instrumentation implementation - is invoked for every class loaded.
             * Thus, we must check the classDetailsMap to see if we are 'interested'
             * in the class.
             */
            final ClassDetails classDetails = (ClassDetails)classDetailsMap.get(Helper.toSlashedClassName(className));

            if (classDetails != null) {
                if (shouldLogFinest) {
                    WeaverLogger.log(SessionLog.FINEST, "begin_weaving_class", className);
                }
                final ClassReader classReader = new ClassReader(classfileBuffer);
                ClassWriter classWriter = null;
                final String introspectForHierarchy = System.getProperty(SystemProperties.WEAVING_REFLECTIVE_INTROSPECTION, null);
                if (introspectForHierarchy != null) {
                    if (shouldLogFinest) {
                        ClassLoader contextClassLoader;
                        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                            try {
                                contextClassLoader = AccessController.doPrivileged(
                                        new PrivilegedGetClassLoaderFromCurrentThread());
                            } catch (PrivilegedActionException ex) {
                                throw (RuntimeException) ex.getCause();
                            }
                        } else {
                            contextClassLoader = Thread.currentThread().getContextClassLoader();
                        }
                        WeaverLogger.log(SessionLog.FINEST, "weaving_init_class_writer", className,
                                Integer.toHexString(System.identityHashCode(contextClassLoader)));
                    }
                    classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                } else {
                    if (shouldLogFinest) {
                        ClassLoader contextClassLoader;
                        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                            try {
                                contextClassLoader = AccessController.doPrivileged(
                                        new PrivilegedGetClassLoaderFromCurrentThread());
                            } catch (PrivilegedActionException ex) {
                                throw (RuntimeException) ex.getCause();
                            }
                        } else {
                            contextClassLoader = Thread.currentThread().getContextClassLoader();
                        }
                        WeaverLogger.log(SessionLog.FINEST, "weaving_init_compute_class_writer", className,
                                Integer.toHexString(System.identityHashCode(contextClassLoader)),
                                loader != null ? Integer.toHexString(System.identityHashCode(loader)) : "null");
                      }
                    classWriter = new ComputeClassWriter(loader, ClassWriter.COMPUTE_FRAMES);
                }
                final ClassWeaver classWeaver = new ClassWeaver(classWriter, classDetails);
                final ClassVisitor sv = new SerialVersionUIDAdder(classWeaver);
                classReader.accept(sv, 0);
                if (classWeaver.alreadyWeaved) {
                    if (shouldLogFinest) {
                        WeaverLogger.log(SessionLog.FINEST, "end_weaving_class", className);
                    }
                    return null;
                }
                if (classWeaver.weaved) {
                    final byte[] bytes = classWriter.toByteArray();
                    final String outputPath = System.getProperty(SystemProperties.WEAVING_OUTPUT_PATH, "");

                    if (!outputPath.equals("")) {
                        Helper.outputClassFile(className, bytes, outputPath);
                    }
                    // PERF: Don't execute this set of if statements with logging turned off.
                    if (shouldLogFinest) {
                        if (classWeaver.weavedPersistenceEntity) {
                            WeaverLogger.log(SessionLog.FINEST, "weaved_persistenceentity", className);
                        }
                        if (classWeaver.weavedChangeTracker) {
                            WeaverLogger.log(SessionLog.FINEST, "weaved_changetracker", className);
                        }
                        if (classWeaver.weavedLazy) {
                            WeaverLogger.log(SessionLog.FINEST, "weaved_lazy", className);
                        }
                        if (classWeaver.weavedFetchGroups) {
                            WeaverLogger.log(SessionLog.FINEST, "weaved_fetchgroups", className);
                        }
                        if (classWeaver.weavedRest) {
                            WeaverLogger.log(SessionLog.FINEST, "weaved_rest", className);
                        }
                        WeaverLogger.log(SessionLog.FINEST, "end_weaving_class", className);
                    }
                    return bytes;
                }
                if (shouldLogFinest) {
                    WeaverLogger.log(SessionLog.FINEST, "end_weaving_class", className);
                }
            } else {
                if (shouldLogFinest) {
                    WeaverLogger.log(SessionLog.FINEST, "transform_missing_class_details", className);
                }
            }
        } catch (Throwable exception) {
            if (WeaverLogger.shouldLog(SessionLog.FINE)) {
                WeaverLogger.log(SessionLog.FINE, EXCEPTION_WHILE_WEAVING, new Object[] {exception, className});
                if (shouldLogFinest) {
                    WeaverLogger.logThrowable(SessionLog.FINEST, exception);
                }
            }
        }
        if (shouldLogFinest) {
            WeaverLogger.log(SessionLog.FINEST, "transform_existing_class_bytes", className);
        }
        return null; // returning null means 'use existing class bytes'
    }

    // same as in org.eclipse.persistence.internal.helper.Helper, but uses
    // '/' slash as delimiter, not '.'
    /**
     * INTERNAL:
     * Returns an unqualified class name from the specified class name.
     * @param name Class name with {@code '/'} as delimiter.
     * @return Unqualified class name.
     */
    protected static String getShortName(String name) {
        int pos  = name.lastIndexOf('/');
        if (pos >= 0) {
            name = name.substring(pos+1);
            if (name.endsWith(";")) {
                name = name.substring(0, name.length()-1);
            }
            return name;
        }
        return "";
    }
}
