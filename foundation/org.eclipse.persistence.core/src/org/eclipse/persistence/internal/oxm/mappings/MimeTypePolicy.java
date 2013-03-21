/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.mappings;

/**
 * <p>Purpose: </p> A MimeTypePolicy is used in conjunction with an BinaryData/CollectionMapping
 * in order to allow customization of the mime type for a specific property at runtime rather than
 * at design time.
 */
public interface MimeTypePolicy {

    /**
     * return a MIME type string
     * @param anObject - fixed non-dynamic implementors will ignore this parameter
     * @return String
     */
    public String getMimeType(Object object);

}