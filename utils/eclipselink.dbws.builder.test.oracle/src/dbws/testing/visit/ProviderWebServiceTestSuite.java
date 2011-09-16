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
 *     David McCann - Sept. 15, 2011 - 2.3.1 - Initial Implementation
 ******************************************************************************/
package dbws.testing.visit;

//javase imports
import static dbws.testing.visit.DBWSTestHelper.PACKAGE_NAME;
import static dbws.testing.visit.DBWSTestHelper.PROC5;
import static javax.xml.ws.Service.Mode.MESSAGE;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;

import javax.annotation.PostConstruct;
import javax.wsdl.WSDLException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.PLSQLProcedureOperationModel;
import org.junit.Test;

/**
 * Test DBWSProvider generation.  The initial implementation of this
 * test class will test for correct generation of static imports for 
 * SOAPBinding (SOAP11HTTP_MTOM_BINDING, SOAP12HTTP_BINDING, and
 * SOAP12HTTP_MTOM_BINDING).
 */
@WebServiceProvider(
    targetNamespace = "urn:providerTestService",
    serviceName = "providerTestService",
    portName = "providerTestServicePort"
)
@ServiceMode(MESSAGE)
public class ProviderWebServiceTestSuite extends WebServiceTestSuite implements Provider<SOAPMessage> {

    static final String ENDPOINT_ADDRESS = "http://localhost:9999/providerTest";
    static final String TEST_NAME = "providerTest";
    static final String TEST_NAMESPACE = "urn:providerTest";
    static final String SOAP11HTTP_MTOM_BINDING = "import static javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_MTOM_BINDING";
    static final String SOAP12HTTP_BINDING = "import static javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING";
    static final String SOAP12HTTP_MTOM_BINDING = "import static javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_MTOM_BINDING";

    @PostConstruct
    public void init() {
        super.init();
    }
    
    @Test
    public void testNoSOAPBindingImports() throws WSDLException {
        DBWS_SERVICE_STREAM = new ByteArrayOutputStream();
        DBWS_SCHEMA_STREAM = new ByteArrayOutputStream();
        DBWS_OR_STREAM = new ByteArrayOutputStream();
        DBWS_OX_STREAM = new ByteArrayOutputStream();
        DBWS_WSDL_STREAM = new ByteArrayOutputStream();
        DBWS_PROVIDER_STREAM = new ByteArrayOutputStream();
    	builder = new DBWSBuilder();
        builder.setProjectName(TEST_NAME);
        builder.setTargetNamespace(TEST_NAMESPACE);
        PLSQLProcedureOperationModel p5Model = new PLSQLProcedureOperationModel();
        p5Model.setName(TEST_NAME);
        p5Model.setCatalogPattern(PACKAGE_NAME);
        p5Model.setProcedurePattern(PROC5);
        builder.getOperations().add(p5Model);
        serviceSetup(ENDPOINT_ADDRESS, new ProviderWebServiceTestSuite(), true);
        if (DBWS_PROVIDER_STREAM.toString().contains(SOAP11HTTP_MTOM_BINDING)
        		|| DBWS_PROVIDER_STREAM.toString().contains(SOAP12HTTP_BINDING)
        		|| DBWS_PROVIDER_STREAM.toString().contains(SOAP12HTTP_MTOM_BINDING)) {
        	fail("Unexpected SOAPBinding import encountered");
        }
    }

    @Test
    public void testSOAP12HTTPBindingImport() throws WSDLException {
        DBWS_SERVICE_STREAM = new ByteArrayOutputStream();
        DBWS_SCHEMA_STREAM = new ByteArrayOutputStream();
        DBWS_OR_STREAM = new ByteArrayOutputStream();
        DBWS_OX_STREAM = new ByteArrayOutputStream();
        DBWS_WSDL_STREAM = new ByteArrayOutputStream();
        DBWS_PROVIDER_STREAM = new ByteArrayOutputStream();
    	builder = new DBWSBuilder();
        builder.setProjectName(TEST_NAME);
        builder.setTargetNamespace(TEST_NAMESPACE);
        PLSQLProcedureOperationModel p5Model = new PLSQLProcedureOperationModel();
        p5Model.setName(TEST_NAME);
        p5Model.setCatalogPattern(PACKAGE_NAME);
        p5Model.setProcedurePattern(PROC5);
        builder.getOperations().add(p5Model);
        // set useSOAP12
        builder.useSOAP12();
        serviceSetup(ENDPOINT_ADDRESS, new ProviderWebServiceTestSuite(), true);
        if (DBWS_PROVIDER_STREAM.toString().contains(SOAP11HTTP_MTOM_BINDING)
        		|| DBWS_PROVIDER_STREAM.toString().contains(SOAP12HTTP_MTOM_BINDING)) {
        	fail("Unexpected SOAPBinding import encountered");
        }
        if (!DBWS_PROVIDER_STREAM.toString().contains(SOAP12HTTP_BINDING)) {
        	fail("Expected SOAPBinding import was not found");
        }
    }
    
    @Test
    public void testSOAP12HTTPMTOMBindingImport() throws WSDLException {
        DBWS_SERVICE_STREAM = new ByteArrayOutputStream();
        DBWS_SCHEMA_STREAM = new ByteArrayOutputStream();
        DBWS_OR_STREAM = new ByteArrayOutputStream();
        DBWS_OX_STREAM = new ByteArrayOutputStream();
        DBWS_WSDL_STREAM = new ByteArrayOutputStream();
        DBWS_PROVIDER_STREAM = new ByteArrayOutputStream();
    	builder = new DBWSBuilder();
        builder.setProjectName(TEST_NAME);
        builder.setTargetNamespace(TEST_NAMESPACE);
        PLSQLProcedureOperationModel p5Model = new PLSQLProcedureOperationModel();
        p5Model.setName(TEST_NAME);
        p5Model.setCatalogPattern(PACKAGE_NAME);
        p5Model.setProcedurePattern(PROC5);
        // enable MTOM
        p5Model.setAttachmentType("MTOM");
        builder.getOperations().add(p5Model);
        // set useSOAP12
        builder.useSOAP12();
        serviceSetup(ENDPOINT_ADDRESS, new ProviderWebServiceTestSuite(), true);
        if (DBWS_PROVIDER_STREAM.toString().contains(SOAP11HTTP_MTOM_BINDING)
        		|| DBWS_PROVIDER_STREAM.toString().contains(SOAP12HTTP_BINDING)) {
        	fail("Unexpected SOAPBinding import encountered");
        }
        if (!DBWS_PROVIDER_STREAM.toString().contains(SOAP12HTTP_MTOM_BINDING)) {
        	fail("Expected SOAPBinding import was not found");
        }
    }
    
    @Test
    public void testSOAP11HTTPMTOMBindingImport() throws WSDLException {
        DBWS_SERVICE_STREAM = new ByteArrayOutputStream();
        DBWS_SCHEMA_STREAM = new ByteArrayOutputStream();
        DBWS_OR_STREAM = new ByteArrayOutputStream();
        DBWS_OX_STREAM = new ByteArrayOutputStream();
        DBWS_WSDL_STREAM = new ByteArrayOutputStream();
        DBWS_PROVIDER_STREAM = new ByteArrayOutputStream();
    	builder = new DBWSBuilder();
        builder.setProjectName(TEST_NAME);
        builder.setTargetNamespace(TEST_NAMESPACE);
        PLSQLProcedureOperationModel p5Model = new PLSQLProcedureOperationModel();
        p5Model.setName(TEST_NAME);
        p5Model.setCatalogPattern(PACKAGE_NAME);
        p5Model.setProcedurePattern(PROC5);
        // enable MTOM
        p5Model.setAttachmentType("MTOM");
        builder.getOperations().add(p5Model);
        serviceSetup(ENDPOINT_ADDRESS, new ProviderWebServiceTestSuite(), true);
        if (DBWS_PROVIDER_STREAM.toString().contains(SOAP12HTTP_MTOM_BINDING)
        		|| DBWS_PROVIDER_STREAM.toString().contains(SOAP12HTTP_BINDING)) {
        	fail("Unexpected SOAPBinding import encountered");
        }
        if (!DBWS_PROVIDER_STREAM.toString().contains(SOAP11HTTP_MTOM_BINDING)) {
        	fail("Expected SOAPBinding import was not found");
        }
    }
}