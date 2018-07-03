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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportQuery;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

public abstract class MWAbstractEisReadQuery extends MWAbstractReadQuery {

    private MWEisInteraction eisInteraction;

    // **************** Static methods ****************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MWAbstractEisReadQuery.class);
        descriptor.getInheritancePolicy().setParentClass(MWAbstractReadQuery.class);

        // Aggregate mapping - xml interaction
        XMLCompositeObjectMapping xmlInteractionMapping = new XMLCompositeObjectMapping();
        xmlInteractionMapping.setAttributeName("eisInteraction");
        xmlInteractionMapping.setReferenceClass(MWEisInteraction.class);
        xmlInteractionMapping.setXPath("eis-interaction");
        descriptor.addMapping(xmlInteractionMapping);

        return descriptor;
    }


    // **************** Constructors ****************

    /** Default constructor - for TopLink use only. */
    MWAbstractEisReadQuery() {
        super();
    }

    MWAbstractEisReadQuery(MWEisQueryManager parent, String name) {
        super(parent, name);
    }

    protected void initialize(Node parent) {
        super.initialize(parent);
        this.eisInteraction = new MWEisInteraction(this);
    }


    // ************* Morphing ************

    public Iterator queryTypes() {
        List list = new ArrayList();
        list.add(READ_ALL_QUERY);
        list.add(READ_OBJECT_QUERY);
        return list.iterator();
    }

    public MWReportQuery asReportQuery() {
        throw new UnsupportedOperationException();
    }

    //TODO not sure this will work, need property change notification
    public void initializeFrom(MWReadQuery query) {
        super.initializeFrom(query);
        this.eisInteraction = ((MWAbstractEisReadQuery) query).getEisInteraction();
        this.eisInteraction.setParent(this);
    }

    protected void addChildrenTo(List children) {
        super.addChildrenTo(children);
        children.add(this.eisInteraction);
    }


    // ****************** accessors ****************

    public MWEisInteraction getEisInteraction() {
        return this.eisInteraction;
    }


    // **************** Runtime Conversion ****************

    public DatabaseQuery runtimeQuery() {
        ObjectLevelReadQuery runtimeQuery = (ObjectLevelReadQuery) super.runtimeQuery();
        this.eisInteraction.adjustRuntimeDescriptor(runtimeQuery);
        return runtimeQuery;
    }
}
