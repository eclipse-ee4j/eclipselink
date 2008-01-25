/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.oxm.jaxb.compiler;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author    Rick Barkhouse <rick.barkhouse@oracle.com>
 * @since    04/15/2004 10:16:13
 */
public class TopLinkJAXBBindingSchemaCollection {
    private Vector typesafeBindings;
    private Vector bindings;

    // ===========================================================================
    public Vector getTypesafeBindings() {
        return this.typesafeBindings;
    }

    public void setTypesafeBindings(Vector value) {
        this.typesafeBindings = value;
    }

    public Vector getBindings() {
        return this.bindings;
    }

    public void setBindings(Vector value) {
        this.bindings = value;
    }

    public void handleTypesafeEnumerations() {
        if ((this.typesafeBindings == null) || (this.bindings == null)) {
            return;
        }
        HashMap typesafeBindingsMap = new HashMap(this.typesafeBindings.size());
        for (int i = 0; i < this.typesafeBindings.size(); i++) {
            TopLinkJAXBBindingSchema nextBinding = (TopLinkJAXBBindingSchema)this.typesafeBindings.get(i);
            String key = nextBinding.getTypesafeEnumPackage() + "." + nextBinding.getTypesafeEnumClassName();
            typesafeBindingsMap.put(key, nextBinding);
        }

        Enumeration regBindings = this.bindings.elements();
        TopLinkJAXBBindingSchema regSchema;

        while (regBindings.hasMoreElements()) {
            regSchema = (TopLinkJAXBBindingSchema)regBindings.nextElement();
            String simpleTypeName = regSchema.getSimpleTypeName();
            if ((simpleTypeName != null) && !simpleTypeName.equals("")) {
                Object typeSafeBinding = typesafeBindingsMap.get(simpleTypeName);
                if (typeSafeBinding != null) {
                    regSchema.setTypesafeEnumPackage(((TopLinkJAXBBindingSchema)typeSafeBinding).getTypesafeEnumPackage());
                    regSchema.setTypesafeEnumClassName(((TopLinkJAXBBindingSchema)typeSafeBinding).getTypesafeEnumClassName());
                }
            } else {
                if (regSchema.getProperties() != null) {
                    int size = regSchema.getProperties().size();
                    for (int i = 0; i < size; i++) {
                        TopLinkJAXBProperty nextProperty = (TopLinkJAXBProperty)regSchema.getProperties().get(i);
                        String javaTypeName = nextProperty.getJavaTypeName();

                        if ((javaTypeName != null) && !javaTypeName.equals("")) {
                            Object typeSafeBinding = typesafeBindingsMap.get(javaTypeName);
                            if (typeSafeBinding != null) {
                                nextProperty.setTypesafeEnumPackage(((TopLinkJAXBBindingSchema)typeSafeBinding).getTypesafeEnumPackage());
                                nextProperty.setTypesafeEnumClassName(((TopLinkJAXBBindingSchema)typeSafeBinding).getTypesafeEnumClassName());
                                regSchema.setTypesafeEnumPackage(((TopLinkJAXBBindingSchema)typeSafeBinding).getTypesafeEnumPackage());
                                regSchema.setTypesafeEnumClassName(((TopLinkJAXBBindingSchema)typeSafeBinding).getTypesafeEnumClassName());
                            }
                        }
                    }
                }
            }
        }
        this.typesafeBindings = null;
    }
}