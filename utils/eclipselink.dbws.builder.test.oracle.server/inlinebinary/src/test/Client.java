package test;

//javase imports
import java.io.IOException;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.w3c.dom.NodeList;

public class Client {
	static final String b0 = "rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAQEBAQEBAQEBAQEBAQEB";
	static final String b1 = "rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAgICAgICAgICAgICAgIC";
	static final String b2 = "rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAwMDAwMDAwMDAwMDAwMD";
	
    static final String SOAP_FINDBYPK_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findByPrimaryKey_inlinebinaryType xmlns=\"urn:inlinebinaryService\">" +
              "<id>2</id>" +
            "</findByPrimaryKey_inlinebinaryType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_FINDALL_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findAll_inlinebinaryType xmlns=\"urn:inlinebinaryService\" />" +
          "</env:Body>" +
        "</env:Envelope>";

    // pass in two args: hostname, port
    public static void main(String[] args) throws SOAPException, TransformerException, IOException {
        if (args.length != 2) {
            System.out.println("Client - requires 2 args: hostname, port");
            System.exit(-1);
        }
        String hostname = args[0];
        String port = args[1];

        QName qname = new QName("urn:inlinebinaryService", "inlinebinaryServicePort");
        Service service = Service.create(new QName("urn:inlinebinary", "inlinebinaryService"));
        service.addPort(qname, SOAPBinding.SOAP11HTTP_BINDING, "http://" + hostname + ":" + port + "/inlinebinary/inlinebinary");
        Dispatch<SOAPMessage> sourceDispatch = service.createDispatch(qname, SOAPMessage.class, Service.Mode.MESSAGE);

        // TEST FINDALL
        // we expect 3 inline binary elements
        SOAPMessage request = createSOAPMessage(SOAP_FINDALL_REQUEST);
    	SOAPMessage response = sourceDispatch.invoke(request);

    	NodeList elements = response.getSOAPBody().getElementsByTagName("b");
    	if (elements.getLength() != 3) {
        	System.out.println("findAll failed:  wrong number of inline binary elements returned - expected [3] but was [" + elements.getLength() + "]");
        	System.exit(-1);
		}
		String inlineBinary = elements.item(0).getTextContent();
		if (inlineBinary == null || !inlineBinary.equals(b0)) {
        	System.out.println("findAll failed:  unexpected inline binary - expected [" + b0 + "] but was [" + inlineBinary + "]");
        	System.exit(-1);
		}
		inlineBinary = elements.item(1).getTextContent();
		if (inlineBinary == null || !inlineBinary.equals(b1)) {
        	System.out.println("findAll failed:  unexpected inline binary - expected [" + b1 + "] but was [" + inlineBinary + "]");
        	System.exit(-1);
		}
		inlineBinary = elements.item(2).getTextContent();
		if (inlineBinary == null || !inlineBinary.equals(b2)) {
        	System.out.println("findAll failed:  unexpected inline binary - expected [" + b2 + "] but was [" + inlineBinary + "]");
        	System.exit(-1);
		}
    	
        // TEST FINDBYPK
        request = createSOAPMessage(SOAP_FINDBYPK_REQUEST);
    	response = sourceDispatch.invoke(request);
    	elements = response.getSOAPBody().getElementsByTagName("b");
    	if (elements.getLength() != 1) {
        	System.out.println("findByPk failed:  wrong number of inline binary elements returned - expected [1] but was [" + elements.getLength() + "]");
        	System.exit(-1);
    	}
		inlineBinary = elements.item(0).getTextContent();
		if (inlineBinary == null || !inlineBinary.equals(b1)) {
        	System.out.println("findByPk failed:  unexpected inline binary - expected [" + b1 + "] but was [" + inlineBinary + "]");
        	System.exit(-1);
		}
    }
    
    static SOAPMessage createSOAPMessage(String message) {
        try {
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage soapMessage = factory.createMessage();
            soapMessage.getSOAPPart().setContent((Source)new StreamSource(new StringReader(message)));
            soapMessage.saveChanges();
            return soapMessage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}