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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import java.net.URISyntaxException;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWXmlProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ResourceException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchemaRepository;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;

public abstract class XmlTestProject
    extends TestProject
{

    // ********** constructor/initialization **********

    protected XmlTestProject() {
        super();
    }

    @Override
    protected void initializeProject() {
        super.initializeProject();
        this.initializeSchemas();
        this.initializeDescriptors();
    }

    protected void initializeSchemas() {
        // do nothing
    }

    protected void initializeDescriptors() {
        // do nothing
    }


    // ********** Convenience Methods **********

    public MWXmlProject getProject() {
        return (MWXmlProject) this.getProjectInternal();
    }

    protected void addSchema(String schemaName, String resourceName) {
        try {
            this.schemaRepository().createSchemaFromFile(schemaName, FileTools.resourceFile(resourceName).getPath());
        } catch(ResourceException exception) {
            throw new RuntimeException(exception);
        } catch(URISyntaxException exception) {
            throw new RuntimeException(exception);
        }
    }

    protected void addRootEisDescriptorForTypeNamed(String typeName) {
        ((MWEisProject) this.getProject()).addRootEisDescriptorForType(this.refreshedTypeNamed(typeName));
    }

    protected MWXmlDescriptor xmlDescriptorWithShortName(String name) {
        return (MWXmlDescriptor) this.descriptorWithShortName(name);
    }

    protected MWXmlSchemaRepository schemaRepository() {
        return this.getProject().getSchemaRepository();
    }

}
