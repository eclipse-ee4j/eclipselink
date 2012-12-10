/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import java.util.List;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractRecord;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;

public interface ObjectBuilder<
    ABSTRACT_RECORD extends CoreAbstractRecord,
    ABSTRACT_SESSION extends CoreAbstractSession,
    MARSHALLER extends Marshaller> {

    public List addExtraNamespacesToNamespaceResolver(Descriptor desc, XMLRecord marshalRecord, CoreAbstractSession session, boolean allowOverride, boolean ignoreEqualResolvers);

    public boolean addXsiTypeAndClassIndicatorIfRequired(XMLRecord record, Descriptor xmlDescriptor, Descriptor referenceDescriptor, Field xmlField, boolean isRootElement);

        public boolean addXsiTypeAndClassIndicatorIfRequired(XMLRecord record, Descriptor xmlDescriptor, Descriptor referenceDescriptor, Field xmlField,
            Object originalObject, Object obj, boolean wasXMLRoot, boolean isRootElement);

    public XMLRecord buildRow(XMLRecord record, Object object, CoreAbstractSession session, MARSHALLER marshaller, XPathFragment rootFragment, WriteType writeType);

    public ABSTRACT_RECORD createRecord(ABSTRACT_SESSION session);
    
    public XPathNode getRootXPathNode();

    public boolean marshalAttributes(MarshalRecord marshalRecord, Object object, CoreAbstractSession session);

    public void removeExtraNamespacesFromNamespaceResolver(XMLRecord marshalRecord, List extraNamespaces, CoreAbstractSession session);

}
