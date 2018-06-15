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
package org.eclipse.persistence.testing.perf.moxy.casesensitivity.otherCase;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Dummy class for performance testing with case differing from the xml resource.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LoremIpsum {

    // different cases than the xml - use with Insensitive
    @XmlAttribute
    private int LorEm, ipsUm, DoLor, sIT, AMet;
    @XmlElement
    private int conSectetuR, aDiPiscinG, eLIt, PhasEllus,
                conDimentuM, tElLus, tINciDUnt, magNa, Fusce, cursus;

    @XmlElement
    private String Diam, iD, pUlVinar, mauRIs, iacuLis;

}
