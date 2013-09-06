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
 *     Guy Pelletier - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.config.queries;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredProcedureQueryMetadata;
import org.eclipse.persistence.jpa.config.NamedStoredProcedureQuery;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class NamedStoredProcedureQueryImpl extends AbstractStoredQueryImpl<NamedStoredProcedureQueryMetadata, NamedStoredProcedureQuery> implements NamedStoredProcedureQuery {

    public NamedStoredProcedureQueryImpl() {
        super(new NamedStoredProcedureQueryMetadata());
        
        getMetadata().setResultClassNames(new ArrayList<String>());
        getMetadata().setResultSetMappings(new ArrayList<String>());
    }

    public NamedStoredProcedureQuery addResultClass(String resultClass) {
        getMetadata().getResultClassNames().add(resultClass);
        return this;
    }

    public NamedStoredProcedureQuery addResultSetMapping(String resultSetMapping) {
        getMetadata().getResultSetMappings().add(resultSetMapping);
        return this;
    }

    public NamedStoredProcedureQuery setProcedureName(String procedureName) {
        getMetadata().setProcedureName(procedureName);
        return this;
    }

    public NamedStoredProcedureQuery setReturnsResult(Boolean returnsResultSet) {
        getMetadata().setReturnsResultSet(returnsResultSet);
        return this;
    }

    public NamedStoredProcedureQuery setMultipleResultSets(Boolean multipleResultSets) {
        getMetadata().setMultipleResultSets(multipleResultSets);
        return this;
    }
    
}
