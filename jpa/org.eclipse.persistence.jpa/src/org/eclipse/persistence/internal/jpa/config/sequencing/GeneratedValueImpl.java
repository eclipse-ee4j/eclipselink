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
import org.eclipse.persistence.internal.jpa.metadata.sequencing.GeneratedValueMetadata;
import org.eclipse.persistence.jpa.config.GeneratedValue;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class GeneratedValueImpl extends MetadataImpl<GeneratedValueMetadata> implements GeneratedValue {

    public GeneratedValueImpl() {
        super(new GeneratedValueMetadata());
    }
    
    public GeneratedValue setStrategy(String strategy) {
        getMetadata().setStrategy(strategy);
        return this;
    }

    public GeneratedValue setGenerator(String generator) {
        getMetadata().setGenerator(generator);
        return this;
    }

}
