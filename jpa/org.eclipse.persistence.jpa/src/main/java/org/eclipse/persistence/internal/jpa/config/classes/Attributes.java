/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Doug Clarke - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.classes;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.XMLAttributes;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicMapAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ElementCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.IdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.TransformationAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.TransientAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VariableOneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VersionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.structures.ArrayAccessor;
import org.eclipse.persistence.internal.jpa.metadata.structures.StructureAccessor;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class Attributes extends XMLAttributes {

    public Attributes() {

        setIds(new ArrayList<IdAccessor>());
        setBasics(new ArrayList<BasicAccessor>());
        setVersions(new ArrayList<VersionAccessor>());
        setOneToOnes(new ArrayList<OneToOneAccessor>());
        setOneToManys(new ArrayList<OneToManyAccessor>());
        setBasicCollections(new ArrayList<BasicCollectionAccessor>());
        setBasicMaps(new ArrayList<BasicMapAccessor>());
        setArrays(new ArrayList<ArrayAccessor>());
        setElementCollections(new ArrayList<ElementCollectionAccessor>());
        setEmbeddeds(new ArrayList<EmbeddedAccessor>());
        setManyToManys(new ArrayList<ManyToManyAccessor>());
        setManyToOnes(new ArrayList<ManyToOneAccessor>());
        setStructures(new ArrayList<StructureAccessor>());
        setTransformations(new ArrayList<TransformationAccessor>());
        setTransients(new ArrayList<TransientAccessor>());
        setVariableOneToOnes(new ArrayList<VariableOneToOneAccessor>());

    }

}
