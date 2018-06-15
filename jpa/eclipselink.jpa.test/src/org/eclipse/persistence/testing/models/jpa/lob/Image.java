/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.lob;

import java.io.Serializable;
import javax.persistence.*;

import org.eclipse.persistence.annotations.SerializedConverter;
import org.eclipse.persistence.annotations.SerializedConverters;
import org.eclipse.persistence.sessions.serializers.JSONSerializer;
import org.eclipse.persistence.sessions.serializers.XMLSerializer;

/**
 * Model class used to test Lobs and Lazy Basics.
 */

@Entity
@Table(name="CMP3_IMAGE")
@SecondaryTable(name="CMP3_CLIP")
@PrimaryKeyJoinColumn(name="ID", referencedColumnName="ID")
@SerializedConverter(name="json2", serializerClass=JSONSerializer.class, serializerPackage="org.eclipse.persistence.testing.models.jpa.lob")
public class Image implements Serializable {
    private int id;
    private byte[] audio;
    private char[] commentary;
    private Byte[] picture;
    private String script;
    private SerializableNonEntity customAttribute1;
    private SerializableNonEntity customAttribute2;
    private SerializableNonEntity xml1;
    private SerializableNonEntity json1;
    private SerializableNonEntity xml2;
    private SerializableNonEntity json2;

    @org.eclipse.persistence.annotations.Convert("xml2")
    @SerializedConverters({
        @SerializedConverter(name="xml2", serializerClass=XMLSerializer.class, serializerPackage="org.eclipse.persistence.testing.models.jpa.lob")
    })
    public SerializableNonEntity getXml2() {
        return xml2;
    }

    public void setXml2(SerializableNonEntity xml2) {
        this.xml2 = xml2;
    }

    @org.eclipse.persistence.annotations.Convert("json2")
    public SerializableNonEntity getJson2() {
        return json2;
    }

    public void setJson2(SerializableNonEntity json2) {
        this.json2 = json2;
    }

    @org.eclipse.persistence.annotations.Convert(org.eclipse.persistence.annotations.Convert.XML)
    public SerializableNonEntity getXml1() {
        return xml1;
    }

    public void setXml1(SerializableNonEntity xml) {
        this.xml1 = xml;
    }

    @org.eclipse.persistence.annotations.Convert(org.eclipse.persistence.annotations.Convert.JSON)
    public SerializableNonEntity getJson1() {
        return json1;
    }

    public void setJson1(SerializableNonEntity json) {
        this.json1 = json;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Lob
    @Column(table="CMP3_CLIP", length=4800)
    @Basic(fetch=FetchType.LAZY)
    public byte[] getAudio(){
        return audio;
    }

    @Lob
    @Column(table="CMP3_CLIP", length=4500)
    public char[] getCommentary() {
        return commentary;
    }

    @Lob
    @Basic(fetch=FetchType.LAZY)
    public SerializableNonEntity getCustomAttribute1() {
        return customAttribute1;
    }


    public SerializableNonEntity getCustomAttribute2() {
        return customAttribute2;
    }

    @Id
    public int getId(){
        return id;
    }

    @Lob
    @Column(length=4800)
    public Byte[] getPicture(){
        return picture;
    }

    @Lob
    @Column(length=4500)
    public String getScript()    {
        return script;
    }

    public void setAudio(byte[] audio)    {
        this.audio = audio;
    }

    public void setCommentary(char[] commentary)    {
        this.commentary = commentary;
    }

    public void setCustomAttribute1(SerializableNonEntity customAttribute) {
        this.customAttribute1= customAttribute;
    }

    public void setCustomAttribute2(SerializableNonEntity customAttribute) {
        this.customAttribute2=customAttribute;
    }

    public void setId(int id)    {
        this.id = id;
    }

    public void setPicture(Byte[] picture)    {
        this.picture = picture;
    }

    public void setScript(String script)    {
        this.script = script;
    }
}
