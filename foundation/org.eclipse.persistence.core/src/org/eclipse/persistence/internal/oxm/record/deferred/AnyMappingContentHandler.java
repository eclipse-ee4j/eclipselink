/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.internal.oxm.record.deferred;

import org.xml.sax.SAXException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.record.UnmappedContentHandlerWrapper;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.oxm.unmapped.UnmappedContentHandler;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;

/**
 * <p><b>Purpose</b>: Implementation of DeferredContentHandler for XMLAnyCollectionMappings.
 * <p><b>Responsibilities</b>:<ul>
 * <li> If the element is empty then execute stored events and return control to the original parentRecord
 * <li> If the element has simple content execute stored events and return control to the original parentRecord
 * <li> If the element has complex content execute stored events and return control to a new unmappedhandler
 * </ul>
 */
public class AnyMappingContentHandler extends DeferredContentHandler {
     private boolean usesXMLRoot;
     
     public AnyMappingContentHandler(UnmarshalRecord parentRecord, boolean usesXMLRoot) {
         super(parentRecord);
         this.usesXMLRoot = usesXMLRoot;
     }

     protected void processEmptyElement() throws SAXException {
         processSimpleElement();
     }

     protected void processSimpleElement() throws SAXException {
         if(usesXMLRoot) {
             //Remove the first event as the start element for this element was already process by parentRecord
             getEvents().remove(0);
             executeEvents(getParent());
         } else {
             processComplexElement();
         }
     }

     protected void processComplexElement() throws SAXException {
         getParent().unmappedContent();
         Class unmappedContentHandlerClass = getParent().getUnmarshaller().getUnmappedContentHandlerClass();
         UnmappedContentHandler unmappedContentHandler;
         if (null == unmappedContentHandlerClass) {
             unmappedContentHandler = UnmarshalRecord.DEFAULT_UNMAPPED_CONTENT_HANDLER;
         } else {
             try {
                 PrivilegedNewInstanceFromClass privilegedNewInstanceFromClass = new PrivilegedNewInstanceFromClass(unmappedContentHandlerClass);
                 unmappedContentHandler = (UnmappedContentHandler)privilegedNewInstanceFromClass.run();
             } catch (ClassCastException e) {
                 throw XMLMarshalException.unmappedContentHandlerDoesntImplement(e, unmappedContentHandlerClass.getName());
             } catch (IllegalAccessException e) {
                 throw XMLMarshalException.errorInstantiatingUnmappedContentHandler(e, unmappedContentHandlerClass.getName());
             } catch (InstantiationException e) {
                 throw XMLMarshalException.errorInstantiatingUnmappedContentHandler(e, unmappedContentHandlerClass.getName());
             }
         }
         UnmappedContentHandlerWrapper unmappedContentHandlerWrapper = new UnmappedContentHandlerWrapper(getParent(), unmappedContentHandler);
         executeEvents(unmappedContentHandlerWrapper);
     }
 }
