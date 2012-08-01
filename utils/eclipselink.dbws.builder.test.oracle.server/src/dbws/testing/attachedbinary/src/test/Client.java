package dbws.testing.attachedbinary.src.test;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

public class Client {
	public static final String TEST = "attachedbinary";
	public static final String TEST_NAMESPACE = "urn:attachedbinary";
	public static final String SERVICE = "attachedbinaryService";
	public static final String SERVICE_NAMESPACE = "urn:attachedbinaryService";
	public static final String SERVICE_PORT = "attachedbinaryServicePort";

	public static final String FIND_ALL = 
		"<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
		"<env:Header />" +
			"<env:Body>" + 
				"<findAll_attachedbinaryType xmlns=\"urn:attachedbinaryService\" />" +
			"</env:Body>" +
		"</env:Envelope>";
    public static final String GET_BLOB_BY_ID =
		"<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
		"<env:Header />" +
			"<env:Body>" +
				"<getBLOBById xmlns=\"urn:attachedbinaryService\">" +
					"<PK>1</PK>" +
				"</getBLOBById>" +
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

        QName qname = new QName("urn:attachedbinaryService", "attachedbinaryServicePort");
        Service service = Service.create(new QName("urn:attachedbinary", "attachedbinaryService"));
        service.addPort(qname, SOAPBinding.SOAP11HTTP_BINDING, "http://" + hostname + ":" + port + "/attachedbinary/attachedbinary");
        Dispatch<SOAPMessage> sourceDispatch = service.createDispatch(qname, SOAPMessage.class, Service.Mode.MESSAGE);

        // TEST FINDALL
        // we expect 3 attachments
		SOAPMessage request = createSOAPMessage(FIND_ALL);
        SOAPMessage result = sourceDispatch.invoke(request);
		
		if (result.countAttachments() != 3) {
        	System.out.println("findAll failed:  wrong number of attachments - expected [3] but was [" + result.countAttachments() + "]");
        	System.exit(-1);
		}

		SOAPElement elt = SOAPFactory.newInstance().createElement("b");
		
		// verify cid:ref1
		elt.addTextNode("cid:ref1");
		AttachmentPart ap = result.getAttachment(elt);
		if (ap == null) {
        	System.out.println("getBlobById failed:  no attachment for [cid:ref1]");
        	System.exit(-1);
		}
		byte[] rawBytes = ap.getRawContentBytes() ;
        if (rawBytes.length != 15) {
        	System.out.println("getBlobById failed:  wrong number of bytes returned - expected [15] but was [" + rawBytes.length + "]");
        	System.exit(-1);
        }
        for (int i = 0; i < rawBytes.length; i++) {
        	if (rawBytes[i] != 1) {
        		System.out.println("getBlobById failed:  wrong byte value returned - expected [1] but was [" + rawBytes[i] + "]");
        		System.exit(-1);
        	}
        }
        elt.removeContents();
		
		// verify cid:ref2
		elt.addTextNode("cid:ref2");
		ap = result.getAttachment(elt);
		if (ap == null) {
        	System.out.println("getBlobById failed:  no attachment for [cid:ref2]");
        	System.exit(-1);
		}
		rawBytes = ap.getRawContentBytes() ;
        if (rawBytes.length != 15) {
        	System.out.println("getBlobById failed:  wrong number of bytes returned - expected [15] but was [" + rawBytes.length + "]");
        	System.exit(-1);
        }
        for (int i = 0; i < rawBytes.length; i++) {
        	if (rawBytes[i] != 2) {
        		System.out.println("getBlobById failed:  wrong byte value returned - expected [2] but was [" + rawBytes[i] + "]");
        		System.exit(-1);
        	}
        }
        elt.removeContents();
		
		// verify cid:ref3
		elt.addTextNode("cid:ref3");
		ap = result.getAttachment(elt);
		if (ap == null) {
        	System.out.println("getBlobById failed:  no attachment for [cid:ref3]");
        	System.exit(-1);
		}
		rawBytes = ap.getRawContentBytes() ;
        if (rawBytes.length != 15) {
        	System.out.println("getBlobById failed:  wrong number of bytes returned - expected [15] but was [" + rawBytes.length + "]");
        	System.exit(-1);
        }
        for (int i = 0; i < rawBytes.length; i++) {
        	if (rawBytes[i] != 3) {
        		System.out.println("getBlobById failed:  wrong byte value returned - expected [3] but was [" + rawBytes[i] + "]");
        		System.exit(-1);
        	}
        }
        elt.removeContents();
        
		// TEST STORED FUNCTION GETBLOBBYID
		// we expect one attachment
		request = createSOAPMessage(GET_BLOB_BY_ID);
        result = sourceDispatch.invoke(request);

		if (result.countAttachments() != 1) {
        	System.out.println("getBlobById failed:  wrong number of attachments - expected [1] but was [" + result.countAttachments() + "]");
        	System.exit(-1);
        }

		elt.addTextNode("cid:ref1");
		ap = result.getAttachment(elt);
		if (ap == null) {
        	System.out.println("getBlobById failed:  no attachment for [cid:ref1]");
        	System.exit(-1);
		}
		rawBytes = ap.getRawContentBytes() ;
        if (rawBytes.length != 15) {
        	System.out.println("getBlobById failed:  wrong number of bytes returned - expected [15] but was [" + rawBytes.length + "]");
        	System.exit(-1);
        }
        for (int i = 0; i < rawBytes.length; i++) {
        	if (rawBytes[i] != 1) {
        		System.out.println("getBlobById failed:  wrong byte value returned - expected [1] but was [" + rawBytes[i] + "]");
        		System.exit(-1);
        	}
        }
	}

    static SOAPMessage createSOAPMessage(String message) {
        SOAPMessage soapMessage;
        try {
            MessageFactory factory = MessageFactory.newInstance();
            soapMessage = factory.createMessage();
            soapMessage.getSOAPPart().setContent((Source)new StreamSource(new StringReader(message)));
            soapMessage.saveChanges();
        } catch (Exception e) {
            e.printStackTrace();
            soapMessage = null;
        }
        return soapMessage;
    }
}