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
package org.eclipse.persistence.testing.oxm.mappings.sequenced;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.sequenced.SequencedObject;
import org.eclipse.persistence.oxm.sequenced.Setting;

public class PhoneNumber implements SequencedObject {

    private String areaCode;
    private String number;
    private String extension;
    private List<Setting> settings = new ArrayList<Setting>();

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public List<Setting> getSettings() {
        // TODO Auto-generated method stub
        return settings;
    }

    public boolean equals(Object object) {
        try {
            PhoneNumber testPhoneNumber = (PhoneNumber) object;
            if(!Comparer.equals(settings, testPhoneNumber.getSettings())) {
                return false;
            }
            return true;
        } catch(ClassCastException e) {
            return false;
        }
    }

}
