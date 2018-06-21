/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.2 - initial implementation
 package org.eclipse.persistence.testing.jaxb.attachments.contenttype;

import java.awt.Image;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RootImageStar {

    private String mimeType;
    private Image image;

    @XmlAttribute(namespace = "http://www.w3.org/2005/05/xmlmime", name="contentType")
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @XmlMimeType("image/*")
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}
