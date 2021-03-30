/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
// Contributors:
//      Oracle - initial API and implementation
package org.eclipse.persistence.internal.libraries.asm;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.SortedMap;
import java.util.Collections;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EclipseLink specific {@link ClassVisitor} that generates a corresponding ClassFile structure
 * for currently running Java VM.
 */
public class EclipseLinkASMClassWriter extends ClassWriter {

    private static final Logger LOG = Logger.getLogger(EclipseLinkASMClassWriter.class.getName());

    private final int version = getLatestOPCodeVersion();

    private static final SortedMap<String, Integer> versionMap = Collections.unmodifiableSortedMap(new TreeMap<String, Integer>() {
        {
            put("1.7", Opcodes.V1_7);
            put("1.8", Opcodes.V1_8);
            put("9", Opcodes.V9);
            put("10", Opcodes.V10);
            put("11", Opcodes.V11);
            put("12", Opcodes.V12);
            put("13", Opcodes.V13);
            put("14", Opcodes.V14);
            put("15", Opcodes.V15);
            put("16", Opcodes.V16);
            put("17", Opcodes.V17);
        }
    });

    public EclipseLinkASMClassWriter() {
        this(ClassWriter.COMPUTE_FRAMES);
    }

    public EclipseLinkASMClassWriter(final int flags) {
        super(flags);
    }

  /**
   * Visits the header of the class with {@code version} set
   * equal to currently running Java SE version.
   *
   * @param access the class's access flags (see {@link Opcodes}). This parameter also indicates if
   *     the class is deprecated {@link Opcodes#ACC_DEPRECATED} or a record {@link
   *     Opcodes#ACC_RECORD}.
   * @param name the internal name of the class (see {@link Type#getInternalName()}).
   * @param signature the signature of this class. May be {@literal null} if the class is not a
   *     generic one, and does not extend or implement generic classes or interfaces.
   * @param superName the internal of name of the super class (see {@link Type#getInternalName()}).
   *     For interfaces, the super class is {@link Object}. May be {@literal null}, but only for the
   *     {@link Object} class.
   * @param interfaces the internal names of the class's interfaces (see {@link
   *     Type#getInternalName()}). May be {@literal null}.
   * @see #visit(int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
   */
    public final void visit(final int access, final String name,
            final String signature, final String superName, final String[] interfaces) {
        visit(version, access, name, signature, superName, interfaces);
    }

    private static int getLatestOPCodeVersion() {
        // let's default to oldest supported Java SE version
        String v = versionMap.firstKey();
        if (System.getSecurityManager() == null) {
            v = System.getProperty("java.specification.version");
        } else {
            try {
                v = AccessController.doPrivileged(new PrivilegedAction<String>() {
                    @Override
                    public String run() {
                        return System.getProperty("java.specification.version");
                    }
                });
            } catch (Throwable t) {
                // ie SecurityException
                LOG.log(Level.WARNING, "Cannot read 'java.specification.version' property.", t);
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "Generating bytecode for Java SE ''{0}''.", v);
                }
            }
        }
        Integer version = versionMap.get(v);
        if (version == null) {
            // current JDK is iether too new
            String latest = versionMap.lastKey();
            if (latest.compareTo(v) < 0) {
                LOG.log(Level.WARNING, "Java SE ''{0}'' is not fully supported yet. Report this error to the EclipseLink open source project.", v);
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "Generating bytecode for Java SE ''{0}''.", latest);
                }
                version = versionMap.get(latest);
            } else {
                // or too old
                String key = versionMap.firstKey();
                LOG.log(Level.WARNING, "Java SE ''{0}'' is too old.", v);
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "Generating bytecode for Java SE ''{0}''.", key);
                }
                version = versionMap.get(key);
            }
        }
        return version;
    }
}
