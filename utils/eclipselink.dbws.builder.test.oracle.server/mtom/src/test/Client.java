package test;

//javase imports
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

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
    static final String SOAP_FINDBYPK_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findByPrimaryKey_mtomType xmlns=\"urn:mtomService\" xmlns:urn=\"urn:mtom\">" +
              "<id>3</id>" +
            "</findByPrimaryKey_mtomType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_FINDALL_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findAll_mtomType xmlns=\"urn:mtomService\" xmlns:urn=\"urn:mtom\"/>" +
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

        QName qname = new QName("urn:mtomService", "mtomServicePort");
        Service service = Service.create(new QName("urn:mtom", "mtomService"));
        service.addPort(qname, SOAPBinding.SOAP12HTTP_BINDING, "http://" + hostname + ":" + port + "/mtom/mtom");
        Dispatch<SOAPMessage> sourceDispatch = service.createDispatch(qname, SOAPMessage.class, Service.Mode.MESSAGE);

        // TEST FINDALL
        // we expect 3 attachments
        SOAPMessage request = createSOAPMessage(SOAP_FINDALL_REQUEST);
    	SOAPMessage response = sourceDispatch.invoke(request);
		if (response.countAttachments() != 3) {
        	System.out.println("findAll failed:  wrong number of attachments - expected [3] but was [" + response.countAttachments() + "]");
        	System.exit(-1);
		}
		// verify MTOM format, i.e. 
		// <xop:Include xmlns:xop="http://www.w3.org/2004/08/xop/include" href="cid:c060bfb1-82cb-4820-9d87-4f2422b50915"/>
        for (Iterator<AttachmentPart> attachmentsIterator = (Iterator<AttachmentPart>)response.getAttachments(); attachmentsIterator.hasNext();) {
            AttachmentPart ap = attachmentsIterator.next();

            SOAPElement elt = SOAPFactory.newInstance().createElement(new QName("http://www.w3.org/2004/08/xop/include", "Include", "xop"));
    		// content id will be wrapped in angled brackets - need to remove them
    		String contentId = ap.getContentId();
    		contentId = contentId.replaceFirst("<", "");
    		contentId = contentId.replaceFirst(">", "");
    		elt.addAttribute(new QName("href"), "cid:" + contentId);
    		
    		ap = response.getAttachment(elt);
    		if (ap == null) {
            	System.out.println("FindAll failed:  no attachment for [cid:" + contentId + "]");
            	System.exit(-1);
    		}
    		byte[] rawBytes = ap.getRawContentBytes() ;
            if (rawBytes.length != 15) {
            	System.out.println("FindAll failed:  wrong number of bytes returned - expected [15] but was [" + rawBytes.length + "]");
            	System.exit(-1);
            }
            // no order is guaranteed, so need to check which attachment we are dealing with
            byte b = rawBytes[0];
            if (b == 1 || b == 2 || b == 3) {
            	compareBytes(b, rawBytes, "FindAll");
            } else {
        		System.out.println("FindAll failed:  wrong byte value returned - expected [1], [2] or [3] but was [" + b + "]");
        		System.exit(-1);
            }
            elt.removeContents();
        }
        // TEST FINDBYPK
        // we expect 1 attachment
        request = createSOAPMessage(SOAP_FINDBYPK_REQUEST);
    	response = sourceDispatch.invoke(request);
		if (response.countAttachments() != 1) {
        	System.out.println("findByPk failed:  wrong number of attachments - expected [1] but was [" + response.countAttachments() + "]");
        	System.exit(-1);
		}
		AttachmentPart ap = (AttachmentPart)((Iterator)response.getAttachments()).next();
        SOAPElement elt = SOAPFactory.newInstance().createElement(new QName("http://www.w3.org/2004/08/xop/include", "Include", "xop"));
		// content id will be wrapped in angled brackets - need to remove them
		String contentId = ap.getContentId();
		contentId = contentId.replaceFirst("<", "");
		contentId = contentId.replaceFirst(">", "");
		elt.addAttribute(new QName("href"), "cid:" + contentId);
		
		ap = response.getAttachment(elt);
		if (ap == null) {
        	System.out.println("FindByPk failed:  no attachment for [cid:" + contentId + "]");
        	System.exit(-1);
		}
		byte[] rawBytes = ap.getRawContentBytes() ;
        if (rawBytes.length != 15) {
        	System.out.println("FindByPk failed:  wrong number of bytes returned - expected [15] but was [" + rawBytes.length + "]");
        	System.exit(-1);
        }
    	compareBytes(3, rawBytes, "FindByPk");
    }
    
    static void compareBytes(int intVal, byte[] bytes, String testName) {
        for (int i = 0; i < bytes.length; i++) {
        	if (bytes[i] != intVal) {
        		System.out.println(testName + " failed:  wrong byte value returned - expected [" + intVal + "] but was [" + bytes[i] + "]");
        		System.exit(-1);
        	}
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