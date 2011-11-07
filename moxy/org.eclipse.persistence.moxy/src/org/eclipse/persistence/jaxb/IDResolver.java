package org.eclipse.persistence.jaxb;

import javax.xml.bind.ValidationEventHandler;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * <p>
 * IDResolver can be subclassed to allow customization of the ID/IDREF processing of
 * JAXBUnmarshaller.  A custom IDResolver can be specified on the Unmarshaller as follows:
 * </p>
 *
 * <p>
 * <code>
 * IDResolver customResolver = new MyIDResolver();
 * jaxbUnmarshaller.setProperty(JAXBContext.ID_RESOLVER, customResolver);
 * </code>
 * </p>
 *
 * @see JAXBUnmarshaller
 */
public abstract class IDResolver extends org.eclipse.persistence.oxm.IDResolver {

    /**
     * <p>
     * Called when unmarshalling begins.
     * </p>
     *
     * @param eventHandler Any errors encountered during the unmarshal process should be reported to this handler.
     *
     * @throws SAXException
     */
    public void startDocument(ValidationEventHandler eventHandler) throws SAXException {}

    /**
     * INTERNAL
     */
    public final void startDocument(ErrorHandler errorHandler) throws SAXException {
        JAXBErrorHandler jeh = (JAXBErrorHandler) errorHandler;
        startDocument(jeh.getValidationEventHandler());
    }

}