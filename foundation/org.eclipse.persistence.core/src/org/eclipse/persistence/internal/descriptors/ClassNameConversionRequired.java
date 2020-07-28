/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.descriptors;

/**
 * Tagging interface used to mark class as requiring class name conversion during deployment.
 *
 * @author James Sutherland
 * @since EclipseLink 2.6
 */
public interface ClassNameConversionRequired {

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this converter to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * This method is implemented by subclasses as necessary.
     * @param classLoader
     */
    void convertClassNamesToClasses(ClassLoader classLoader);
}
