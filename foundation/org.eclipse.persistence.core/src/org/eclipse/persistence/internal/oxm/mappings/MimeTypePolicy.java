/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan - 2.5 - initial implementation
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
