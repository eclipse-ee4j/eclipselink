/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.4.1 - Initial Implementation
package org.eclipse.persistence.testing.jaxb.xmlelements;

import javax.xml.bind.annotation.XmlAttribute;

public class Link {

    @XmlAttribute
    public String href;

    public boolean equals(Object obj) {
        Link link = (Link)obj;
        return href.equals(link.href);
    }
}
