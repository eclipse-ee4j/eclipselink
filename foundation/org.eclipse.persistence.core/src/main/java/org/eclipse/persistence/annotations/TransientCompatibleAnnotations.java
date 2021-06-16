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
//     tware - added in fix for bug 277550
package org.eclipse.persistence.annotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PUBLIC:
 *
 * This class is used by our JPA annotation processing to discover which annotations may coexist with a
 * jakarta.persistence.Transient annotation.  If jakarta.persistence.Transient appears on a field or property with an
 * annotation in the jakarta.persistence or org.eclipse.persistence package that is not in the list returned by getTransientCompatibleAnnotations()
 * an exception will be thrown.
 * @author tware
 *
 */
public class TransientCompatibleAnnotations {

    private static final List<String> transientCompatibleAnnotations = Collections.unmodifiableList(new ArrayList<String>() {{
        add("jakarta.persistence.PersistenceUnits");
        add("jakarta.persistence.PersistenceUnit");
        add("jakarta.persistence.PersistenceContext");
        add("jakarta.persistence.PersistenceContexts");
        add("jakarta.persistence.Access");
        add("jakarta.persistence.Transient");
    }});

    /**
     * PUBLIC:
     * Return a list of classnames of annotations that are compatible with the jakarta.persistence.Transient
     * annotation.
     * @return
     */
    public static List<String> getTransientCompatibleAnnotations(){
        return transientCompatibleAnnotations;
    }

}
