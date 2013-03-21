/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - initial implementation (2.3.3)
 ******************************************************************************/  
package org.eclipse.persistence.oxm;

/**
 * <p><b>Purpose</b>:Provides a means to customise the namespace prefixes used while marshalling
 * An implementation of this class can be set on an instance of XMLMarshaller to allow for 
 * each instance of XMLMarshaller to use different namespace prefixes. 
 */
public abstract class NamespacePrefixMapper extends org.eclipse.persistence.internal.oxm.NamespacePrefixMapper {

    /**
     * Return true if this prefix mapper applies to the media type provided.
     */
    public boolean supportsMediaType(MediaType mediaType) {
        return true;
    }

}