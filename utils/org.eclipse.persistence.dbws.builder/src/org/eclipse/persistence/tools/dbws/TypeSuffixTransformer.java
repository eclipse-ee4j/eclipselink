/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.tools.dbws;

public class TypeSuffixTransformer extends DefaultNamingConventionTransformer {

    @Override
    protected boolean isDefaultTransformer() {
        return true;
    }

    @Override
    public String generateSchemaAlias(String tableName) {
        return super.generateSchemaAlias(tableName.concat("Type"));
    }

    @Override
    public String toString() {
        return "TypeSuffixTransformer adds suffix 'Type' to high-level schema type";
    }
}
