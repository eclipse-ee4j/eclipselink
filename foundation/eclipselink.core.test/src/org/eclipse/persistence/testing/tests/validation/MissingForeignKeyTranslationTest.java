/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.mappings.OneToOneMapping;


//Created by Ian Reid
//Date: Mar 5, 2k3

public class MissingForeignKeyTranslationTest extends ExceptionTest {

    IntegrityChecker orgIntegrityChecker;

    public MissingForeignKeyTranslationTest() {
        setDescription("This tests Missing Foreign Key Translation (TL-ERROR 155)");
    }

    protected void setup() {
        expectedException = DescriptorException.missingForeignKeyTranslation(null, null);
        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    public void reset() {
        if (orgIntegrityChecker != null)
            getSession().setIntegrityChecker(orgIntegrityChecker);
    }


    public void test() {
        OneToOneMapping mapping = new OneToOneMapping();
        RelationalDescriptor descriptor = new RelationalDescriptor();
        mapping.setDescriptor(descriptor);
        mapping.setIsForeignKeyRelationship(false);
        descriptor.setPrimaryKeyFieldName("id");
        try {
            mapping.getOrderedForeignKeyFields();
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}
