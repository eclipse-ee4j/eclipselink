/*
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - November 18/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlattachmentref;

import jakarta.activation.DataHandler;

public class AttTypes {
    //@jakarta.xml.bind.annotation.XmlAttribute
    //@jakarta.xml.bind.annotation.XmlAttachmentRef
    public DataHandler data;

    //@jakarta.xml.bind.annotation.XmlElement
    //@jakarta.xml.bind.annotation.XmlAttachmentRef
    public DataHandler body;

    public Object thing;
}
