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

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.helper.*;
import deprecated.sdk.SDKPlatform;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.sequencing.Sequence;

/**
 * <code>XMLPlatform</code> implements various database platform behaviors in
 * a way that is useful for an XML data store:<ul>
 * <li> Building the calls necessary for XML "sequence numbers".
 * </ul>
 *
 * @see XMLDataReadCall
 * @see XMLDataUpdateCall
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLPlatform extends SDKPlatform {

    /**
     * Default constructor.
     */
    public XMLPlatform() {
        super();
    }

    /**
     * OBSOLETE:
     * Return the name of the element holding the sequence counter.
     * @deprecated use ((XMLSequence)getDefaultSequence()).getCounterElementName() instead
     */
    protected String getSequenceCounterElementName() {
        return this.getSequenceCounterFieldName();
    }

    /**
     * OBSOLETE:
     * Return the name of the element holding the sequence name.
     * @deprecated use ((XMLSequence)getDefaultSequence()).getNameElementName() instead
     */
    protected String getSequenceNameElementName() {
        return this.getSequenceNameFieldName();
    }

    /**
     * OBSOLETE:
     * Return the root element name for the sequence number documents.
     * @deprecated use ((XMLSequence)getDefaultSequence()).getRootElementName() instead
     */
    protected String getSequenceRootElementName() {
        if (getDefaultSequence() instanceof XMLSequence) {
            return ((XMLSequence)getDefaultSequence()).getRootElementName();
        } else {
            throw ValidationException.wrongSequenceType(Helper.getShortClassName(getDefaultSequence()), "getRootElementName");
        }
    }

    /**
     * OBSOLETE:
     * Set the name of the element holding the sequence counter.
     * @deprecated use ((XMLSequence)getDefaultSequence()).setCounterElementName() instead
     */
    protected void setSequenceCounterElementName(String sequenceCounterElementName) {
        this.setSequenceCounterFieldName(sequenceCounterElementName);
    }

    /**
     * OBSOLETE:
     * Set the name of the element holding the sequence name.
     * @deprecated use ((XMLSequence)getDefaultSequence()).setNameElementName() instead
     */
    protected void setSequenceNameElementName(String sequenceNameElementName) {
        this.setSequenceNameFieldName(sequenceNameElementName);
    }

    /**
     * OBSOLETE:
     * Specify the root element name for the sequence numbers.
     * @deprecated use ((XMLSequence)getDefaultSequence()).setRootElementName() instead
     */
    protected void setSequenceRootElementName(String sequenceRootElementName) {
        if (getDefaultSequence() instanceof XMLSequence) {
            ((XMLSequence)getDefaultSequence()).setRootElementName(sequenceRootElementName);
        } else if (!sequenceRootElementName.equals((new XMLSequence()).getRootElementName())) {
            throw ValidationException.wrongSequenceType(Helper.getShortClassName(getDefaultSequence()), "setRootElementName");
        }
    }

    /**
     * INTERNAL:
     * Create platform-default Sequence
     */
    protected Sequence createPlatformDefaultSequence() {
        return new XMLSequence();
    }
}