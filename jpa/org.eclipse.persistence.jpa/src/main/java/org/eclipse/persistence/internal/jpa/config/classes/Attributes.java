/*
 * Copyright (c) 2013, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Doug Clarke - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.classes;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.XMLAttributes;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class Attributes extends XMLAttributes {

    public Attributes() {

        setIds(new ArrayList<>());
        setBasics(new ArrayList<>());
        setVersions(new ArrayList<>());
        setOneToOnes(new ArrayList<>());
        setOneToManys(new ArrayList<>());
        setBasicCollections(new ArrayList<>());
        setBasicMaps(new ArrayList<>());
        setArrays(new ArrayList<>());
        setElementCollections(new ArrayList<>());
        setEmbeddeds(new ArrayList<>());
        setManyToManys(new ArrayList<>());
        setManyToOnes(new ArrayList<>());
        setStructures(new ArrayList<>());
        setTransformations(new ArrayList<>());
        setTransients(new ArrayList<>());
        setVariableOneToOnes(new ArrayList<>());

    }

}
