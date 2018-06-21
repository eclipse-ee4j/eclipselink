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
//     Denise Smith - August 2013
package org.eclipse.persistence.testing.jaxb.xsitype;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Root {
    @Override
    public boolean equals(Object obj){
        return obj instanceof Root;
    }

    // This is a really terrible hash function. But either this stays, or the overriden equals method must go too.
    @Override
    public int hashCode() {
        return 0xCAFEBABE;
    }
}
