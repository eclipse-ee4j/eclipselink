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
package org.eclipse.persistence.internal.jpa.config.sequencing;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.jpa.config.SequenceGenerator;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class SequenceGeneratorImpl extends MetadataImpl<SequenceGeneratorMetadata> implements SequenceGenerator {

    public SequenceGeneratorImpl() {
        super(new SequenceGeneratorMetadata());
    }
    
    public SequenceGenerator setAllocationSize(Integer allocationSize) {
        getMetadata().setAllocationSize(allocationSize);
        return this;
    }
    
    public SequenceGenerator setCatalog(String catalog) {
        getMetadata().setCatalog(catalog);
        return this;
    }
    
    public SequenceGenerator setInitialValue(Integer initialValue) {
        getMetadata().setInitialValue(initialValue);
        return this;
    }
    
    public SequenceGenerator setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    public SequenceGenerator setSchema(String schema) {
        getMetadata().setSchema(schema);
        return this;
    }
    
    public SequenceGenerator setSequenceName(String sequenceName) {
        getMetadata().setSequenceName(sequenceName);
        return this;
    }

}
