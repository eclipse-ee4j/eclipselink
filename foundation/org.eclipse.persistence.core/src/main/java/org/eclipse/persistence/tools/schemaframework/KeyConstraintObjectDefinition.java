/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.tools.schemaframework;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class KeyConstraintObjectDefinition extends ConstraintObjectDefinition {
    private List<String> sourceFields; // field names

    protected KeyConstraintObjectDefinition() {
        this("");
    }

    protected KeyConstraintObjectDefinition(String name) {
        super(name);
        sourceFields = new ArrayList<>();
    }

    protected KeyConstraintObjectDefinition(String name, String sourceField) {
        this(name);
        sourceFields.add(sourceField);
    }

    @Deprecated(forRemoval = true, since = "4.0.9")
    protected void appendKeys(Writer writer, List<String> keys) {
        try {
            writer.write("(");
            for (Iterator<String> iterator = keys.iterator();
                 iterator.hasNext();) {
                writer.write(iterator.next());
                if (iterator.hasNext()) {
                    writer.write(", ");
                }
            }
            writer.write(")");
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public void addSourceField(String sourceField) {
        getSourceFields().add(sourceField);
    }

    public List<String> getSourceFields() {
        return sourceFields;
    }

    public void setSourceFields(List<String> sourceFields) {
        this.sourceFields = sourceFields;
    }
}
