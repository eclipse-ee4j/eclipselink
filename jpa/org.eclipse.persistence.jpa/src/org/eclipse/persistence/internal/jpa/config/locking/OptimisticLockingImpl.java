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
package org.eclipse.persistence.internal.jpa.config.locking;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.config.columns.ColumnImpl;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.locking.OptimisticLockingMetadata;
import org.eclipse.persistence.jpa.config.Column;
import org.eclipse.persistence.jpa.config.OptimisticLocking;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class OptimisticLockingImpl extends MetadataImpl<OptimisticLockingMetadata> implements OptimisticLocking {

    public OptimisticLockingImpl() {
        super(new OptimisticLockingMetadata());
        
        getMetadata().setSelectedColumns(new ArrayList<ColumnMetadata>());
    }
    
    public Column addSelectedColumn() {
        ColumnImpl column = new ColumnImpl();
        getMetadata().getSelectedColumns().add(column.getMetadata());
        return column;
    }

    public OptimisticLocking setCascade(Boolean cascade) {
        getMetadata().setCascade(cascade);
        return this;
    }

    public OptimisticLocking setType(String type) {
        getMetadata().setType(type);
        return this;
    }

}
