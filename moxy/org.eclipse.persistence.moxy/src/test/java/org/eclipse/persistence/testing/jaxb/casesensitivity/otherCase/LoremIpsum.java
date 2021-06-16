/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.jaxb.casesensitivity.otherCase;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;


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
