/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jaxb.javamodel;

import java.util.Map;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>A TopLink JAXB 2.0 Java model representation of a JDK Annotation.
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Provide the annotation's name</li>
 * <li>Provide a map of components (declared members) for this annotation type</li>
 * </ul>
 *
 * @since Oracle TopLink 11.1.1.0.0
 * @see java.lang.annotation.Annotation
 *
 */
public interface JavaAnnotation {

    public Map getComponents();

    public String getName();

}
