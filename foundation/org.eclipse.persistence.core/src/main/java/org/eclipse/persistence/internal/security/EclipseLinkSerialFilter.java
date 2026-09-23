/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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
//     EclipseLink contributors - initial API and implementation
package org.eclipse.persistence.internal.security;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

import java.io.ObjectInputFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Limits classes read from a remote command or remote-session byte stream.
 * Column converters keep the unfiltered {@code JavaSerializer.deserialize}.
 */
public final class EclipseLinkSerialFilter implements ObjectInputFilter {

    private static final int MAX_DEPTH = 128;
    private static final long MAX_ARRAY_LENGTH = 1_000_000L;

    private final Session session;
    private Set<Class<?>> sessionClasses;

    private EclipseLinkSerialFilter(Session session) {
        this.session = session;
    }

    public static ObjectInputFilter create(Session session) {
        return new EclipseLinkSerialFilter(session);
    }

    @Override
    public Status checkInput(FilterInfo info) {
        if (info.depth() > MAX_DEPTH) {
            return Status.REJECTED;
        }
        if (info.arrayLength() >= 0 && info.arrayLength() > MAX_ARRAY_LENGTH) {
            return Status.REJECTED;
        }
        Class<?> serialClass = info.serialClass();
        if (serialClass == null) {
            return Status.UNDECIDED;
        }
        while (serialClass.isArray()) {
            serialClass = serialClass.getComponentType();
        }
        if (serialClass.isPrimitive()) {
            return Status.ALLOWED;
        }
        String name = serialClass.getName();
        if (name.startsWith("org.eclipse.persistence.")
                || name.startsWith("java.")
                || name.startsWith("javax.")
                || name.startsWith("jakarta.")) {
            return Status.ALLOWED;
        }
        if (sessionClasses().contains(serialClass)) {
            return Status.ALLOWED;
        }
        return Status.REJECTED;
    }

    private Set<Class<?>> sessionClasses() {
        if (sessionClasses != null) {
            return sessionClasses;
        }
        sessionClasses = new HashSet<>();
        if (!(session instanceof AbstractSession abstractSession) || abstractSession.getProject() == null) {
            return sessionClasses;
        }
        Map<Class<?>, ClassDescriptor> descriptors = abstractSession.getProject().getDescriptors();
        if (descriptors == null) {
            return sessionClasses;
        }
        for (ClassDescriptor descriptor : descriptors.values()) {
            if (descriptor == null) {
                continue;
            }
            if (descriptor.getJavaClass() != null) {
                sessionClasses.add(descriptor.getJavaClass());
            }
            List<DatabaseMapping> mappings = descriptor.getMappings();
            if (mappings == null) {
                continue;
            }
            for (DatabaseMapping mapping : mappings) {
                Class<?> classification = mapping.getAttributeClassification();
                if (classification != null) {
                    sessionClasses.add(classification);
                }
            }
        }
        return sessionClasses;
    }
}
