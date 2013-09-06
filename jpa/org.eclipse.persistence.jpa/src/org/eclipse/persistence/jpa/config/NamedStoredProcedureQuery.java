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
package org.eclipse.persistence.jpa.config;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public interface NamedStoredProcedureQuery {

    public QueryHint addQueryHint();
    public StoredProcedureParameter addParameter();
    public NamedStoredProcedureQuery addResultClass(String resultClass);
    public NamedStoredProcedureQuery addResultSetMapping(String resultSetMapping);
    public NamedStoredProcedureQuery setName(String name);
    public NamedStoredProcedureQuery setProcedureName(String procedureName);
    public NamedStoredProcedureQuery setReturnsResult(Boolean returnsResultSet);
    public NamedStoredProcedureQuery setMultipleResultSets(Boolean multipleResultSets);
    public NamedStoredProcedureQuery setCallByIndex(Boolean callByIndex);
  
}
