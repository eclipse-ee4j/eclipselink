/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.schematype;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement
public class Root {

    private Date singleDate;
    private List<Date> dateList;

    public Root() {
        dateList = new ArrayList<Date>();
    }

    @XmlPath("single/date/text()")
    @XmlSchemaType(name="date")
    public Date getSingleDate() {
        return singleDate;
    }

    public void setSingleDate(Date singleDate) {
        this.singleDate = singleDate;
    }

    @XmlPath("date/list/text()")
    @XmlSchemaType(name="time")
    public List<Date> getDateList() {
        return dateList;
    }

    public void setDateList(List<Date> dateList) {
        this.dateList = dateList;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != Root.class) {
            return false;
        }
        Root testRoot = (Root) obj;
        if(!equals(singleDate, testRoot.getSingleDate())) {
            return false;
        }
        int dateListSize = dateList.size();
        if(dateListSize != testRoot.getDateList().size()) {
            return false;
        }
        for(int x=0; x<dateListSize; x++) {
            if(!equals(dateList.get(x), testRoot.getDateList().get(x))) {
                return false;
            }
        }
        return true;
    }

    private boolean equals(Date control, Date test) {
        if(null == control) {
            return null == test;
        }
        return control.equals(test);
    }

}
