/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml;

import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAbstractTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWOXQueryManager;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

public class MWOXTransactionalPolicy extends MWAbstractTransactionalPolicy {

    private MWXmlPrimaryKeyPolicy primaryKeyPolicy;

    // ********** static methods **********

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MWOXTransactionalPolicy.class);
        descriptor.getInheritancePolicy().setParentClass(MWAbstractTransactionalPolicy.class);


        XMLCompositeObjectMapping primaryKeyFieldPolicyMapping = new XMLCompositeObjectMapping();
        primaryKeyFieldPolicyMapping.setAttributeName("primaryKeyPolicy");
        primaryKeyFieldPolicyMapping.setReferenceClass(MWXmlPrimaryKeyPolicy.class);
        primaryKeyFieldPolicyMapping.setXPath("primary-key-policy");
        descriptor.addMapping(primaryKeyFieldPolicyMapping);

        return descriptor;
    }

    // ********** Constructors **********

    /** Default constructor - for TopLink use only. */
    private MWOXTransactionalPolicy() {
        super();
    }

    MWOXTransactionalPolicy(MWOXDescriptor parent) {
        super(parent);
    }

    // **************** Initialization ****************************************

    protected void initialize(Node parent) {
        super.initialize(parent);
        this.primaryKeyPolicy = new MWXmlPrimaryKeyPolicy(this);
    }

    protected void addChildrenTo(List children) {
        super.addChildrenTo(children);
        children.add(this.primaryKeyPolicy);
    }

    @Override
    protected MWLockingPolicy buildLockingPolicy() {
        return new MWOXDescriptorLockingPolicy(this);
    }

    @Override
    protected MWQueryManager buildQueryManager() {
        return new MWOXQueryManager(this);
    }

    public MWXmlPrimaryKeyPolicy getPrimaryKeyPolicy() {
        return this.primaryKeyPolicy;
    }

    // *************** Runtime Conversion ********************

    public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
        this.primaryKeyPolicy.adjustRuntimeDescriptor(runtimeDescriptor);
    }

}
