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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.directmap.DirectMapMappings;


//Check if DirectMapMapping is written out and read in properly
public class DirectMapMappingTest extends AutoVerifyTestCase {

    DatabaseMapping directMap;
    DatabaseMapping directMapForBatchRead;

    public DirectMapMappingTest() {
        setDescription("Test if DirectMapMapping is written out and read in properly");
    }

    public void test() {
        directMap = getSession().getDescriptor(DirectMapMappings.class).getMappingForAttributeName("directMap");
        directMapForBatchRead = 
                getSession().getDescriptor(DirectMapMappings.class).getMappingForAttributeName("directMapForBatchRead");
    }

    protected void verify() {
        if (directMap == null || directMapForBatchRead == null) {
            throw new TestErrorException("DirectMapMapping does not exist after written out and read in from xml");
        } else if (!(directMap instanceof DirectMapMapping) || 
                   !(directMapForBatchRead instanceof DirectMapMapping)) {
            throw new TestErrorException("DirectMapMapping has been changed to another class after written out and read in from xml");
        } else if (((DirectMapMapping)directMap).getKeyConverter() == null || 
                   !(((DirectMapMapping)directMap).getKeyConverter() instanceof TypeConversionConverter) || 
                   (((TypeConversionConverter)((DirectMapMapping)directMap).getKeyConverter()).getObjectClass() != 
                    Integer.class)) {
            throw new TestErrorException("DirectMapMapping returned a wrong key converter after written out and read in from xml");
        } else if (((DirectMapMapping)directMap).getDirectKeyField() == null || 
                   !(((DirectMapMapping)directMap).getDirectKeyField().getClass() == DatabaseField.class) || 
                   !(((DirectMapMapping)directMap).getDirectKeyField().getName().equals("KEY_FIELD"))) {
            throw new TestErrorException("DirectMapMapping returned a wrong directKeyField after written out and read in from xml");
        }
    }
}
