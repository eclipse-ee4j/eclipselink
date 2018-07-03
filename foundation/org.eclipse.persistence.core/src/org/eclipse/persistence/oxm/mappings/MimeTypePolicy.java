/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.oxm.mappings;

/**
 * <p>Purpose: </p> A MimeTypePolicy is used in conjunction with an XMLBinaryData/CollectionMapping
 * in order to allow customisation of the mime type for a specific property at runtime rather than
 * at design time. By default, a FixedMimeTypePolicy is used.
 *
 * @see XMLBinaryDataMapping
 * @see XMLBinaryDataCollectionMapping
 * @see FixedMimeTypePolicy
 *
 */
public interface MimeTypePolicy extends org.eclipse.persistence.internal.oxm.mappings.MimeTypePolicy {
    /**
     * return a MIME type string
     * @param anObject - fixed non-dynamic implementors will ignore this parameter
     * @return String
     */
    String getMimeType(Object anObject);

}
