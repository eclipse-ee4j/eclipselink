/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     James Sutherland - initial API and implementation
package org.eclipse.persistence.platform.database.partitioning;

import javax.sql.DataSource;

import org.eclipse.persistence.sessions.Session;

/**
 * PUBLIC:
 * Defines the API for the integration with an external DataSources data partitioning support.
 * This is used to support data partitioning in an Oracle RAC through UCP and WebLogic.
 *
 * @see org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
public interface DataPartitioningCallback {

    void register(DataSource dataSoruce, Session session);

    void setPartitionId(int id);

}
