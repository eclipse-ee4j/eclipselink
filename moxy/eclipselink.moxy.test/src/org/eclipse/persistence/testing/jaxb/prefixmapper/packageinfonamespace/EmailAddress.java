/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/16/2018-2.7.2 Radek Felcman
 *       - 531349 - @XmlSchema Prefix is not honoured if root element is nil
 ******************************************************************************/

package org.eclipse.persistence.testing.jaxb.prefixmapper.packageinfonamespace;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class EmailAddress {

  public EmailAddress() {
    super();
  }

}
