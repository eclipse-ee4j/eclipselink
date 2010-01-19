/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - January 5/2010 - 2.0.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.inheritance;

//import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.oxm.QNameInheritancePolicy;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.sessions.Project;

public class RootElementProject extends Project {

    public RootElementProject() {
        // For this test the SportsCar descriptor must be added before the Car
        // descriptor.
        addDescriptor(getVehicleDescriptor());
        addDescriptor(getSportsCarDescriptor());
        addDescriptor(getCarDescriptor()); 
    }

    private XMLDescriptor getVehicleDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Vehicle.class);

        // For this test each descriptor in the inheritance hierarchy must have
        // a different default root element.
        xmlDescriptor.setDefaultRootElement("vehicle");

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("xsi", XMLConstants.SCHEMA_INSTANCE_URL);
        xmlDescriptor.setNamespaceResolver(nsResolver);

        xmlDescriptor.getInheritancePolicy().setClassIndicatorFieldName("@xsi:type");
        xmlDescriptor.getInheritancePolicy().addClassIndicator(Vehicle.class, "vehicle");
        xmlDescriptor.getInheritancePolicy().addClassIndicator(Car.class, "car");
        xmlDescriptor.getInheritancePolicy().addClassIndicator(SportsCar.class, "sports-car");

        XMLSchemaReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/vehicle");
        xmlDescriptor.setSchemaReference(schemaReference);

        return xmlDescriptor;
    }

    private XMLDescriptor getCarDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Car.class);

        // For this test each descriptor in the inheritance hierarchy must have
        // a different default root element.
        xmlDescriptor.setDefaultRootElement("car");

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("xsi", XMLConstants.SCHEMA_INSTANCE_URL);
        xmlDescriptor.setNamespaceResolver(nsResolver);

        xmlDescriptor.getInheritancePolicy().setParentClass(Vehicle.class);

        XMLSchemaReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/car");
        xmlDescriptor.setSchemaReference(schemaReference);

        return xmlDescriptor;
    }

    private XMLDescriptor getSportsCarDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(SportsCar.class);

        // For this test each descriptor in the inheritance hierarchy must have
        // a different default root element.
        xmlDescriptor.setDefaultRootElement("sports-car");

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("xsi", XMLConstants.SCHEMA_INSTANCE_URL);
        xmlDescriptor.setNamespaceResolver(nsResolver);

        xmlDescriptor.getInheritancePolicy().setParentClass(Car.class);

        XMLSchemaReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/sports-car");
        xmlDescriptor.setSchemaReference(schemaReference);

        return xmlDescriptor;
    }

}