/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//   Denise Smith  - Dec 2012
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SubsetHolder {

    public String name;
    public CoinSubset coin;

    public boolean equals(Object obj){
        if(!(obj instanceof SubsetHolder compare)){
            return false;
        }
        if(!name.equals(compare.name)){
            return false;
        }
        if(!coin.equals(compare.coin)){
            return false;
        }
        return true;
    }
}
