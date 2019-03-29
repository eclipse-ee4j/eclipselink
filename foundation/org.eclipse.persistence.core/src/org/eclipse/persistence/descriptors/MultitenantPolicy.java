/*
 * Copyright (c) 2011, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     09/09/2011-2.3.1 Guy Pelletier
//       - 356197: Add new VPD type to MultitenantType
//     11/10/2011-2.4 Guy Pelletier
//       - 357474: Address primaryKey option from tenant discriminator column
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
package org.eclipse.persistence.descriptors;

import java.io.Serializable;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * A multitenant interface.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.3.1
 */
public interface MultitenantPolicy extends Serializable {
    void addToTableDefinition(TableDefinition tableDefinition);
    void addFieldsToRow(AbstractRecord row, AbstractSession session);
    MultitenantPolicy clone(ClassDescriptor descriptor);
    boolean isSingleTableMultitenantPolicy();
    boolean isSchemaPerMultitenantPolicy();
    boolean isTablePerMultitenantPolicy();
    void postInitialize(AbstractSession session);
    void initialize(AbstractSession session) throws DescriptorException;
    void preInitialize(AbstractSession session) throws DescriptorException;
}
