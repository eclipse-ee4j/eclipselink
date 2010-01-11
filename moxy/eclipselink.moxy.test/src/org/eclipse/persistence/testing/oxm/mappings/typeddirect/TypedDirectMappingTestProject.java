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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.typeddirect;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.sessions.Project;

public class TypedDirectMappingTestProject extends Project {
    public TypedDirectMappingTestProject() {
        addDescriptor(getTestObjectDescriptor());
    }

    public XMLDescriptor getTestObjectDescriptor() {
        XMLDescriptor testObjectDescriptor = new XMLDescriptor();
        testObjectDescriptor.setJavaClass(org.eclipse.persistence.testing.oxm.mappings.typeddirect.TestObject.class);
        testObjectDescriptor.setDefaultRootElement("test-object");

        XMLDirectMapping base64Mapping = new XMLDirectMapping();
        base64Mapping.setAttributeName("base64");
        base64Mapping.setGetMethodName("getBase64");
        base64Mapping.setSetMethodName("setBase64");
        XMLField field = new XMLField("binary[1]/text()");
        field.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        base64Mapping.setField(field);
        testObjectDescriptor.addMapping(base64Mapping);

        XMLDirectMapping hexMapping = new XMLDirectMapping();
        hexMapping.setAttributeName("hex");
        hexMapping.setGetMethodName("getHex");
        hexMapping.setSetMethodName("setHex");
        XMLField hexfield = new XMLField("binary[2]/text()");
        hexfield.setSchemaType(XMLConstants.HEX_BINARY_QNAME);
        hexMapping.setField(hexfield);
        testObjectDescriptor.addMapping(hexMapping);

        XMLDirectMapping dateMapping = new XMLDirectMapping();
        dateMapping.setAttributeName("date");
        dateMapping.setGetMethodName("getDate");
        dateMapping.setSetMethodName("setDate");
        XMLField datefield = new XMLField("date/text()");
        datefield.setSchemaType(XMLConstants.DATE_QNAME);
        dateMapping.setField(datefield);
        testObjectDescriptor.addMapping(dateMapping);

        XMLDirectMapping timeMapping = new XMLDirectMapping();
        timeMapping.setAttributeName("time");
        timeMapping.setGetMethodName("getTime");
        timeMapping.setSetMethodName("setTime");
        XMLField timefield = new XMLField("time/text()");
        timefield.setSchemaType(XMLConstants.TIME_QNAME);
        timeMapping.setField(timefield);
        testObjectDescriptor.addMapping(timeMapping);

        XMLDirectMapping dateTimeMapping = new XMLDirectMapping();
        dateTimeMapping.setAttributeName("dateTime");
        dateTimeMapping.setGetMethodName("getDateTime");
        dateTimeMapping.setSetMethodName("setDateTime");
        XMLField dateTimefield = new XMLField("date-time/text()");
        dateTimefield.setSchemaType(XMLConstants.DATE_TIME_QNAME);
        dateTimeMapping.setField(dateTimefield);
        testObjectDescriptor.addMapping(dateTimeMapping);

        XMLCompositeDirectCollectionMapping dateVectorMapping = new XMLCompositeDirectCollectionMapping();
        dateVectorMapping.setAttributeName("dateVector");
        dateVectorMapping.setGetMethodName("getDateVector");
        dateVectorMapping.setSetMethodName("setDateVector");
        XMLField dateVectorfield = new XMLField("date-vector/date-element/text()");
        dateVectorfield.setSchemaType(XMLConstants.DATE_QNAME);
        dateVectorMapping.setField(dateVectorfield);
        dateVectorMapping.setAttributeElementClass(java.util.Calendar.class);
        testObjectDescriptor.addMapping(dateVectorMapping);

        XMLCompositeDirectCollectionMapping timeVectorMapping = new XMLCompositeDirectCollectionMapping();
        timeVectorMapping.setAttributeName("timeVector");
        timeVectorMapping.setGetMethodName("getTimeVector");
        timeVectorMapping.setSetMethodName("setTimeVector");
        XMLField timeVectorfield = new XMLField("time-vector/time-element/text()");
        timeVectorfield.setSchemaType(XMLConstants.TIME_QNAME);
        timeVectorMapping.setField(timeVectorfield);
        timeVectorMapping.setAttributeElementClass(java.util.Calendar.class);
        testObjectDescriptor.addMapping(timeVectorMapping);

        XMLCompositeDirectCollectionMapping dateTimeVectorMapping = new XMLCompositeDirectCollectionMapping();
        dateTimeVectorMapping.setAttributeName("dateTimeVector");
        dateTimeVectorMapping.setGetMethodName("getDateTimeVector");
        dateTimeVectorMapping.setSetMethodName("setDateTimeVector");
        XMLField dateTimeVectorfield = new XMLField("date-time-vector/date-time-element/text()");
        dateTimeVectorfield.setSchemaType(XMLConstants.DATE_TIME_QNAME);
        dateTimeVectorMapping.setField(dateTimeVectorfield);
        dateTimeVectorMapping.setAttributeElementClass(java.util.Calendar.class);
        testObjectDescriptor.addMapping(dateTimeVectorMapping);

        XMLCompositeDirectCollectionMapping base64VectorMapping = new XMLCompositeDirectCollectionMapping();
        base64VectorMapping.setAttributeName("base64Vector");
        base64VectorMapping.setGetMethodName("getBase64Vector");
        base64VectorMapping.setSetMethodName("setBase64Vector");
        XMLField base64Vectorfield = new XMLField("base-64-vector/base-64-element/text()");
        base64Vectorfield.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        base64VectorMapping.setField(base64Vectorfield);
        base64VectorMapping.setAttributeElementClass(byte[].class);
        testObjectDescriptor.addMapping(base64VectorMapping);

        XMLCompositeDirectCollectionMapping hexVectorMapping = new XMLCompositeDirectCollectionMapping();
        hexVectorMapping.setAttributeName("hexVector");
        hexVectorMapping.setGetMethodName("getHexVector");
        hexVectorMapping.setSetMethodName("setHexVector");
        XMLField hexVectorfield = new XMLField("hex-vector/hex-element/text()");
        hexVectorfield.setSchemaType(XMLConstants.HEX_BINARY_QNAME);
        hexVectorMapping.setField(hexVectorfield);
        hexVectorMapping.setAttributeElementClass(byte[].class);
        testObjectDescriptor.addMapping(hexVectorMapping);

        XMLDirectMapping unTypedDateMapping = new XMLDirectMapping();
        unTypedDateMapping.setAttributeName("untypedDate");
        unTypedDateMapping.setGetMethodName("getUntypedDate");
        unTypedDateMapping.setSetMethodName("setUntypedDate");
        unTypedDateMapping.setXPath("untyped-date/text()");
        testObjectDescriptor.addMapping(unTypedDateMapping);

        XMLDirectMapping typedDateMapping = new XMLDirectMapping();
        typedDateMapping.setAttributeName("typedDate");
        typedDateMapping.setGetMethodName("getTypedDate");
        typedDateMapping.setSetMethodName("setTypedDate");
        XMLField typedDatefield = new XMLField("typed-date/text()");
        typedDatefield.setSchemaType(XMLConstants.DATE_QNAME);
        typedDateMapping.setField(typedDatefield);
        testObjectDescriptor.addMapping(typedDateMapping);
        
        XMLDirectMapping nextMapping;
        XMLField typedField;
        
        nextMapping = new XMLDirectMapping();
        nextMapping.setAttributeName("untypedSqlDate");
        nextMapping.setGetMethodName("getUntypedSqlDate");
        nextMapping.setSetMethodName("setUntypedSqlDate");
        nextMapping.setXPath("untyped-sql-date/text()");
        testObjectDescriptor.addMapping(nextMapping);        

        nextMapping = new XMLDirectMapping();
        nextMapping.setAttributeName("typedSqlDate");
        nextMapping.setGetMethodName("getTypedSqlDate");
        nextMapping.setSetMethodName("setTypedSqlDate");
        typedField = new XMLField("typed-sql-date/text()");
        typedField.setSchemaType(XMLConstants.DATE_QNAME);
        nextMapping.setField(typedField);
        testObjectDescriptor.addMapping(nextMapping);

        nextMapping = new XMLDirectMapping();
        nextMapping.setAttributeName("untypedTimestamp");
        nextMapping.setGetMethodName("getUntypedTimestamp");
        nextMapping.setSetMethodName("setUntypedTimestamp");
        nextMapping.setXPath("untyped-timestamp/text()");
        testObjectDescriptor.addMapping(nextMapping);        

        nextMapping = new XMLDirectMapping();
        nextMapping.setAttributeName("typedTimestamp");
        nextMapping.setGetMethodName("getTypedTimestamp");
        nextMapping.setSetMethodName("setTypedTimestamp");
        typedField = new XMLField("typed-timestamp/text()");
        typedField.setSchemaType(XMLConstants.TIME_QNAME);
        nextMapping.setField(typedField);
        testObjectDescriptor.addMapping(nextMapping);


        return testObjectDescriptor;
    }
}
