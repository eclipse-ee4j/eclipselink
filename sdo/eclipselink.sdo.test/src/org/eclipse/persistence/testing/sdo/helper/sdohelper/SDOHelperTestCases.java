/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dmccann - September 15/2009 - 1.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.sdohelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOHelper;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOSequence;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOCopyHelper;
import org.eclipse.persistence.sdo.helper.SDODataFactory;
import org.eclipse.persistence.sdo.helper.SDODataHelper;
import org.eclipse.persistence.sdo.helper.SDOEqualityHelper;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.types.SDOChangeSummaryType;
import org.eclipse.persistence.sdo.types.SDODataObjectType;
import org.eclipse.persistence.sdo.types.SDODataType;
import org.eclipse.persistence.sdo.types.SDOObjectType;
import org.eclipse.persistence.sdo.types.SDOOpenSequencedType;
import org.eclipse.persistence.sdo.types.SDOPropertyType;
import org.eclipse.persistence.sdo.types.SDOTypeType;
import org.eclipse.persistence.sdo.types.SDOWrapperType;
import org.eclipse.persistence.sdo.types.SDOXMLHelperLoadOptionsType;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.HelperProvider;

import junit.framework.TestCase;

public class SDOHelperTestCases extends TestCase {
    HelperContext aHelperContext;
    TypeHelper typeHelper;
    XMLHelper xmlHelper;
    XSDHelper xsdHelper;
    EqualityHelper equalityHelper;
    CopyHelper copyHelper;
    DataFactory dataFactory;
    DataHelper dataHelper;
    DataObject customerTypeDO;
    List types;
    Type teamType;
    DataObject teamDO;

    public void setUp() throws Exception {
        aHelperContext = SDOHelperContext.getHelperContext();
        typeHelper = aHelperContext.getTypeHelper();
        xmlHelper = aHelperContext.getXMLHelper();
        xsdHelper = aHelperContext.getXSDHelper();
        equalityHelper = aHelperContext.getEqualityHelper();
        copyHelper = aHelperContext.getCopyHelper();
        dataFactory = aHelperContext.getDataFactory();
        dataHelper = aHelperContext.getDataHelper();

        Reader rdr = new InputStreamReader(new FileInputStream(new File(
                "./org/eclipse/persistence/testing/sdo/schemas/Team_cs_on_root.xsd")));
        types = xsdHelper.define(rdr, null);
        teamType = typeHelper.getType("http://www.example.org", "Team");
        teamDO = dataFactory.create(teamType);
    }

    /**
     * Also tests getType method.
     */
    public void testUnwrapType() {
        assertTrue(SDOHelper.unwrap(teamType, SDOType.class) instanceof SDOType);
    }

    public void testUnwrapTypeToSDOTypeType() {
        Type typeType = typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);
        assertTrue(SDOHelper.unwrap(typeType, SDOTypeType.class) instanceof SDOTypeType);
    }

    public void testUnwrapTypeToSDOPropertyType() {
        Type propertyType = typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);
        assertTrue(SDOHelper.unwrap(propertyType, SDOPropertyType.class) instanceof SDOPropertyType);
    }

    public void testUnwrapTypeToSDOChangeSummaryType() {
        Type csType = typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);
        assertTrue(SDOHelper.unwrap(csType, SDOChangeSummaryType.class) instanceof SDOChangeSummaryType);
    }
    
    public void testUnwrapTypeToSDODataObjectType() {
        Type doType = typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);
        assertTrue(SDOHelper.unwrap(doType, SDODataObjectType.class) instanceof SDODataObjectType);
    }

    public void testUnwrapTypeToSDODataType() {
        Type dataType = typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.BOOLEAN);
        assertTrue(SDOHelper.unwrap(dataType, SDODataType.class) instanceof SDODataType);
    }
    
    public void testUnwrapTypeToSDOOpenSequencedType() {
        Type osType = typeHelper.getType(SDOConstants.ORACLE_SDO_URL, "OpenSequencedType");
        assertTrue(SDOHelper.unwrap(osType, SDOOpenSequencedType.class) instanceof SDOOpenSequencedType);
    }

    public void testUnwrapTypeToSDOWrapperType() {
        Type wrapperType = new SDOWrapperType(teamType, "MyTeamTypeWrapper", (SDOTypeHelper) typeHelper);
        assertTrue(SDOHelper.unwrap(wrapperType, SDOWrapperType.class) instanceof SDOWrapperType);
    }

    public void testUnwrapTypeToSDOXMLHelperLoadOptionsType() {
        Type loType = new SDOXMLHelperLoadOptionsType((SDOTypeHelper) typeHelper, SDOHelper.getType(teamType));
        assertTrue(SDOHelper.unwrap(loType, SDOXMLHelperLoadOptionsType.class) instanceof SDOXMLHelperLoadOptionsType);
    }

    public void testUnwrapTypeToSDOObjectType() {
        Type sdoObjectType = new SDOObjectType("http://www.example.org", "Team", (SDOTypeHelper) typeHelper);
        assertTrue(SDOHelper.unwrap(sdoObjectType, SDOObjectType.class) instanceof SDOObjectType);
    }

    /**
     * Also tests getProperty method.
     */
    public void testUnwrapProperty() {
        Property prop = teamType.getProperty("employee");
        assertTrue(SDOHelper.unwrap(prop, SDOProperty.class) instanceof SDOProperty);
    }

    /**
     * Also tests getDataObject method.
     */
    public void testUnwrapDataObject() {
        assertTrue(SDOHelper.unwrap(teamDO, SDODataObject.class) instanceof SDODataObject);
    }

    /**
     * Also tests getChangeSummary method.
     */
    public void testCastToSDOChangeSummary() {
        ChangeSummary teamCS = teamDO.getChangeSummary();
        assertTrue(SDOHelper.unwrap(teamCS, SDOChangeSummary.class) instanceof SDOChangeSummary);
    }

    /**
     * Also tests getSequence method.
     */
    public void testUnwrapSequence() {
        Sequence teamSeq = teamDO.getSequence();
        assertTrue(SDOHelper.unwrap(teamSeq, SDOSequence.class) instanceof SDOSequence);
    }

    /**
     * Also tests getHelperContext method.
     */
    public void testUnwrapHelperContext() {
        HelperContext hCtx = HelperProvider.getDefaultContext();
        assertTrue(SDOHelper.unwrap(hCtx, SDOHelperContext.class) instanceof SDOHelperContext);
    }

    /**
     * Also tests getCopyHelper method.
     */
    public void testUnwrapCopyHelper() {
        assertTrue(SDOHelper.unwrap(copyHelper, SDOCopyHelper.class) instanceof SDOCopyHelper);
    }

    /**
     * Also tests getDataFactory method.
     */
    public void testUnwrapDataFactory() {
        assertTrue(SDOHelper.unwrap(dataFactory, SDODataFactory.class) instanceof SDODataFactory);
    }

    /**
     * Also tests getDataHelper method.
     */
    public void testUnwrapDataHelper() {
        assertTrue(SDOHelper.unwrap(dataHelper, SDODataHelper.class) instanceof SDODataHelper);
    }

    /**
     * Also tests getEqualityHelper method.
     */
    public void testUnwrapEqualityHelper() {
        assertTrue(SDOHelper.unwrap(equalityHelper, SDOEqualityHelper.class) instanceof SDOEqualityHelper);
    }

    /**
     * Also tests getTypeHelper method.
     */
    public void testUnwrapTypeHelper() {
        assertTrue(SDOHelper.unwrap(typeHelper, SDOTypeHelper.class) instanceof SDOTypeHelper);
    }

    /**
     * Also tests getXSDHelper method.
     */
    public void testUnwrapXSDHelper() {
        assertTrue(SDOHelper.unwrap(xsdHelper, SDOXSDHelper.class) instanceof SDOXSDHelper);
    }

    /**
     * Also tests getXMLHelper method.
     */
    public void testUnwrapXMLHelper() {
        assertTrue(SDOHelper.unwrap(xmlHelper, SDOXMLHelper.class) instanceof SDOXMLHelper);
    }

    public void testUnwrapXMLHelperToXMLContext() {
        assertTrue(SDOHelper.unwrap(xmlHelper, XMLContext.class) instanceof XMLContext);
    }
}
