/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - November 18/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlattachmentref;

import javax.activation.DataHandler;

public class AttTypes {
    //@javax.xml.bind.annotation.XmlAttribute
    //@javax.xml.bind.annotation.XmlAttachmentRef
    public DataHandler data;
    
    //@javax.xml.bind.annotation.XmlElement
    //@javax.xml.bind.annotation.XmlAttachmentRef
    public DataHandler body;

    public Object thing;
}
