/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.annotations;

import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * Enum used with the BatchFetch annotation, or "eclipselink.batch.type" query hint.
 * This can be specified on a mapping or query to configure the type of batch reading to be used.
 * 
 * @see BatchFetch
 * @see ForeignReferenceMapping#setBatchFetch(BatchFetchType)
 * @author James Sutherland
 * @since EclipseLink 2.1
 */
public enum BatchFetchType {
    /**
     * This is the default form of batch reading.
     * The original query's selection criteria is joined with the batch query.
     */
    JOIN,

    /**
     * This uses an SQL EXISTS and a sub-select in the batch query instead of a join.
     * This has the advantage of not requiring an SQL DISTINCT which can have issues
     * with LOBs, or may be more efficient for some types of queries or on some databases.
     */
    EXISTS,

    /**
     * This uses an SQL IN clause in the batch query passing in the source object Ids.
     * This has the advantage of only selecting the objects not already contained in the cache,
     * and can work better with cursors, or if joins cannot be used.
     * This may only work for singleton Ids on some databases.
     */
    IN    
}
