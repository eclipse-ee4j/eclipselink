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

package org.eclipse.persistence.tools.dbws;

//javase imports
import java.io.OutputStream;

//Java extension imports
import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPFault;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Binding;
import javax.wsdl.extensions.soap12.SOAP12Body;
import javax.wsdl.extensions.soap12.SOAP12Fault;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
//import static 

//EclipseLink imports
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.Any;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Element;
import org.eclipse.persistence.internal.oxm.schema.model.Import;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.oxm.schema.model.Sequence;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.Parameter;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLMarshaller;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.TARGET_NAMESPACE_PREFIX;
import static org.eclipse.persistence.internal.xr.Util.SERVICE_SUFFIX;
import static org.eclipse.persistence.tools.dbws.Util.THE_INSTANCE_NAME;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_PREFIX;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_URI;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_XSD_FILE;

public class WSDLGenerator {

    public static final String BINDING_SUFFIX = "_SOAP_HTTP";
    public static final String EMPTY_RESPONSE = "EmptyResponse";
    public static final String EXCEPTION_SUFFIX = "Exception";
    public static final String FAULT_SUFFIX = "Fault";
    public static final String NS_SCHEMA_PREFIX = "xsd";
    public static final String NS_TNS_PREFIX = "tns";
    public static final String SOAP_11_NAMESPACE_URI = "http://schemas.xmlsoap.org/wsdl/soap/";
    public static final String SOAP_11_NAMESPACE_PREFIX = "soap";
    public static final String SOAP_12_NAMESPACE_URI = "http://schemas.xmlsoap.org/wsdl/soap12/";
    public static final String SOAP_12_NAMESPACE_PREFIX = "soap12";
    public static final String PORT_SUFFIX = "_Interface";
    public static final String REQUEST_SUFFIX = "Request";
    public static final String RESPONSE_SUFFIX = "Response";
    public static final String SOAP_STYLE = "document";
    public static final String SOAP11_HTTP_TRANSPORT = "http://schemas.xmlsoap.org/soap/http";
    public static final String SOAP12_HTTP_TRANSPORT = "http://www.w3.org/2003/05/soap/bindings/HTTP/";
    public static final String SOAP_USE = "literal";
    public static final String TAG_SCHEMA = "schema";
    public static final String TAG_SOAP_ADDRESS = "address";
    public static final String TAG_SOAP_BINDING = "binding";
    public static final String TAG_SOAP_BODY = "body";
    public static final String TAG_SOAP_FAULT = "fault";
    public static final String TAG_SOAP_OPERATION = "operation";
    public static final String TYPE_SUFFIX = "Type";

    protected XRServiceModel xrServiceModel;
    protected NamingConventionTransformer nct;
    protected String serviceName;
    protected String serviceNameSpace;
    protected String importedSchemaNameSpace;
    protected String wsdlLocationURI;
    protected boolean hasAttachments;
    protected OutputStream os;

    public WSDLGenerator(XRServiceModel xrServiceModel, NamingConventionTransformer nct,
        String wsdlLocationURI, boolean hasAttachments, String importedSchemaNameSpace,
        OutputStream os) {
        this.xrServiceModel = xrServiceModel;
        this.nct = nct;
        this.wsdlLocationURI = wsdlLocationURI;
        this.hasAttachments = hasAttachments;
        this.os = os;
        this.importedSchemaNameSpace = importedSchemaNameSpace;
        serviceName = xrServiceModel.getName() + SERVICE_SUFFIX;
        serviceNameSpace = importedSchemaNameSpace + SERVICE_SUFFIX;
    }

    public Definition generateWSDL() throws WSDLException {
        return generateWSDL(false);
    }
    
    public Definition generateWSDL(boolean useSOAP12) throws WSDLException {
        WSDLFactory factory = WSDLFactory.newInstance();
        ExtensionRegistry registry = factory.newPopulatedExtensionRegistry();

        Definition def = factory.newDefinition();
        if (useSOAP12) {
            def.addNamespace(SOAP_12_NAMESPACE_PREFIX, SOAP_12_NAMESPACE_URI);
        }
        else {
            def.addNamespace(SOAP_11_NAMESPACE_PREFIX, SOAP_11_NAMESPACE_URI);
        }
        def.setTargetNamespace(serviceNameSpace);
        def.setQName(new QName(serviceNameSpace, serviceName));
        def.addNamespace(NS_TNS_PREFIX, serviceNameSpace);
        def.addNamespace(TARGET_NAMESPACE_PREFIX, importedSchemaNameSpace);
        def.addNamespace(NS_SCHEMA_PREFIX, W3C_XML_SCHEMA_NS_URI);

        Types types = def.createTypes();
        javax.wsdl.extensions.schema.Schema schema =
            (javax.wsdl.extensions.schema.Schema)registry.createExtension(Types.class, new QName(
                W3C_XML_SCHEMA_NS_URI, TAG_SCHEMA));
        schema.setElement(createInlineSchema());
        types.addExtensibilityElement(schema);
        def.setTypes(types);

        PortType portType = def.createPortType();
        portType.setUndefined(false);
        portType.setQName(new QName(serviceNameSpace, serviceName + PORT_SUFFIX));
        def.addPortType(portType);

        ExtensibilityElement soap = null;
        if (useSOAP12) {
            soap = registry.createExtension(Binding.class, new QName(
                SOAP_12_NAMESPACE_URI, TAG_SOAP_BINDING));
            ((SOAP12Binding)soap).setStyle(SOAP_STYLE);
            ((SOAP12Binding)soap).setTransportURI(SOAP12_HTTP_TRANSPORT);
        }
        else {
            soap = registry.createExtension(Binding.class, new QName(SOAP_11_NAMESPACE_URI,
                TAG_SOAP_BINDING));
            ((SOAPBinding)soap).setStyle(SOAP_STYLE);
            ((SOAPBinding)soap).setTransportURI(SOAP11_HTTP_TRANSPORT);
        }
        Binding binding = def.createBinding();
        binding.setUndefined(false);
        binding.setQName(new QName(serviceNameSpace, serviceName + BINDING_SUFFIX));
        binding.setPortType(portType);
        binding.addExtensibilityElement(soap);
        def.addBinding(binding);

        ExtensibilityElement sa = null;
        if (useSOAP12) {
            sa = registry.createExtension(Port.class, new QName(SOAP_12_NAMESPACE_URI,
                TAG_SOAP_ADDRESS));
            ((SOAP12Address)sa).setLocationURI(wsdlLocationURI);
        }
        else {
            sa = registry.createExtension(Port.class, new QName(SOAP_11_NAMESPACE_URI,
                TAG_SOAP_ADDRESS));
            ((SOAPAddress)sa).setLocationURI(wsdlLocationURI);
        }
        Service ws = def.createService();
        ws.setQName(new QName(serviceNameSpace, serviceName));
        Port port = def.createPort();
        port.setName(serviceName + "Port");
        port.setBinding(binding);
        port.addExtensibilityElement(sa);
        ws.addPort(port);
        def.addService(ws);

        boolean requireEmptyResponseMessages = false;
        for (Operation op : xrServiceModel.getOperationsList()) {
            if (!(op instanceof QueryOperation)) {
                requireEmptyResponseMessages = true;
                break;
            }
        }
        if (requireEmptyResponseMessages) {
            /*
             * Add in empty response message
             * <wsdl:message name="EmptyResponse">
             *   <wsdl:part name="emptyResponse" element="tns:EmptyResponse"></wsdl:part>
             * </wsdl:message>
             */
            Message emptyResponseMessage = def.createMessage();
            emptyResponseMessage.setUndefined(false);
            emptyResponseMessage.setQName(new QName(serviceNameSpace, EMPTY_RESPONSE));
            Part responsePart = def.createPart();
            responsePart.setName("emptyResponse");
            responsePart.setElementName(new QName(serviceNameSpace, EMPTY_RESPONSE));
            emptyResponseMessage.addPart(responsePart);
            def.addMessage(emptyResponseMessage);
            /*
             * Add in Fault message
             * <wsdl:message name="FaultType">
             *   <wsdl:part name="fault" element="tns:FaultType"></wsdl:part>
             * </wsdl:message>
             */
            Message faultMessage = def.createMessage(); 
            faultMessage.setUndefined(false);
            faultMessage.setQName(new QName(serviceNameSpace, FAULT_SUFFIX + TYPE_SUFFIX));
            Part faultPart = def.createPart();
            faultPart.setName("fault");
            faultPart.setElementName(new QName(serviceNameSpace, FAULT_SUFFIX + TYPE_SUFFIX));
            faultMessage.addPart(faultPart);
            def.addMessage(faultMessage);
        }
        for (Operation op : xrServiceModel.getOperationsList()) {
            createMethodDefinition(factory, registry, def, op, useSOAP12);
        }

        if (os != null) {
            WSDLWriter writer = factory.newWSDLWriter();
            writer.writeWSDL(def, os);
        }
        return def;
    }

    private void createMethodDefinition(WSDLFactory factory, ExtensionRegistry registry,
        Definition def, Operation operation, boolean useSOAP12) throws WSDLException {
        Message requestMessage = def.createMessage();
        requestMessage.setUndefined(false);
        requestMessage.setQName(new QName(serviceNameSpace, operation.getName() + REQUEST_SUFFIX));
        Part requestPart = def.createPart();
        requestPart.setName(operation.getName() + REQUEST_SUFFIX);
        requestPart.setElementName(new QName(serviceNameSpace, operation.getName()));
        requestMessage.addPart(requestPart);
        def.addMessage(requestMessage);
        
        Message responseMessage = null;
        if (operation.hasResponse()) {
            responseMessage = def.createMessage();
            responseMessage.setUndefined(false);
            responseMessage.setQName(new QName(serviceNameSpace, operation.getName()
                + RESPONSE_SUFFIX));
            Part responsePart = def.createPart();
            responsePart.setName(operation.getName() + RESPONSE_SUFFIX);
            responsePart.setElementName(new QName(serviceNameSpace, operation.getName()
                + RESPONSE_SUFFIX));
            responseMessage.addPart(responsePart);
            def.addMessage(responseMessage);
        }

        PortType portType =
            def.getPortType(new QName(serviceNameSpace, serviceName + PORT_SUFFIX));
        javax.wsdl.Operation op = def.createOperation();
        op.setUndefined(false);
        op.setName(operation.getName());
        Input input = def.createInput();
        input.setMessage(requestMessage);
        op.setInput(input);
        if (operation.hasResponse()) {
            Output output = def.createOutput();
            output.setMessage(responseMessage);
            op.setOutput(output);
        }
        portType.addOperation(op);

        Binding binding =
            def.getBinding(new QName(serviceNameSpace, serviceName + BINDING_SUFFIX));
        BindingOperation bop = def.createBindingOperation();
        bop.setName(operation.getName());
        ExtensibilityElement so = null;
        if (useSOAP12) {
            so = registry.createExtension(BindingOperation.class, new QName(
                SOAP_12_NAMESPACE_URI, TAG_SOAP_OPERATION));
            ((SOAP12Operation)so).setSoapActionURI(serviceNameSpace + ":" + op.getName());
        }
        else {
            so = registry.createExtension(BindingOperation.class, new QName(
                SOAP_11_NAMESPACE_URI, TAG_SOAP_OPERATION));
            ((SOAPOperation)so).setSoapActionURI(serviceNameSpace + ":" + op.getName());
        }
        bop.addExtensibilityElement(so);
        BindingInput bi = def.createBindingInput();
        ExtensibilityElement soapInputBody = null;
        if (useSOAP12) {
            soapInputBody = registry.createExtension(BindingInput.class, new QName(
                SOAP_12_NAMESPACE_URI, TAG_SOAP_BODY));
            ((SOAP12Body)soapInputBody).setUse(SOAP_USE);
        }
        else {
            soapInputBody = registry.createExtension(BindingInput.class, new QName(
                SOAP_11_NAMESPACE_URI, TAG_SOAP_BODY));
            ((SOAPBody)soapInputBody).setUse(SOAP_USE);
        }
        bi.addExtensibilityElement(soapInputBody);
        bop.setBindingInput(bi);

        if (operation.hasResponse()) {
            BindingOutput bo = def.createBindingOutput();
            ExtensibilityElement soapOutputBody = null;
            if (useSOAP12) {
                soapOutputBody = registry.createExtension(BindingOutput.class, new QName(
                    SOAP_12_NAMESPACE_URI, TAG_SOAP_BODY));
                ((SOAP12Body)soapOutputBody).setUse(SOAP_USE);
            }
            else {
                soapOutputBody = registry.createExtension(BindingOutput.class, new QName(
                    SOAP_11_NAMESPACE_URI, TAG_SOAP_BODY));
                ((SOAPBody)soapOutputBody).setUse(SOAP_USE);
            }
            bo.addExtensibilityElement(soapOutputBody);
            bop.setBindingOutput(bo);
        }
        if (!(operation instanceof QueryOperation)) {
            // non-QueryOperations don't have Responses, but the fault requirements
            // mean we have to create 'dummy' WSDL outputs
            BindingOutput bo = def.createBindingOutput();
            ExtensibilityElement soapOutputBody = null;
            if (useSOAP12) {
                soapOutputBody = registry.createExtension(BindingOutput.class, new QName(
                    SOAP_12_NAMESPACE_URI, TAG_SOAP_BODY));
                ((SOAP12Body)soapOutputBody).setUse(SOAP_USE);
            }
            else {
                soapOutputBody = registry.createExtension(BindingOutput.class, new QName(
                    SOAP_11_NAMESPACE_URI, TAG_SOAP_BODY));
                ((SOAPBody)soapOutputBody).setUse(SOAP_USE);
            }
            bo.addExtensibilityElement(soapOutputBody);
            bop.setBindingOutput(bo);
            // add WSDL fault to binding operations
            BindingFault bindingFault = def.createBindingFault();
            String exceptionName = FAULT_SUFFIX + EXCEPTION_SUFFIX;
            bindingFault.setName(exceptionName);
            ExtensibilityElement soapFaultBody = null;
            if (useSOAP12) {
                soapFaultBody = registry.createExtension(BindingFault.class, new QName(
                    SOAP_12_NAMESPACE_URI, TAG_SOAP_FAULT));
                ((SOAP12Fault)soapFaultBody).setUse(SOAP_USE);
                ((SOAP12Fault)soapFaultBody).setName(exceptionName);
            }
            else {
                soapFaultBody = registry.createExtension(BindingFault.class, new QName(
                    SOAP_11_NAMESPACE_URI, TAG_SOAP_FAULT));
                ((SOAPFault)soapFaultBody).setUse(SOAP_USE);
                ((SOAPFault)soapFaultBody).setName(exceptionName);
            }
            bindingFault.addExtensibilityElement(soapFaultBody);
            bop.addBindingFault(bindingFault);
            Message emptyResponseMessage = def.getMessage(new QName(serviceNameSpace, EMPTY_RESPONSE));
            Output output = def.createOutput();
            output.setName(operation.getName() + EMPTY_RESPONSE);
            output.setMessage(emptyResponseMessage);
            op.setOutput(output);
            Message faultMessage = def.getMessage(new QName(serviceNameSpace, 
                FAULT_SUFFIX + TYPE_SUFFIX));
            Fault fault = def.createFault();
            fault.setMessage(faultMessage);
            fault.setName(exceptionName);
            op.addFault(fault);
        }
        binding.addBindingOperation(bop);
    }

    @SuppressWarnings("unchecked")
    private org.w3c.dom.Element createInlineSchema() {

        SchemaModelProject project = new SchemaModelProject();
        XMLContext context = new XMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLDescriptor descriptor = (XMLDescriptor) project.getClassDescriptor(Schema.class);
        if (descriptor.getNamespaceResolver() == null) {
            descriptor.setNamespaceResolver(new NamespaceResolver());
        }
        descriptor.getNamespaceResolver().put(NS_TNS_PREFIX, serviceNameSpace);
        descriptor.getNamespaceResolver().put(NS_SCHEMA_PREFIX, W3C_XML_SCHEMA_NS_URI);
        if (hasAttachments) {
            descriptor.getNamespaceResolver().put(WSI_SWAREF_PREFIX, WSI_SWAREF_URI);
        }

        Schema schema = new Schema();
        schema.setTargetNamespace(serviceNameSpace);
        schema.setElementFormDefault(true);
        Import parent = new Import();
        parent.setNamespace(importedSchemaNameSpace);
        parent.setSchemaLocation(DBWS_SCHEMA_XML);
        schema.getImports().add(parent);
        if (hasAttachments) {
            Import ref = new Import();
            ref.setNamespace(WSI_SWAREF_URI);
            // ref.setSchemaLocation("http://ws-i.org/profiles/basic/1.1/swaref.xsd"); // later version
            ref.setSchemaLocation(WSI_SWAREF_XSD_FILE);
            schema.getImports().add(ref);
        }
        boolean requireFaultTypeEmptyResponse = false;
        for (Operation op : xrServiceModel.getOperationsList()) {
            String opName = op.getName();
            ComplexType requestType = new ComplexType();
            // extract tableNameAlias from operation name - everything after first underscore _ character
            String tableNameAlias = opName.substring(opName.indexOf('_')+1);
            requestType.setName(opName + REQUEST_SUFFIX + TYPE_SUFFIX);
            Sequence requestSequence = null;
            if (op.getParameters().size() > 0) {
                requestSequence = new Sequence();
                for (Parameter p : op.getParameters()) {
                    Element arg = new Element();
                    arg.setName(p.getName());
                    if (THE_INSTANCE_NAME.equals(p.getName())) {
                        ComplexType nestedComplexType = new ComplexType();
                        Sequence nestedSequence = new Sequence();
                        nestedComplexType.setSequence(nestedSequence);
                        Element nestedElement = new Element();
                        nestedElement.setRef(TARGET_NAMESPACE_PREFIX + ":" + tableNameAlias);
                        nestedSequence.addElement(nestedElement);
                        arg.setComplexType(nestedComplexType);
                    }
                    else {
                        arg.setName(p.getName());
                        if (p.getType().getNamespaceURI().equals(W3C_XML_SCHEMA_NS_URI)) {
                            arg.setType(NS_SCHEMA_PREFIX + ":" + p.getType().getLocalPart());
                        }
                        else if (p.getType().getNamespaceURI().equals(importedSchemaNameSpace)) {
                            arg.setType(TARGET_NAMESPACE_PREFIX + ":" + p.getType().getLocalPart());
                        }
                        else {
                            arg.setType(p.getType().getLocalPart());
                        }
                    }
                    requestSequence.addElement(arg);
                }
                requestType.setSequence(requestSequence);
            }
            schema.addTopLevelComplexTypes(requestType);
            Element requestElement = new Element();
            requestElement.setName(op.getName());
            requestElement.setType(NS_TNS_PREFIX + ":" + requestType.getName());
            schema.addTopLevelElement(requestElement);
            // build response message based on operation type
            if (op instanceof QueryOperation) {
                QueryOperation q = (QueryOperation) op;
                ComplexType responseType = new ComplexType();
                responseType.setName(op.getName() + RESPONSE_SUFFIX + TYPE_SUFFIX);
                Sequence responseSequence = new Sequence();
                Element result = new Element();
                result.setName("result");
                if (q.isAttachment()) {
                    result.setType(WSI_SWAREF_PREFIX + ":" + WSI_SWAREF);
                }
                else if (q.isSimpleXMLFormat() ||
                           q.getResultType().equals(new QName(W3C_XML_SCHEMA_NS_URI, "any"))) {
                    ComplexType anyType = new ComplexType();
                    Sequence anySequence = new Sequence();
                    anySequence.addAny(new Any());
                    anyType.setSequence(anySequence);
                    result.setComplexType(anyType);
                }
                else {
                    if (q.getResultType().getNamespaceURI().equals(W3C_XML_SCHEMA_NS_URI)) {
                        result.setType(NS_SCHEMA_PREFIX + ":" + q.getResultType().getLocalPart());
                    }
                    else {
                        ComplexType nestedComplexType = new ComplexType();
                        Sequence nestedSequence = new Sequence();
                        nestedComplexType.setSequence(nestedSequence);
                        Element nestedElement = new Element();
                        if (!q.isUserDefined()) {
                            nestedElement.setRef(TARGET_NAMESPACE_PREFIX + ":" + tableNameAlias);
                        }
                        else {
                            nestedElement.setRef(TARGET_NAMESPACE_PREFIX + ":" + q.getResultType().getLocalPart());
                        }
                        nestedElement.setMinOccurs("0");
                        if (q.isCollection()) {
                            nestedElement.setMaxOccurs("unbounded");
                        }
                        nestedSequence.addElement(nestedElement);
                        result.setComplexType(nestedComplexType);
                    }
                }
                responseSequence.addElement(result);
                responseType.setSequence(responseSequence);
                schema.addTopLevelComplexTypes(responseType);
                Element responseElement = new Element();
                responseElement.setName(op.getName() + RESPONSE_SUFFIX);
                responseElement.setType(NS_TNS_PREFIX + ":" + responseType.getName());
                schema.addTopLevelElement(responseElement);
            }
            else {
                requireFaultTypeEmptyResponse = true;
            }
        }
        if (requireFaultTypeEmptyResponse) {
            // <element name="EmptyResponse">
            //   <xsd:complexType/>
            // </element>
            Element emptyResponseElement = new Element();
            emptyResponseElement.setName(EMPTY_RESPONSE);
            ComplexType emptyResponseComplexType = new ComplexType();
            emptyResponseElement.setComplexType(emptyResponseComplexType);
            schema.addTopLevelElement(emptyResponseElement);
            // <xsd:element name="FaultType">
            //   <xsd:complexType>
            //     <xsd:sequence>
            //       <xsd:element name="faultCode" type="xsd:string"/> 
            //       <xsd:element name="faultString" type="xsd:string"/> 
            //     </xsd:sequence>
            //   </xsd:complexType>
            // </element>
            Element elementFaultType = new Element();
            elementFaultType.setName(FAULT_SUFFIX + TYPE_SUFFIX);
            ComplexType faultComplexType = new ComplexType();
            elementFaultType.setComplexType(faultComplexType);
            Sequence nestedSequence = new Sequence();
            faultComplexType.setSequence(nestedSequence);
            Element faultCodeElement = new Element();
            faultCodeElement.setName("faultCode");
            faultCodeElement.setMinOccurs("1");
            faultCodeElement.setType(NS_SCHEMA_PREFIX + ":string");
            nestedSequence.addElement(faultCodeElement);
            Element faultStringElement = new Element();
            faultStringElement.setMinOccurs("1");
            faultStringElement.setName("faultString");
            faultStringElement.setType(NS_SCHEMA_PREFIX + ":string");
            nestedSequence.addElement(faultStringElement);
            schema.addTopLevelElement(elementFaultType);
        }
        return marshaller.objectToXML(schema).getDocumentElement();
    }

	public String getServiceName() {
		return serviceName;
	}

	public String getServiceNameSpace() {
		return serviceNameSpace;
	}
}
