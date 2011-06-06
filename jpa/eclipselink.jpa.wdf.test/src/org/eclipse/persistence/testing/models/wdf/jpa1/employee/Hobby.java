/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "TMP_HOBBY")
@TableGenerator(name = "StringIdGenerator", table = "TMP_STRING_GEN", pkColumnName = "BEAN_NAME", valueColumnName = "MAX_ID")
public class Hobby {
    private String id;
    private String description;
    private String category;

    public Hobby() {
    }

    public Hobby(String aDescription) {
        description = aDescription;
    }

    public Hobby(String txt, String aDescription) {
        id = txt;
        description = aDescription;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "StringIdGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    public String getCategory() {
        return category;
    }

    public void setCategory(final String aCategory) {
        category = aCategory;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Hobby)) {
            return false;
        }
        final Hobby other = (Hobby) obj;

        if (id == null && other.id != null) {
            return false;
        }
        if (id != null && !id.equals(other.id)) {
            return false;
        }
        if (description == null && other.description != null) {
            return false;
        }
        if (description != null && !description.equals(other.description)) {
            return false;
        }
        if (category != null && !category.equals(other.category)) {
            return false;
        }
        if (category == null && other.category != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 17 + id.hashCode();
        result *= 37;
        if (description != null) {
            result += description.hashCode();
        }
        result += 17;
        result *= 37;
        if (category != null) {
            result += category.hashCode();
        }

        return result;
    }

    @Override
    public String toString() {
        return id + ":" + category + ":" + description;
    }
}
