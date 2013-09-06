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

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.QueryHintMetadata;
import org.eclipse.persistence.jpa.config.QueryHint;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
@SuppressWarnings("unchecked")
public abstract class AbstractNamedQueryImpl<T extends NamedQueryMetadata, R> extends MetadataImpl<T> {

    public AbstractNamedQueryImpl(T t) {
        super(t);
        
        getMetadata().setHints(new ArrayList<QueryHintMetadata>());
    }
    
    public QueryHint addQueryHint() {
        QueryHintImpl hint = new QueryHintImpl();
        getMetadata().getHints().add(hint.getMetadata());
        return hint;
    }

    public R setName(String name) {
        getMetadata().setName(name);
        return (R) this;
    }
    
    
}
