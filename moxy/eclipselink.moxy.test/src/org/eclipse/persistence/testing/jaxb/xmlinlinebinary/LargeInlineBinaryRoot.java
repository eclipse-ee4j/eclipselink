/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlinlinebinary;

import java.awt.Image;
import java.util.List;

import javax.activation.DataHandler;
import javax.mail.internet.MimeMultipart;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Source;

@XmlRootElement(name="root")
public class LargeInlineBinaryRoot {

    private DataHandler dataHandler;
    private Image image;
    private Source source;
    private MimeMultipart mimeMultipart;
    private List<DataHandler> dataHandlerList;
    private List<Image> imageList;
    private List<Source> sourceList;
    private List<MimeMultipart> mimeMultipartList;

    @XmlInlineBinaryData
    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public void setDataHandler(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @XmlInlineBinaryData
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @XmlInlineBinaryData
    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    @XmlInlineBinaryData
    public MimeMultipart getMimeMultipart() {
        return mimeMultipart;
    }

    public void setMimeMultipart(MimeMultipart mimeMultipart) {
        this.mimeMultipart = mimeMultipart;
    }

    @XmlInlineBinaryData
    public List<DataHandler> getDataHandlerList() {
        return dataHandlerList;
    }

    public void setDataHandlerList(List<DataHandler> dataHandlerList) {
        this.dataHandlerList = dataHandlerList;
    }

    @XmlInlineBinaryData
    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }

    @XmlInlineBinaryData
    public List<Source> getSourceList() {
        return sourceList;
    }

    public void setSourceList(List<Source> sourceList) {
        this.sourceList = sourceList;
    }

    @XmlInlineBinaryData
    public List<MimeMultipart> getMimeMultipartList() {
        return mimeMultipartList;
    }

    public void setMimeMultipartList(List<MimeMultipart> mimeMultipartList) {
        this.mimeMultipartList = mimeMultipartList;
    }

}