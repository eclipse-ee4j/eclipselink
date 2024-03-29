/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 05 November 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlpath;

import jakarta.xml.bind.annotation.XmlType;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlType(namespace="http://www.example.org/type")
public class Entry {

    @XmlPath("@type")
    String type;

    String data;

}
