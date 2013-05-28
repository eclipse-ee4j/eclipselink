/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util.list;

import java.util.List;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;

/**
 * Marker interface for queries returning paged results
 *  
 * @see ReadAllQueryResultCollection
 * @see ReportQueryResultCollection
 *
 */
public interface PageableCollection {

    /**
     * Sets the checks for more.
     *
     * @param hasMore the new checks for more
     */
    public void setHasMore(Boolean hasMore);

    /**
     * Gets the total number of records in the current response.
     *
     * @return the count
     */
    public Integer getCount();

    /**
     * Sets the count.
     *
     * @param count the new count
     */
    public void setCount(Integer count);

    /**
     * Sets the limit.
     *
     * @param limit the new limit
     */
    public void setLimit(Integer limit);

    /**
     * Sets the offset.
     *
     * @param offset the new offset
     */
    public void setOffset(Integer offset);

    /**
     * Sets the links.
     *
     * @param links the new links
     */
    public void setLinks(List<Link> links);
}
