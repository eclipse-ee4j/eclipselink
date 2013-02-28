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
 *     tware - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.rs.metadata.model;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class Link {

    public Link() {
    }

    public Link(String rel, String method, String href) {
        this.rel = rel;
        this.method = method;
        this.href = href;
    }

    private String rel;
    private String method;
    private String href;

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