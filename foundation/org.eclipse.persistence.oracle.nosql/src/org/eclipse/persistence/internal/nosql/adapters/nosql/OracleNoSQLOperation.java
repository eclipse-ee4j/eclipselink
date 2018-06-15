/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.nosql.adapters.nosql;

/**
 * Defines the valid Oracle NoSQL operations.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public enum OracleNoSQLOperation {
    GET, PUT, PUT_IF_ABSENT, PUT_IF_PRESENT, PUT_IF_VERSION, DELETE, DELETE_IF_VERSION, ITERATOR
}
