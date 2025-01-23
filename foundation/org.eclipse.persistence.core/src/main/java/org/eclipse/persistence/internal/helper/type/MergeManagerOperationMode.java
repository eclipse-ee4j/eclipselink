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

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.helper.type;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>: It is data model behind {@code org.eclipse.persistence.config.SystemProperties#CONCURRENCY_MANAGER_ALLOW_GET_CACHE_KEY_FOR_MERGE_MODE}
 * or {@code org.eclipse.persistence.config.PersistenceUnitProperties#CONCURRENCY_MANAGER_ALLOW_GET_CACHE_KEY_FOR_MERGE_MODE}.
 */
public enum MergeManagerOperationMode {
    ORIGIN,
    WAITLOOP;
}
