/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
