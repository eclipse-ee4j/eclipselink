/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
