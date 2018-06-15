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

public interface NamingConventionTransformer {

    public enum ElementStyle {
        ELEMENT, ATTRIBUTE, NONE
    };

    public String generateSchemaAlias(String tableName);

    public String generateElementAlias(String originalElementName);

    public ElementStyle styleForElement(String originalElementName);

    public static final String DEFAULT_OPTIMISTIC_LOCKING_FIELD = "VERSION";
    public String getOptimisticLockingField();
}
