/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
//      Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpa.rs.util.list;

import jakarta.xml.bind.JAXBElement;
import java.util.List;

/**
 * Marker interface for queries returning single results.
 *
 * @see org.eclipse.persistence.jpa.rs.util.list.SingleResultQueryResult
 * @see org.eclipse.persistence.jpa.rs.util.list.SingleResultQueryList
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0.
 */
public interface SingleResultQuery {
    /**
     * Gets a list fields.
     *
     * @return a list of JAXBElement.
     */
    List<JAXBElement<?>> getFields();

    /**
     * Sets a list of fields.
     *
     * @param fields fields to set.
     */
    void setFields(List<JAXBElement<?>> fields);
}
