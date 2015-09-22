/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A model for Java class.
 *
 * @author Dmitry Kornilov
 */
public class ClassModel {
    private final Class clazz;

    /**
     * Indicates that this class is nillable.
     */
    private boolean nillable;

    /**
     * A list of class fields.
     */
    private final List<FieldModel> fields = new ArrayList<>();

    public ClassModel(Class clazz) {
        this.clazz = clazz;
    }

    public Class getType() {
        return clazz;
    }

    public List<FieldModel> getFieldModels() {
        return fields;
    }
}
