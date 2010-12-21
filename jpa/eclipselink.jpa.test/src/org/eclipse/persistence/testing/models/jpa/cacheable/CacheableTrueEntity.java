/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/16/2009-2.0 Guy Pelletier 
 *       - 277039: JPA 2.0 Cache Usage Settings
 *     07/16/2009-2.0 Guy Pelletier 
 *       - 277039: JPA 2.0 Cache Usage Settings
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.cacheable;

import static javax.persistence.GenerationType.TABLE;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.config.QueryHints;

@Entity(name="JPA_CACHEABLE_TRUE")
@Inheritance(strategy=SINGLE_TABLE)
@DiscriminatorColumn(name="DTYPE")
@DiscriminatorValue("CTE")
@Cacheable // defaults to true
@NamedQueries({
    @NamedQuery(
        name="findCacheableTrueEntityByPK_RETRIEVE_BYPASS_STORE_USE",
        query="SELECT OBJECT(e) FROM JPA_CACHEABLE_TRUE e WHERE e.id = :id",
        hints={
            @QueryHint(name=QueryHints.CACHE_RETRIEVE_MODE, value="BYPASS"),
            @QueryHint(name=QueryHints.CACHE_STORE_MODE, value="USE")
        }
    ),
    @NamedQuery(
            name="findCacheableTrueEntityByPK_RETRIEVE_USE_STORE_BYPASS",
            query="SELECT OBJECT(e) FROM JPA_CACHEABLE_TRUE e WHERE e.id = :id",
            hints={
                @QueryHint(name=QueryHints.CACHE_RETRIEVE_MODE, value="USE"),
                @QueryHint(name=QueryHints.CACHE_STORE_MODE, value="BYPASS")
            }
    ),
    @NamedQuery(
        name="findCacheableTrueEntityByPK_USE_USE",
        query="SELECT OBJECT(e) FROM JPA_CACHEABLE_TRUE e WHERE e.id = :id",
        hints={
            @QueryHint(name=QueryHints.CACHE_RETRIEVE_MODE, value="USE"),
            @QueryHint(name=QueryHints.CACHE_STORE_MODE, value="USE")
        }
    ),
    @NamedQuery(
        name="findCacheableTrueEntityByPK_BYPASS_BYPASS",
        query="SELECT OBJECT(e) FROM JPA_CACHEABLE_TRUE e WHERE e.id = :id",
        hints={
            @QueryHint(name=QueryHints.CACHE_RETRIEVE_MODE, value="BYPASS"),
            @QueryHint(name=QueryHints.CACHE_STORE_MODE, value="BYPASS")
        }
    )
})
public class CacheableTrueEntity {
    private int id;
    private String name;
    
    public CacheableTrueEntity() {}
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="CACHEABLE_TABLE_GENERATOR")
    @TableGenerator(
        name="CACHEABLE_TABLE_GENERATOR", 
        table="JPA_CACHEABLE_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="JPA_CACHEABLE_SEQ")
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String toString() {
        return "CacheableTrueEntity: [" + name + "]";
    }
}
