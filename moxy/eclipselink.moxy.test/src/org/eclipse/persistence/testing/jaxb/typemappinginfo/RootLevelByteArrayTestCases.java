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
 *     Matt MacIvor -  January, 2010
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.w3c.dom.Document;

public class RootLevelByteArrayTestCases extends TypeMappingInfoTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/byteArrayMtom.xml";
    
    private MyAttachmentMarshaller attachmentMarshaller;

    public RootLevelByteArrayTestCases(String name) throws Exception {
        super(name);
        init();
    }
    
    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setupParser();
    
        setTypeMappingInfos(getTypeMappingInfos());
        this.attachmentMarshaller = new MyAttachmentMarshaller();
        jaxbUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
        jaxbMarshaller.setAttachmentMarshaller(this.attachmentMarshaller);
        
        byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6};
        MyAttachmentMarshaller.attachments.put(MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID, bytes);
    }
    
    protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
        if(typeMappingInfos == null) {
            typeMappingInfos = new TypeMappingInfo[1];
            TypeMappingInfo tpi = new TypeMappingInfo();
            tpi.setXmlTagName(new QName("someUri","testTagname"));      
            tpi.setElementScope(ElementScope.Global);
                
            tpi.setType(byte[].class);         
            typeMappingInfos[0] = tpi;          
        }
        return typeMappingInfos;        
    }
        
    protected Object getControlObject() {
        
        byte[] data = new byte[] {1, 2, 3, 4, 5, 6};      
        QName qname = new QName("someUri", "testTagName");
        JAXBElement jaxbElement = new JAXBElement(qname, byte[].class, null);
        jaxbElement.setValue(data);

        return jaxbElement;
    }


    public Map<String, InputStream> getControlSchemaFiles(){                       
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/byteArray.xsd");
        
        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
        controlSchema.put("someUri", instream);
        return controlSchema;
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE;
    }
    
    protected void comparePrimitiveArrays(Object controlValue, Object testValue){
        byte[] bytes1;
        byte[] bytes2;
        try {
            bytes1 = (byte[])controlValue;
            bytes2 = (byte[])testValue;
            
            if(bytes1.length != bytes2.length) {
                fail("control value doesn't match test value: " + controlValue + " - " + testValue);
            }
            for(int i = 0; i < bytes1.length; i++) {
                if(bytes1[i] != bytes2[i]) {
                    fail("control value doesn't match test value: " + controlValue + " - " + testValue);
                }
            }
        } catch(Exception ex) {
            fail("control value doesn't match test value: " + controlValue + " - " + testValue);
        }
    }  
    
    public void objectToXMLDocumentTest(Document testDocument) throws Exception {
        super.objectToXMLDocumentTest(testDocument);
        assertNotNull(this.attachmentMarshaller.getLocalName());
    }    
    
    

}
