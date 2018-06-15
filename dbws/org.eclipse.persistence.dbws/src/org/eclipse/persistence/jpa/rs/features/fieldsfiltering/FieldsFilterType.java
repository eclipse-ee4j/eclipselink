/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Dmitry Kornilov - initial implementation
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
