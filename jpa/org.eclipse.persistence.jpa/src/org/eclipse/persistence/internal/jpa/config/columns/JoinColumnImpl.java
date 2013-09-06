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
package org.eclipse.persistence.internal.jpa.config.columns;

import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.jpa.config.JoinColumn;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class JoinColumnImpl extends AbstractRelationalColumnImpl<JoinColumnMetadata, JoinColumn> implements JoinColumn {

    public JoinColumnImpl() {
        super(new JoinColumnMetadata());
    }
    
    public JoinColumn setInsertable(Boolean insertable) {
        getMetadata().setInsertable(insertable);
        return this;
    }
    
    public JoinColumn setNullable(Boolean nullable) {
        getMetadata().setNullable(nullable);
        return this;
    }

    public JoinColumn setTable(String table) {
        getMetadata().setTable(table);
        return this;
    }
    
    public JoinColumn setUpdatable(Boolean updatable) {
        getMetadata().setUpdatable(updatable);
        return this;
    }
    
    public JoinColumn setUnique(Boolean unique) {
        getMetadata().setUnique(unique);
        return this;
    }

}
