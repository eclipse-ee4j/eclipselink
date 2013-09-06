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
package org.eclipse.persistence.internal.jpa.config.mappings;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.mappings.AccessMethodsMetadata;
import org.eclipse.persistence.jpa.config.AccessMethods;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class AccessMethodsImpl extends MetadataImpl<AccessMethodsMetadata> implements AccessMethods {

    public AccessMethodsImpl() {
        super(new AccessMethodsMetadata());
    }

    public AccessMethods setGetMethod(String getMethod) {
        getMetadata().setGetMethodName(getMethod);
        return this;
    }

    public AccessMethods setSetMethod(String setMethod) {
        getMetadata().setSetMethodName(setMethod);
        return this;
    }

}
