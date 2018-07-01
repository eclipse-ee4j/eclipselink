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
//      rbarkhouse - 2013 June 24 - 2.5.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbcontext.notext;

import javax.xml.bind.annotation.XmlValue;

public class TypeWithText {

    @XmlValue
    public String textValue;

}
