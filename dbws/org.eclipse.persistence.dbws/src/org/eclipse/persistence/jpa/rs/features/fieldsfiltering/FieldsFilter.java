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
//      Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpa.rs.features.fieldsfiltering;

import java.util.Arrays;
import java.util.List;

/**
 * Filter for fields filtering (projection) feature. Since JPARS 2.0.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class FieldsFilter {
    private List<String> fields;

    private FieldsFilterType type;

    public FieldsFilter(FieldsFilterType type, String fields) {
        this.type = type;
        this.fields = Arrays.asList(fields.split("\\s*,\\s*"));
    }

    public List<String> getFields() {
        return fields;
    }

    public FieldsFilterType getType() {
        return type;
    }
}
