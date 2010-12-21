/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - November 24/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs.collectiontype;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

//@XmlAccessorType(XmlAccessType.FIELD)
//@XmlType(name = "", propOrder = {
//    "dateOrTime"
//})
//@XmlRootElement(name = "root")
public class Root {
    //@XmlElementRefs({
    //    @XmlElementRef(name = "date", type = JAXBElement.class),
    //    @XmlElementRef(name = "time", type = JAXBElement.class)
    //})
    protected JAXBElement<XMLGregorianCalendar> [] dateOrTime;

    public JAXBElement<XMLGregorianCalendar> [] getDateOrTime() {
        if (this.dateOrTime == null) {
            return new JAXBElement[ 0 ] ;
        }
        JAXBElement<XMLGregorianCalendar> [] retVal = new JAXBElement[this.dateOrTime.length] ;
        System.arraycopy(this.dateOrTime, 0, retVal, 0, this.dateOrTime.length);
        return (retVal);
    }

    public JAXBElement<XMLGregorianCalendar> getDateOrTime(int idx) {
        if (this.dateOrTime == null) {
            throw new IndexOutOfBoundsException();
        }
        return this.dateOrTime[idx];
    }

    public int getDateOrTimeLength() {
        if (this.dateOrTime == null) {
            return  0;
        }
        return this.dateOrTime.length;
    }

    public void setDateOrTime(JAXBElement<XMLGregorianCalendar> [] values) {
        int len = values.length;
        this.dateOrTime = ((JAXBElement<XMLGregorianCalendar> []) new JAXBElement[len] );
        for (int i = 0; (i<len); i ++) {
            this.dateOrTime[i] = ((JAXBElement<XMLGregorianCalendar> ) values[i]);
        }
    }

    public JAXBElement<XMLGregorianCalendar> setDateOrTime(int idx, JAXBElement<XMLGregorianCalendar> value) {
        return this.dateOrTime[idx] = ((JAXBElement<XMLGregorianCalendar> ) value);
    }
}
