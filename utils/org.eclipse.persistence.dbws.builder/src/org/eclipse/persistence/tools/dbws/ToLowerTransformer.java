/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.tools.dbws;

public class ToLowerTransformer extends DefaultNamingConventionTransformer {

    @Override
    protected boolean isDefaultTransformer() {
        return true;
    }

    @Override
    public String generateSchemaAlias(String tableName) {
        return super.generateSchemaAlias(tableName.toLowerCase());
    }

    @Override
    public String generateElementAlias(String originalElementName) {
        return super.generateElementAlias(originalElementName.toLowerCase());
    }

    @Override
    public String toString() {
        return "ToLowerTransformer converts strings to their lowercase";
    }
}
