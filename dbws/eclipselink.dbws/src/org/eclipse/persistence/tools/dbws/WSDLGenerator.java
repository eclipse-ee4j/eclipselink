/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

// J2SE imports
import java.io.OutputStream;

// Java extension imports
import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// EclipseLink imports
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
import static org.eclipse.persistence.tools.dbws.Util.THE_INSTANCE_NAME;

public class WSDLGenerator {

    public static final String BINDING_SUFFIX = "_SOAP_HTTP";
    public static final String NS_SCHEMA_PREFIX = "xsd";
    public static final String SERVICE_SUFFIX = "/service";
    public static final String TYPE_SUFFIX = "Type";
    public static final String RESPONSE_SUFFIX = "Response";
    public static final String REQUEST_SUFFIX = "Request";
    public static final String PORT_SUFFIX = "_Interface";
    public static final String TAG_SCHEMA = "schema";
    public static final String TAG_SOAP_ADDRESS = "address";
    public static final String TAG_SOAP_OPERATION = "operation";
    public static final String TAG_SOAP_BODY = "body";
    public static final String TAG_SOAP_BINDING = "binding";
    public static final String SOAP_USE = "literal";
    public static final String SOAP_STYLE = "document";
    public static final String SOAP_TRANSPORT = "http://schemas.xmlsoap.org/soap/http";
    public static final String NS_WSDL_PREFIX = "wsdl";
    public static final String NS_WSDL_SOAP_PREFIX = "soap";
    public static final String NS_WSDL_SOAP = "http://schemas.xmlsoap.org/wsdl/soap/";
    public static final String NS_WSDL = "http://schemas.xmlsoap.org/wsdl/";
    public static final String NS_THIS_PREFIX = "tns";

    protected XRServiceModel xrServiceModel;
    protected String serviceNameSpace;
    protected String wsdlLocationURI;
    protected boolean hasAttachments;
    protected OutputStream os;

    public WSDLGenerator(XRServiceModel xrServiceModel, String wsdlLocationURI, boolean hasAttachments,
        OutputStream os) {
        this.xrServiceModel = xrServiceModel;
        this.wsdlLocationURI = wsdlLocationURI;
        this.hasAttachments = hasAttachments;
        this.os = os;
        serviceNameSpace = "urn:" + xrServiceModel.getName() + SERVICE_SUFFIX;
    }

    public Definition generateWSDL() throws WSDLException {
        WSDLFactory factory = WSDLFactory.newInstance();
        ExtensionRegistry registry = factory.newPopulatedExtensionRegistry();

        Definition def = factory.newDefinition();
        def.setTargetNamespace(serviceNameSpace);
        def.setQName(new QName(serviceNameSpace, xrServiceModel.getName()));
        def.addNamespace(NS_THIS_PREFIX, serviceNameSpace);
        def.addNamespace(NS_SCHEMA_PREFIX, W3C_XML_SCHEMA_NS_URI);
        def.addNamespace(NS_WSDL_SOAP_PREFIX, NS_WSDL_SOAP);
        def.addNamespace(NS_WSDL_PREFIX, NS_WSDL);

        Types types = def.createTypes();
        javax.wsdl.extensions.schema.Schema schema =
            (javax.wsdl.extensions.schema.Schema) registry.createExtension(Types.class, new QName(
                W3C_XML_SCHEMA_NS_URI, TAG_SCHEMA));
        schema.setElement(createInlineSchema());
        types.addExtensibilityElement(schema);
        def.setTypes(types);

        PortType portType = def.createPortType();
        portType.setUndefined(false);
        portType.setQName(new QName(serviceNameSpace, xrServiceModel.getName() + PORT_SUFFIX));
        def.addPortType(portType);

        Binding binding = def.createBinding();
        binding.setUndefined(false);
        binding.setQName(new QName(serviceNameSpace, xrServiceModel.getName() + BINDING_SUFFIX));
        binding.setPortType(portType);
        SOAPBinding soap =
            (SOAPBinding) registry.createExtension(Binding.class, new QName(NS_WSDL_SOAP,
                TAG_SOAP_BINDING));
        soap.setStyle(SOAP_STYLE);
        soap.setTransportURI(SOAP_TRANSPORT);
        binding.addExtensibilityElement(soap);
        def.addBinding(binding);

        Service ws = def.createService();
        ws.setQName(new QName(serviceNameSpace, xrServiceModel.getName()));
        Port port = def.createPort();
        port.setName(xrServiceModel.getName());
        port.setBinding(binding);
        SOAPAddress sa =
            (SOAPAddress) registry.createExtension(Port.class, new QName(NS_WSDL_SOAP,
                TAG_SOAP_ADDRESS));
        sa.setLocationURI(wsdlLocationURI);
        port.addExtensibilityElement(sa);
        ws.addPort(port);
        def.addService(ws);

        for (Operation op : xrServiceModel.getOperationsList()) {
            createMethodDefinition(factory, registry, def, op);
        }

        if (os != null) {
            WSDLWriter writer = factory.newWSDLWriter();
            writer.writeWSDL(def, os);
        }
        return def;
    }

    private void createMethodDefinition(WSDLFactory factory, ExtensionRegistry registry,
        Definition def, Operation operation) throws WSDLException {
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
            def.getPortType(new QName(serviceNameSpace, xrServiceModel.getName() + PORT_SUFFIX));
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
            def.getBinding(new QName(serviceNameSpace, xrServiceModel.getName() + BINDING_SUFFIX));
        BindingOperation bop = def.createBindingOperation();
        bop.setName(operation.getName());
        SOAPOperation so =
            (SOAPOperation) registry.createExtension(BindingOperation.class, new QName(
                NS_WSDL_SOAP, TAG_SOAP_OPERATION));
        so.setSoapActionURI(serviceNameSpace + "/" + op.getName());
        bop.addExtensibilityElement(so);
        BindingInput bi = def.createBindingInput();
        SOAPBody soapInput =
            (SOAPBody) registry.createExtension(BindingInput.class, new QName(NS_WSDL_SOAP,
                TAG_SOAP_BODY));
        soapInput.setUse(SOAP_USE);
        bi.addExtensibilityElement(soapInput);
        bop.setBindingInput(bi);

        if (operation.hasResponse()) {
            BindingOutput bo = def.createBindingOutput();
            SOAPBody soapOutput =
                (SOAPBody) registry.createExtension(BindingOutput.class, new QName(NS_WSDL_SOAP,
                    TAG_SOAP_BODY));
            soapOutput.setUse(SOAP_USE);
            bo.addExtensibilityElement(soapOutput);
            bop.setBindingOutput(bo);
        }
        binding.addBindingOperation(bop);
    }

    @SuppressWarnings("unchecked")
    private org.w3c.dom.Element createInlineSchema() {

        SchemaModelProject project = new SchemaModelProject();
        XMLDescriptor descriptor = (XMLDescriptor) project.getClassDescriptor(Schema.class);
        if (descriptor.getNamespaceResolver() == null) {
            descriptor.setNamespaceResolver(new NamespaceResolver());
        }
        descriptor.getNamespaceResolver().put(NS_THIS_PREFIX, serviceNameSpace);
        descriptor.getNamespaceResolver().put(NS_SCHEMA_PREFIX, W3C_XML_SCHEMA_NS_URI);
        if (hasAttachments) {
            descriptor.getNamespaceResolver().put("ref", "http://ws-i.org/profiles/basic/1.1/xsd");
        }

        Schema schema = new Schema();
        schema.setTargetNamespace(serviceNameSpace);
        schema.setElementFormDefault(true);
        Import parent = new Import();
        parent.setNamespace(serviceNameSpace);
        int idx = wsdlLocationURI.lastIndexOf("/");
        String s = wsdlLocationURI.substring(0, idx);
        parent.setSchemaLocation(s + "/" + xrServiceModel.getName() + "/" + DBWS_SCHEMA_XML);
        schema.getImports().put(parent.getSchemaLocation(), parent);
        if (hasAttachments) {
            Import ref = new Import();
            ref.setNamespace("http://ws-i.org/profiles/basic/1.1/xsd");
            // ref.setSchemaLocation("http://ws-i.org/profiles/basic/1.1/swaref.xsd");
            ref.setSchemaLocation(s + "/" + xrServiceModel.getName() + "/" + "swaref.xsd");
            schema.getImports().put(ref.getSchemaLocation(), ref);
        }

        for (Operation op : xrServiceModel.getOperationsList()) {
            ComplexType requestType = new ComplexType();
            requestType.setName(op.getName() + TYPE_SUFFIX);
            Sequence requestSequence = new Sequence();
            for (Parameter p : op.getParameters()) {
                Element arg = new Element();
                arg.setName(p.getName());
                if (THE_INSTANCE_NAME.equals(p.getName())) {
                    String localPart = p.getType().getLocalPart();
                    String abbreviatedTypeLabel = localPart.substring(0,
                        localPart.indexOf(TYPE_SUFFIX));
                    ComplexType nestedComplexType = new ComplexType();
                    Sequence nestedSequence = new Sequence();
                    nestedComplexType.setSequence(nestedSequence);
                    Element nestedElement = new Element();
                    nestedElement.setName(abbreviatedTypeLabel);
                    nestedElement.setType(localPart);
                    nestedSequence.addElement(nestedElement);
                    arg.setComplexType(nestedComplexType);
                }
                else {
                    arg.setName(p.getName());
                    if (p.getType().getNamespaceURI().equals(W3C_XML_SCHEMA_NS_URI)) {
                        arg.setType(NS_SCHEMA_PREFIX + ":" + p.getType().getLocalPart());
                    }
                    else {
                        arg.setType(p.getType().getLocalPart());
                    }
                }
                requestSequence.addElement(arg);
            }

            requestType.setSequence(requestSequence);
            schema.addTopLevelComplexTypes(requestType);

            Element requestElement = new Element();
            requestElement.setName(op.getName());
            requestElement.setType(NS_THIS_PREFIX + ":" + requestType.getName());
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
                    result.setType("ref:swaRef");
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
                    } else {
                        result.setType(q.getResultType().getLocalPart());
                    }
                    if (q.isCollection()) {
                        result.setMaxOccurs("unbounded");
                    }
                }
                responseSequence.addElement(result);
                responseType.setSequence(responseSequence);
                schema.addTopLevelComplexTypes(responseType);

                Element responseElement = new Element();
                responseElement.setName(op.getName() + RESPONSE_SUFFIX);
                responseElement.setType(NS_THIS_PREFIX + ":" + responseType.getName());
                schema.addTopLevelElement(responseElement);
            }
        }
        XMLContext context = new XMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        return marshaller.objectToXML(schema).getDocumentElement();
    }
}
