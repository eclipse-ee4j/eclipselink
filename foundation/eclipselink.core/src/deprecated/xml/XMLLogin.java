/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml;

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.databaseaccess.Platform;

/**
 * <code>XMLLogin</code> adds a little protocol to make it
 * look like we are dealing with XML documents and elements instead of
 * database rows and fields.
 *
 * @see XMLPlatform
 * @see XMLAccessor
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLLogin extends deprecated.sdk.SDKLogin {

    /**
     * Default constructor.
     */
    public XMLLogin() {
        this(new XMLPlatform());
    }

    /**
     * Construct a login for the specifed platform
     */
    public XMLLogin(Platform platform) {
        super(platform);
    }

    /**
     * Return the platform cast to XML.
     */
    public XMLPlatform getXMLPlatform() {
        return (XMLPlatform)getDatasourcePlatform();
    }

    /**
     * PUBLIC:
     * Return the name of the element in the TopLink sequence documents
     * that holds the current value for a given sequence (e.g. "SEQ_COUNT").
     * TopLink uses the sequence documents to generate unique object IDs.
     */
    public String getSequenceCounterElementName() {
        return this.getXMLPlatform().getSequenceCounterFieldName();
    }

    /**
     * PUBLIC:
     * Return the name of the element in the TopLink sequence documents
     * that holds the name for a given sequence (e.g. "SEQ_NAME").
     * TopLink uses the sequence documents to generate unique object IDs.
     */
    public String getSequenceNameElementName() {
        return this.getXMLPlatform().getSequenceNameElementName();
    }

    /**
     * PUBLIC:
     * Return the name of the root element in the TopLink sequence documents.
     * TopLink uses a sequence documents to generate unique object IDs.
     */
    public String getSequenceRootElementName() {
        return this.getXMLPlatform().getSequenceRootElementName();
    }

    /**
     * Set the class of the accessor to be built.
     */
    public void setAccessorClass(Class accessorClass) {
        if (!Helper.classImplementsInterface(accessorClass, ClassConstants.XMLAccessor_Class)) {
            throw this.invalidAccessClass(ClassConstants.XMLAccessor_Class, accessorClass);
        }
        super.setAccessorClass(accessorClass);
    }

    /**
     * PUBLIC:
     * Set the name of the element in the TopLink sequence documents
     * that holds the current value for a given sequence (e.g. "SEQ_COUNT").
     * TopLink uses the sequence documents to generate unique object IDs.
     */
    public void setSequenceCounterElementName(String name) {
        this.getXMLPlatform().setSequenceCounterFieldName(name);
    }

    /**
     * PUBLIC:
     * Set the name of the element in the TopLink sequence documents
     * that holds the name for a given sequence (e.g. "SEQ_NAME").
     * TopLink uses the sequence documents to generate unique object IDs.
     */
    public void setSequenceNameElementName(String name) {
        this.getXMLPlatform().setSequenceNameFieldName(name);
    }

    /**
     * PUBLIC:
     * Set the name of the root element in the TopLink sequence documents.
     * TopLink uses a sequence documents to generate unique object IDs.
     */
    public void setSequenceRootElementName(String name) {
        this.getXMLPlatform().setSequenceRootElementName(name);
    }
}