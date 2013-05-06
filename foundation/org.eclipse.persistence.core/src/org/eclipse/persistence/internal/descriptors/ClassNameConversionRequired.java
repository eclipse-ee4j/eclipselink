/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
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
