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
package org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWCompositeEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWRootEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.SPIManager;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.DatasourceLogin;

public final class MWEisProject
    extends MWXmlProject
{
    private volatile MWEisLoginSpec eisLoginSpec;



    // **************** Constructors **************

    /** Default constructor - for TopLink use only. */
    private MWEisProject() {
        super();
    }

    public MWEisProject(String name, String j2cAdapterName, SPIManager spiManager) {
        super(name, spiManager);
        initialize(j2cAdapterName);
    }

    protected void addChildrenTo(List children) {
        super.addChildrenTo(children);
        children.add(this.eisLoginSpec);
    }


    // **************** Initialization ****************************************

    protected void initialize(Node parent)
    {
        super.initialize(parent);
    }

    protected void initialize(String j2cAdapterName)
    {
        this.eisLoginSpec = buildEisLoginSpec(j2cAdapterName);
    }

    protected MWProjectDefaultsPolicy buildDefaultsPolicy() {
        return new MWEisProjectDefaultsPolicy(this);
    }

    protected MWEisLoginSpec buildEisLoginSpec(String j2cAdapterName) {
        return new MWEisLoginSpec(this, j2cAdapterName);
    }


    // ************ Descriptor creation ************

    protected MWDescriptor createDescriptorForType(MWClass type) throws InterfaceDescriptorCreationException {
        if (type.isInterface()) {
            throw new InterfaceDescriptorCreationException(type);
        }

        return new MWCompositeEisDescriptor(this, type, type.fullName());
    }

    public MWRootEisDescriptor addRootEisDescriptorForType(MWClass type) {
        MWRootEisDescriptor descriptor = new MWRootEisDescriptor(this, type, type.fullName());
        this.addDescriptor(descriptor);
        return descriptor;
    }

    // ************ accessors ************

    public MWEisLoginSpec getEisLoginSpec() {
        return this.eisLoginSpec;
    }

    // ************** runtime conversion **********

    protected DatasourceLogin buildRuntimeLogin() {
        return getEisLoginSpec().buildRuntimeLogin();
    }


    // **************** Static Methods *************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MWEisProject.class);
        descriptor.getInheritancePolicy().setParentClass(MWXmlProject.class);

        XMLCompositeObjectMapping eisLoginSpec = new XMLCompositeObjectMapping();
        eisLoginSpec.setAttributeName("eisLoginSpec");
        eisLoginSpec.setGetMethodName("getEisLoginSpecForTopLink");
        eisLoginSpec.setSetMethodName("setEisLoginSpecForTopLink");
        eisLoginSpec.setReferenceClass(MWEisLoginSpec.class);
        eisLoginSpec.setXPath("eis-login");
        descriptor.addMapping(eisLoginSpec);

        return descriptor;
    }

    private MWEisLoginSpec getEisLoginSpecForTopLink() {
        return this.eisLoginSpec;
    }

    private void setEisLoginSpecForTopLink(MWEisLoginSpec loginSpec) {
        if (loginSpec == null) {
            loginSpec = new MWEisLoginSpec(this, MWEisLoginSpec.DEFAULT_ADAPTER_NAME);
        }

        this.eisLoginSpec = loginSpec;
    }
}
