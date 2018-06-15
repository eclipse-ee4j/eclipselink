/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.perf.moxy.casesensitivity.correctCase;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
