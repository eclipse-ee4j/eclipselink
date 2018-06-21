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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWReferenceObjectMapping;


public interface MWXmlReferenceMapping extends MWXmlMapping, MWReferenceObjectMapping {

    public final static String XML_FIELD_PAIRS_LIST = "xmlFieldPairs";

    public MWXmlDescriptor referenceDescriptor();

    public ListIterator<MWXmlFieldPair> xmlFieldPairs();

    public int xmlFieldPairsSize();

    public MWXmlFieldPair xmlFieldPairAt(int index);

    public MWXmlFieldPair addFieldPair(String sourceXpath, String targetXpath);

    public void addFieldPair(MWXmlFieldPair xmlFieldPair);

    public MWXmlFieldPair buildEmptyFieldPair();

    public void removeXmlFieldPair(MWXmlFieldPair xmlFieldPair);

    public void clearXmlFieldPairs();

    public boolean sourceFieldMayUseCollectionXpath();

    public MWDescriptor getReferenceDescriptor();

    public void setReferenceDescriptor(MWDescriptor newValue);

}
