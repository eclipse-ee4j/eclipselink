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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.schematype;

import java.util.List;

import javax.xml.namespace.QName;

public class QNameHolder {
    private QName theQName;
    private List<QName> theQNames;
    private List<QName> theQNames2;

    public boolean equals(Object object) {
        try {
            QNameHolder qnameHolder = (QNameHolder) object;
            if (!this.getTheQName().equals(qnameHolder.getTheQName())) {
                return false;
            }

            if (this.getTheQNames().size() != qnameHolder.getTheQNames().size()) {
                return false;
            }
            for (int i = 0; i < getTheQNames().size(); i++) {
                if (!(getTheQNames().get(i).equals(qnameHolder.getTheQNames()
                        .get(i)))) {
                    return false;
                }
            }

            if (this.getTheQNames2().size() != qnameHolder.getTheQNames2().size()) {
                return false;
            }
            for (int i = 0; i < getTheQNames2().size(); i++) {
                if (!(getTheQNames2().get(i).equals(qnameHolder.getTheQNames2().get(i)))) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

     public String toString()
      {
        String returnString =  "QNameHolder theQName--> ";
        String qnameString = theQName.getNamespaceURI() + ":" + theQName.getLocalPart();
        returnString += qnameString;
        returnString += " theQNames-->";
                for(int i=0; i<theQNames.size(); i++){

                    qnameString = theQNames.get(i).getNamespaceURI() + ":" + theQNames.get(i).getLocalPart();
                    returnString += qnameString;
                }
         returnString += " theQNames2-->";
        for(int i=0; i<theQNames2.size(); i++){
            qnameString = theQNames2.get(i).getNamespaceURI() + ":" + theQNames2.get(i).getLocalPart();
            returnString += qnameString;
        }
            return returnString;
      }

    public QName getTheQName() {
        return theQName;
    }

    public void setTheQName(QName theQName) {
        this.theQName = theQName;
    }

    public List<QName> getTheQNames() {
        return theQNames;
    }

    public void setTheQNames(List<QName> theQNames) {
        this.theQNames = theQNames;
    }

    public List<QName> getTheQNames2() {
        return theQNames2;
    }

    public void setTheQNames2(List<QName> theQNames2) {
        this.theQNames2 = theQNames2;
    }
}
