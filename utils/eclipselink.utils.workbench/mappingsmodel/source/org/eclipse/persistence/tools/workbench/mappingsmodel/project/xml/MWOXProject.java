/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.SPIManager;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.sessions.DatasourceLogin;


public final class MWOXProject
    extends MWXmlProject
{
    // **************** Static Methods ****************************************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MWOXProject.class);
        descriptor.getInheritancePolicy().setParentClass(MWXmlProject.class);
        return descriptor;
    }


    // **************** Constructors ******************************************

    /** Default constructor - for TopLink use only */
    private MWOXProject() {
        super();
    }

    public MWOXProject(String name, SPIManager spiManager) {
        super(name, spiManager);
    }


    // **************** Initialization ****************************************

    protected MWProjectDefaultsPolicy buildDefaultsPolicy() {
        return new MWOXProjectDefaultsPolicy(this);
    }


    // **************** Descriptor creation ************************************

    protected MWDescriptor createDescriptorForType(MWClass type) throws InterfaceDescriptorCreationException {

        if (type.isInterface()) {
            throw new InterfaceDescriptorCreationException(type);
        }
        return new MWOXDescriptor(this, type, type.fullName());
    }



    //    ************ Unsupported EJB functionality ************

    public void removeEjbInformation()
    {
        throw new UnsupportedOperationException();
    }

    public void removeEjb20Information()
    {
        throw new UnsupportedOperationException();
    }

    public void refreshEjb20Information()
    {
        throw new UnsupportedOperationException();
    }

    // ************** runtime conversion **********

    protected DatasourceLogin buildRuntimeLogin() {
        return new XMLLogin();
    }
}
