/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.oxm;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;

import javax.xml.namespace.QName;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the Inheritance Policy is handled when used 
 * with the TreeObjectBuilder.</p>
 */

public class InheritanceNodeValue extends NodeValue {
    private InheritancePolicy inheritancePolicy;

    public InheritancePolicy getInheritancePolicy() {
        return inheritancePolicy;
    }

    public void setInheritancePolicy(InheritancePolicy inheritancePolicy) {
        this.inheritancePolicy = inheritancePolicy;
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, null, session, namespaceResolver, marshalContext);
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);

        if (marshalRecord.getLeafElementType() != null) {
            XMLDescriptor xmlDescriptor = (XMLDescriptor) session.getDescriptor(object.getClass());
            XMLSchemaReference xmlRef = xmlDescriptor.getSchemaReference();
            // only interested in global COMPLEX_TYPE
            if (xmlRef.getType() == 1 && xmlRef.isGlobalDefinition()) {
                QName ctxQName = xmlRef.getSchemaContextAsQName(xmlDescriptor.getNamespaceResolver());
                if (ctxQName.equals(marshalRecord.getLeafElementType())) {
                    // don't write out xsi:type attribute
                    marshalRecord.closeStartGroupingElements(groupingFragment);
                } else {
                    // write out the target descriptor's schema context as
                    // xsi:type
                    XMLField xmlField = (XMLField) getInheritancePolicy().getClassIndicatorField();
                    if (xmlField.getLastXPathFragment().isAttribute()) {
                        marshalRecord.put(xmlField, xmlRef.getSchemaContext().substring(1));
                        marshalRecord.closeStartGroupingElements(groupingFragment);
                    } else {
                        marshalRecord.closeStartGroupingElements(groupingFragment);
                        marshalRecord.put(xmlField, xmlRef.getSchemaContext().substring(1));

                    }
                }
                return true;
            }

            // if the user has set the element type, but the target descriptor's
            // schema context in not a global complex type or is null, delegate
            // the work to the inheritance policy
        }

        // no user-set element type, so proceed via inheritance policy
        XMLField xmlField = (XMLField) getInheritancePolicy().getClassIndicatorField();
        if (xmlField.getLastXPathFragment().isAttribute()) {
            getInheritancePolicy().addClassIndicatorFieldToRow(marshalRecord);
            marshalRecord.closeStartGroupingElements(groupingFragment);
        } else {
            marshalRecord.closeStartGroupingElements(groupingFragment);
            getInheritancePolicy().addClassIndicatorFieldToRow(marshalRecord);
        }
        return true;
    }
}