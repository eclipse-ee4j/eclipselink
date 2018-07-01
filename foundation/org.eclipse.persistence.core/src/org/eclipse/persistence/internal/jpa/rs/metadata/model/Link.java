/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     tware - initial implementation
package org.eclipse.persistence.internal.jpa.rs.metadata.model;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class Link {
    private String rel;
    private String method;
    private String href;

    public Link() {
    }

    public Link(String rel, String method, String href) {
        this.rel = rel;
        this.method = method;
        this.href = href;
    }

    @XmlPath("_link/@rel")
    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    @XmlPath("_link/@method")
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @XmlPath("_link/@href")
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
