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

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.columns.ForeignKeyMetadata;
import org.eclipse.persistence.jpa.config.ForeignKey;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class ForeignKeyImpl extends MetadataImpl<ForeignKeyMetadata> implements ForeignKey {

    public ForeignKeyImpl() {
        super(new ForeignKeyMetadata());
    }
    
    public ForeignKey setConstraintMode(String constraintMode) {
        getMetadata().setConstraintMode(constraintMode);
        return this;
    }

    public ForeignKey setForeignKeyDefinition(String foreignKeyDefinition) {
        getMetadata().setForeignKeyDefinition(foreignKeyDefinition);
        return this;
    }

    public ForeignKey setName(String name) {
        getMetadata().setName(name);
        return this;
    }

}
