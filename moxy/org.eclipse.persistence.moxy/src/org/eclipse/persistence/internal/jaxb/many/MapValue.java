/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith  June 05, 2009 - Initial implementation
package org.eclipse.persistence.internal.jaxb.many;

import javax.xml.bind.annotation.XmlTransient;

/**
 * This abstract class is used to support JAXBContext creation with a Map class.
 * Classes which extend this class will be dynamically generated when a Map class is given to JAXBContext
 */
@XmlTransient
public abstract class MapValue<T> extends ManyValue<Object, T> {

    @XmlTransient
    public boolean isArray(){
        return false;
    }
}
