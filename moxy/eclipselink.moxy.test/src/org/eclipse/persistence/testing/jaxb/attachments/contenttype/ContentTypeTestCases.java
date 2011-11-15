/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.2 - initial implementation
 ******************************************************************************/
 package org.eclipse.persistence.testing.jaxb.attachments.contenttype;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class ContentTypeTestCases extends TestCase {

    private static Image IMAGE = new BufferedImage(1,1,BufferedImage.TYPE_INT_BGR);
    private static String IMAGE_GIF = "image/gif";
    private static String IMAGE_JPEG = "image/jpeg";
    private static String IMAGE_PNG = "image/png";
    private static String APPLICATION_XML = "application/xml";
    private static String TEXT_XML = "text/xml";

    private Marshaller marshaller;
    private TestAttachmentMarshaller attachmentMarshaller;

    @Override
    protected void setUp() throws Exception {
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {Root.class, RootImageStar.class, RootImageJpeg.class, RootSource.class}, null);
        marshaller = jc.createMarshaller();
        attachmentMarshaller = new TestAttachmentMarshaller();
        marshaller.setAttachmentMarshaller(attachmentMarshaller);
    }

    public void testNoMimeTypeSpecied_ContentTypeNull() throws JAXBException {
        Root root = new Root();
        root.setMimeType(null);
        root.setImage(IMAGE);
        marshaller.marshal(root, new StringWriter());
        assertEquals(IMAGE_PNG, attachmentMarshaller.getMimeType());
    }

    public void testNoMimeTypeSpecied_ContentTypeImageGif() throws JAXBException {
        Root root = new Root();
        root.setMimeType(IMAGE_GIF);
        root.setImage(IMAGE);
        marshaller.marshal(root, new StringWriter());
        assertEquals(IMAGE_GIF, attachmentMarshaller.getMimeType());
    }

    public void testNoMimeTypeSpecified_ConentTypeImageJpeg() throws JAXBException {
        Root root = new Root();
        root.setMimeType(IMAGE_JPEG);
        root.setImage(IMAGE);
        marshaller.marshal(root, new StringWriter());
        assertEquals(IMAGE_JPEG, attachmentMarshaller.getMimeType());
    }

    public void testImageStarSpecified_ConentTypeImageGif() throws JAXBException {
        RootImageStar root = new RootImageStar();
        root.setMimeType(IMAGE_GIF);
        root.setImage(IMAGE);
        marshaller.marshal(root, new StringWriter());
        assertEquals(IMAGE_GIF, attachmentMarshaller.getMimeType());
    }

    public void testImageStarSpecified_ConentTypeImageJpeg() throws JAXBException {
        RootImageStar root = new RootImageStar();
        root.setMimeType(IMAGE_JPEG);
        root.setImage(IMAGE);
        marshaller.marshal(root, new StringWriter());
        assertEquals(IMAGE_JPEG, attachmentMarshaller.getMimeType());
    }

    public void testImageJpegSpecified_ConentTypeImageGif() throws JAXBException {
        RootImageJpeg root = new RootImageJpeg();
        root.setMimeType(IMAGE_GIF);
        root.setImage(IMAGE);
        marshaller.marshal(root, new StringWriter());
        assertEquals(IMAGE_GIF, attachmentMarshaller.getMimeType());
    }

    public void testImageJpegSpecified_ConentTypeImageJpeg() throws JAXBException {
        RootImageJpeg root = new RootImageJpeg();
        root.setMimeType(IMAGE_JPEG);
        root.setImage(IMAGE);
        marshaller.marshal(root, new StringWriter());
        assertEquals(IMAGE_JPEG, attachmentMarshaller.getMimeType());
    }

    public void testImageJpegSpecified_ConentTypeNull() throws JAXBException {
        RootImageJpeg root = new RootImageJpeg();
        root.setMimeType(null);
        root.setImage(IMAGE);
        marshaller.marshal(root, new StringWriter());
        assertEquals(IMAGE_JPEG, attachmentMarshaller.getMimeType());
    }

    public void testSourceNoMimeTypeSpecified_ContentTypeNull() throws JAXBException {
        RootSource root = new RootSource();
        root.setMimeType(null);
        root.setSource(new DOMSource());
        marshaller.marshal(root, new StringWriter());
        assertEquals(APPLICATION_XML, attachmentMarshaller.getMimeType());
    }

    public void testSourceNoMimeTypeSpecified_ContentTypeTextXml() throws JAXBException {
        RootSource root = new RootSource();
        root.setMimeType(TEXT_XML);
        root.setSource(new DOMSource());
        marshaller.marshal(root, new StringWriter());
        assertEquals(TEXT_XML, attachmentMarshaller.getMimeType());
    }

}