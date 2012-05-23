package test;

//javase imports
import java.io.StringReader;
import java.io.StringWriter;
import org.w3c.dom.Document;

//java eXtension imports
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

//EclipseLink imports
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;

public class Client {

    static XMLComparer comparer = new XMLComparer();
    static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    static XMLParser xmlParser = xmlPlatform.newXMLParser();

    static final String SOAP_COUNTINFO_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<count-info xmlns=\"urn:simplesqlService\"/>" +
          "</env:Body>" +
        "</env:Envelope>";

    // pass in two args: hostname, port
    public static void main(String[] args) throws SOAPException, TransformerException {
        if (args.length != 2) {
            System.out.println("Client - requires 2 args: hostname, port");
            System.exit(-1);
        }
        String hostname = args[0];
        String port = args[1];

        QName qname = new QName("urn:simplesqlService", "simplesqlServicePort");
        Service service = Service.create(new QName("urn:simplesql", "simplesqlService"));
        service.addPort(qname, SOAPBinding.SOAP11HTTP_BINDING,
            "http://" + hostname + ":" + port + "/simplesql/simplesql");
        Dispatch<SOAPMessage> sourceDispatch = service.createDispatch(qname, SOAPMessage.class,
            Service.Mode.MESSAGE);
        SOAPMessage request = createSOAPMessage(SOAP_COUNTINFO_REQUEST);
        SOAPMessage result = sourceDispatch.invoke(request);
        if (result != null) {
            SOAPBody responseBody = result.getSOAPPart().getEnvelope().getBody();
            Document resultDoc = responseBody.extractContentAsDocument();
            resultDoc.normalizeDocument();
            Document controlDoc = xmlParser.parse(new StringReader(SOAP_COUNTINFO_RESPONSE));
            boolean nodeEqual = comparer.isNodeEqual(controlDoc, resultDoc);
            if (!nodeEqual) {
                System.out.println("findByPrimaryKey_simplesqlType response not same as control document");
                System.out.println("resultDoc: \n" + documentToString(resultDoc));
                System.out.println("controlDoc: \n" + documentToString(controlDoc));
                System.exit(-1);
            }
        } else {
            System.out.println("Result is null - test failed");
            System.exit(-1);
        }
    }
    static final String SOAP_COUNTINFO_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<srvc:count-infoResponse xmlns:srvc=\"urn:simplesqlService\">" +
              "<srvc:result>" +
                "<simple-sql xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"simple-xml-format\">" +
                  "<count-info>" +
                    "<COUNT>3</COUNT>" +
                  "</count-info>" +
                "</simple-sql>" +
              "</srvc:result>" +
            "</srvc:count-infoResponse>";

    static SOAPMessage createSOAPMessage(String message) {
        try {
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage soapMessage = factory.createMessage();
            soapMessage.getSOAPPart().setContent(
                (Source)new StreamSource(new StringReader(message)));
            soapMessage.saveChanges();
            return soapMessage;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    static Transformer getTransformer() {
        Transformer transformer = null;
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            transformer = tf.newTransformer();
        }
        catch (TransformerConfigurationException e) {
            /* extremely rare, safe to ignore */
        }
        return transformer;
    }
    static String documentToString(Document doc) {
        DOMSource domSource = new DOMSource(doc);
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.transform(domSource, result);
            return stringWriter.toString();
        } catch (Exception e) {
            // e.printStackTrace();
            return "<empty/>";
        }
    }
}