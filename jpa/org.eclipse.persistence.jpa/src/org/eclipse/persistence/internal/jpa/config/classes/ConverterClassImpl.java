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
package org.eclipse.persistence.internal.jpa.config.classes;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ConverterAccessor;
import org.eclipse.persistence.jpa.config.ConverterClass;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class ConverterClassImpl extends MetadataImpl<ConverterAccessor> implements ConverterClass {

    public ConverterClassImpl() {
        super(new ConverterAccessor());
    }
    
    public ConverterClass setClass(String className) {
        getMetadata().setClassName(className);
        return this;
    }

    public ConverterClass setAutoApply(Boolean autoApply) {
        getMetadata().setAutoApply(autoApply);
        return this;
    }

}
