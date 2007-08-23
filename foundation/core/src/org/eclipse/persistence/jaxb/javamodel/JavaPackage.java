/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jaxb.javamodel;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>A TopLink JAXB 2.0 Java model representation of a JDK Package.
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Provide the qualified name of the underlying package object</li>
 * </ul>
 *  
 * @since Oracle TopLink 11.1.1.0.0
 * @see org.eclipse.persistence.jaxb20.javamodel.JavaHasAnnotations
 * @see java.lang.Package
 */
public abstract interface JavaPackage extends JavaHasAnnotations {
    public String getQualifiedName();
}
