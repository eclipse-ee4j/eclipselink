/*
 * Copyright (c) 2014, 2020 Oracle and/or its affiliates. All rights reserved.
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
//     Marcel Valovy - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.perf.moxy.casesensitivity.correctCase;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Dummy class for performance testing, in case corresponding with the xml resource.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LoremIpsum {

    // same cases as the xml - use with Sensitive
    @XmlAttribute
    private int Lorem, ipsum, dolor, sit, amet;

    @XmlElement
    private int consectetur, adipiscing, elit, Phasellus,
            condimentum, tellus, tincidunt, magna, Fusce, cursus;

    @XmlElement
    private String diam, id, pulvinar, Mauris, iaculis;

}
