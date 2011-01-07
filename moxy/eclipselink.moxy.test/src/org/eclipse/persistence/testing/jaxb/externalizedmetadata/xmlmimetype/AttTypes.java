/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - November 12/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmimetype;

import java.awt.Image;

import javax.activation.DataHandler;
import javax.xml.transform.Source;

@javax.xml.bind.annotation.XmlRootElement(name="att-types")
public class AttTypes {
    public byte[] b;
    public DataHandler d;
    public Source s;
    public Image i; 
}
