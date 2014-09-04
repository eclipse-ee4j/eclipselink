/*******************************************************************************
 * Copyright (c) 2013, 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     gonural - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.rs.metadata.model;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Represents a link used in JPARS 2.0.
 *
 * @author gonural
 * @since EclipseLink 2.6.0.
 */
public class LinkV2 {
    @XmlAttribute
    private String rel;

    @XmlAttribute
    private String href;

    public LinkV2() {
    }

    public LinkV2(String rel, String href) {
        this.rel = rel;
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}