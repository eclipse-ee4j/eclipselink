/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package commonj.sdo.impl;

import org.eclipse.persistence.sdo.SDOResolvable;
import org.eclipse.persistence.sdo.helper.SDOCopyHelper;
import org.eclipse.persistence.sdo.helper.SDODataFactory;
import org.eclipse.persistence.sdo.helper.SDODataHelper;
import org.eclipse.persistence.sdo.helper.SDOEqualityHelper;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.delegates.SDODataFactoryDelegator;
import org.eclipse.persistence.sdo.helper.delegates.SDOTypeHelperDelegator;
import org.eclipse.persistence.sdo.helper.delegates.SDOXMLHelperDelegator;
import org.eclipse.persistence.sdo.helper.delegates.SDOXSDHelperDelegator;
import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

public class HelperProviderImpl extends HelperProvider {

    private static final SDOXMLHelper xmlHelper = new SDOXMLHelperDelegator();
    private static final SDOTypeHelper typeHelper = new SDOTypeHelperDelegator();
    private static final SDOXSDHelper xsdHelper = new SDOXSDHelperDelegator();
    private static final SDODataFactory dataFactory = new SDODataFactoryDelegator();
    private static final SDODataHelper dataHelper = new SDODataHelper();
    private static final SDOCopyHelper copyHelper = new SDOCopyHelper();
    private static final SDOEqualityHelper equalityHelper = new SDOEqualityHelper();

    public HelperProviderImpl() {
		super();
	}

    public CopyHelper copyHelper() {
        return copyHelper;
    }

    public DataFactory dataFactory() {
        return dataFactory;
    }

    public DataHelper dataHelper() {
        return dataHelper;
    }

    public EqualityHelper equalityHelper() {
        return equalityHelper;
    }

    public TypeHelper typeHelper() {
        return typeHelper;
    }

    public XMLHelper xmlHelper() {
        return xmlHelper;
    }

    public XSDHelper xsdHelper() {
        return xsdHelper;
    }

    /**
     * This class handles resolving objects from a deserialized stream for
     * Reading
     */
    public ExternalizableDelegator.Resolvable resolvable() {
        return new SDOResolvable(HelperProvider.getDefaultContext());
    }

    /**
     * This class handles custom serialization of target objects for
     * Writing
     */
    public ExternalizableDelegator.Resolvable resolvable(Object target) {
        return new SDOResolvable(target, HelperProvider.getDefaultContext());
    }
}
