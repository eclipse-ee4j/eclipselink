/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 

package org.eclipse.persistence.internal.xr;

// Javase imports

//Java extension imports
import javax.xml.namespace.QName;

// TopLink imports
import org.eclipse.persistence.mappings.transformers.FieldTransformerAdapter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Session;

@SuppressWarnings("serial")
public class QNameFieldTransformer extends FieldTransformerAdapter {

    @Override
    public Object buildFieldValue(Object object, String fieldName, Session session) {
        if (object != null) {
          QName qName = null;
          if ("type/text()".equals(fieldName)) {
            if (object instanceof Result) {
              qName = ((Result)object).getType();
            }
            else if (object instanceof Parameter) {
              qName = ((Parameter)object).getType();
            }
            else if (object instanceof ProcedureOutputArgument) {
              qName = ((ProcedureOutputArgument)object).getResultType();
            }
            if (qName != null) {
                XMLDescriptor desc = (XMLDescriptor)session.getDescriptor(object);
                String prefix = desc.getNamespaceResolver().resolveNamespaceURI(qName.getNamespaceURI());
                if (prefix != null) {
                  return prefix + ":" + qName.getLocalPart();
                }
                else {
                  return qName.toString();
                }
            }
          }
        }
        return null;
    }
}