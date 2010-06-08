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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.xdb;

import org.w3c.dom.*;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.platform.database.oracle.Oracle9Platform;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

public class UpdateDocumentTest extends TestCase {
    public UpdateDocumentTest() {
        setDescription("Tests updating an XMLType instance");
    }

    public void setup() {
        if (!(getSession().getPlatform() instanceof Oracle9Platform)) {
            throw new TestWarningException("This test is intended for the Oracle 9 Platform");
        }
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee_XML emp = (Employee_XML)uow.readObject(Employee_XML.class, new ExpressionBuilder().get("firstName").equal("Frank"));
        Document resume = emp.resume;
        // System.out.println(resume);
        NodeList nodes = resume.getElementsByTagName("last-name");
        // System.out.println(nodes);
        Node lastName = nodes.item(0);
        Node lastNameText = lastName.getFirstChild();
        lastNameText.setNodeValue("Williams");
        emp.payroll_xml = "<payroll><salary>50000</salary><pay-period>weekly</pay-period></payroll>";

        uow.commit();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        Employee_XML emp = (Employee_XML)getSession().readObject(Employee_XML.class, new ExpressionBuilder().get("firstName").equal("Frank"));
        if (!(emp.resume.getElementsByTagName("last-name").item(0).getFirstChild().getNodeValue().equals("Williams"))) {
            throw new TestErrorException("Document Not Updated");
        }
    }
}
