/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith  June 05, 2009 - Initial implementation
package org.eclipse.persistence.internal.jaxb.many;

import jakarta.xml.bind.annotation.XmlTransient;

/**
 * This abstract class is used to support JAXBContext creation with a Map class.
 * Classes which extend this class will be dynamically generated when a Map class is given to JAXBContext
 */
@XmlTransient
public abstract class MapValue<T> extends ManyValue<Object, T> {

    @Override
    @XmlTransient
    public boolean isArray(){
        return false;
    }
}
