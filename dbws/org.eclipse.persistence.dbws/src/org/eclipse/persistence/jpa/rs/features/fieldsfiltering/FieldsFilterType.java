/*******************************************************************************
 * Copyright (c) 2014 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.features.fieldsfiltering;

/**
 * Type of fields filter. Part of fields filtering (projection) feature implementation.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0.
 */
public enum FieldsFilterType {
    /* Indicates that filter contains a list of fields to include in the result */
    INCLUDE,

    /* Indicates that filter contains a list of fields to exclude from the result */
    EXCLUDE
}
