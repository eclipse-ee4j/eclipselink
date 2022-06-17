/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.databaseaccess;

import java.sql.ResultSet;

import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Marks {@link DatabasePlatform} specific wrapper of instance from {@link java.sql.ResultSet}.
 * Instances returned by {@link DatabasePlatform#getObjectFromResultSet(ResultSet, int, int, AbstractSession)}
 * from {@code ResultSet} and marked as wrappers are considered as unknown to EclipseLink core module
 * so they are returned back to {@link DatabasePlatform#convertObject(Object, Class)} method to be converted
 * to target {@link org.eclipse.persistence.internal.helper.DatabaseField#type} class.
 */
public interface PlatformWrapper {

    /**
     * Check whether provided instance is marked as {@link DatabasePlatform} specific wrapper.
     * @param instance instance to be checked
     * @return value of {@code true} when provided instance is marked as {@link DatabasePlatform}
     *         specific wrapper or {@code false} otherwise
     */
    static boolean isPlatformWrapper(Object instance) {
        return PlatformWrapper.class.isInstance(instance);
    }

}
