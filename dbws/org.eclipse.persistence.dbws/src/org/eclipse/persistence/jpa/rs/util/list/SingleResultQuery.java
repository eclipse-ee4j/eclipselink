/*******************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Dmitry Kornilov - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util.list;

import javax.xml.bind.JAXBElement;
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
    List<JAXBElement> getFields();

    /**
     * Sets a list of fields.
     *
     * @param fields fields to set.
     */
    void setFields(List<JAXBElement> fields);
}
