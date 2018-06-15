/*
 * Copyright (c) 2008, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.tools.workbench.scplugin.model;

/**
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public enum SequenceType
{
    DEFAULT,

    /**
     * Designates the database sequence mechanism is used.
     */
    NATIVE,

    TABLE,
    UNARY_TABLE,
    XML_FILE
}
