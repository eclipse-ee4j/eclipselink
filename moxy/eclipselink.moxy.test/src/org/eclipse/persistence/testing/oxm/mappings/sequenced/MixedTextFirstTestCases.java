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
// bdoughan - June 11/2008 - 1.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.sequenced;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.sequenced.Setting;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class MixedTextFirstTestCases extends XMLMappingTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/sequenced/MixedTextFirst.xml";
    private static final String CONTROL_AREA_CODE = "613";
    private static final String CONTROL_NUMBER = "555-1111";
    private static final String CONTROL_EXTENSION = "1234";

    private static final EmployeeProject EMPLOYEE_PROJECT = new EmployeeProject();

    private PhoneNumber controlPhoneNumber;

    public MixedTextFirstTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(EMPLOYEE_PROJECT);
    }

    public PhoneNumber getControlObject() {
        if(null != controlPhoneNumber) {
            return controlPhoneNumber;
        }
        controlPhoneNumber = new PhoneNumber();

        Setting numberTextSetting = new Setting(null, "text()");
        numberTextSetting.setObject(controlPhoneNumber);
        DatabaseMapping numberMapping = EMPLOYEE_PROJECT.getDescriptor(PhoneNumber.class).getMappingForAttributeName("number");
        numberTextSetting.setMapping(numberMapping);
        numberTextSetting.setValue(CONTROL_NUMBER);
        controlPhoneNumber.getSettings().add(numberTextSetting);

        Setting areaCodeTextSetting = new Setting(null, "text()");
        areaCodeTextSetting.setObject(controlPhoneNumber);
        DatabaseMapping areaCodeMapping = EMPLOYEE_PROJECT.getDescriptor(PhoneNumber.class).getMappingForAttributeName("areaCode");
        areaCodeTextSetting.setMapping(areaCodeMapping);
        areaCodeTextSetting.setValue(CONTROL_AREA_CODE);
        Setting areaCodeSetting = new Setting(null, "area-code");
        areaCodeSetting.addChild(areaCodeTextSetting);
        controlPhoneNumber.getSettings().add(areaCodeSetting);

        Setting extensionTextSetting = new Setting(null, "text()");
        extensionTextSetting.setObject(controlPhoneNumber);
        DatabaseMapping extensionMapping = EMPLOYEE_PROJECT.getDescriptor(PhoneNumber.class).getMappingForAttributeName("extension");
        extensionTextSetting.setMapping(extensionMapping);
        extensionTextSetting.setValue(CONTROL_EXTENSION);
        Setting extensionSetting = new Setting(null, "extension");
        extensionSetting.addChild(extensionTextSetting);
        controlPhoneNumber.getSettings().add(extensionSetting);

        return controlPhoneNumber;
    }

    public void xmlToObjectTest(Object testObject) throws Exception {
        PhoneNumber testPhoneNumber = (PhoneNumber) testObject;
        assertEquals(getControlObject().getSettings().size(), testPhoneNumber.getSettings().size());
        super.xmlToObjectTest(testObject);
    }

}
