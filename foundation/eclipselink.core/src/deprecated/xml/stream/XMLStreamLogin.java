/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml.stream;


/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>:
 * <p> New login for writing SDK output in to a single stream
 *
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.helper.Helper;
import deprecated.xml.XMLLogin;

public class XMLStreamLogin extends XMLLogin {
    private XMLStreamDatabase xmlStreamDatabase;

    public XMLStreamLogin() {
        super();
        this.xmlStreamDatabase = new XMLStreamDatabase();
    }

    public XMLStreamDatabase getXMLStreamDatabase() {
        return xmlStreamDatabase;
    }

    public void setXMLStreamDatabase(XMLStreamDatabase newXMLStreamDatabase) {
        xmlStreamDatabase = newXMLStreamDatabase;
    }

    /**
     * Set the class of the accessor to be built.
     */
    public void setAccessorClass(Class accessorClass) {
        if (!Helper.classImplementsInterface(accessorClass, XMLStreamAccessor.class)) {
            throw this.invalidAccessClass(XMLStreamAccessor.class, accessorClass);
        }
        super.setAccessorClass(accessorClass);
    }

    public Accessor buildAccessor() {
        XMLStreamAccessor xmlStreamAccessor = (XMLStreamAccessor)super.buildAccessor();
        xmlStreamAccessor.setXMLStreamDatabase(xmlStreamDatabase);
        return xmlStreamAccessor;
    }
}