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
//     Matt MacIvor - initial implementation (2.3.3)
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
