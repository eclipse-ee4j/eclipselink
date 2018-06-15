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
//     ailitchev - UnidirectionalOneToMany mapping
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

@Entity
@Table(name="DDL_COMMENT")
@TableGenerator(
    name = "COMMENT_TABLE_GENERATOR",
    table = "DDL_CKENT_SEQ",
    pkColumnName = "SEQ_NAME",
    valueColumnName = "SEQ_COUNT",
    pkColumnValue = "COMMENT_SEQ"
)
@Index(name="DDL_COMMENT_TEXT_IX", unique=true, columnNames={"TEXT"})
@Indexes({
    @Index(name="DDL_COMMENT_FLAG_IX", columnNames={"FLAG"}),
    @Index(columnNames={"FLAG", "TEXT"})
})
public class Comment<X> {
    @Id
    @GeneratedValue(strategy = TABLE, generator = "COMMENT_TABLE_GENERATOR")
    private int id;

    private String text;

    private X flag;

    @Lob
    @Column(name="PHOTO", length=80000)
    private Byte[] photo;

    @Lob
    @Column(name="AUDIO")
    private Byte[] audio;

    public Comment() {
        super();
    }

    public Comment(String text) {
        this();
        this.text = text;
    }

    public Byte[] getAudio() {
        return audio;
    }

    public int getId() {
        return id;
    }

    public Byte[] getPhoto() {
        return photo;
    }

    public String getTest() {
        return text;
    }

    public void setAudio(Byte[] audio) {
        this.audio = audio;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPhoto(Byte[] photo) {
        this.photo = photo;
    }

    public void setTest(String text) {
        this.text = text;
    }

    public X getFlag() {
        return flag;
    }

    public void setFlag(X flag) {
        this.flag = flag;
    }
}
