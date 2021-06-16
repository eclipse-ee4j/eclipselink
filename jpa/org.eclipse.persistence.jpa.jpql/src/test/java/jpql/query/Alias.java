/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package jpql.query;

import java.util.Date;
import java.util.Map;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@NamedQueries
({
   @NamedQuery(name="alias.param1", query="SELECT a.alias FROM Alias AS a WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1"),
   @NamedQuery(name="alias.key1",   query="SELECT KEY(k) FROM Alias a JOIN a.ids k"),
   @NamedQuery(name="alias.key2",   query="SELECT KEY(e).firstName FROM Alias a JOIN a.addresses e"),
   @NamedQuery(name="alias.value1", query="SELECT VALUE(v) FROM Alias a JOIN a.ids v"),
   @NamedQuery(name="alias.value2", query="SELECT v FROM Alias a JOIN a.ids v"),
   @NamedQuery(name="alias.value3", query="SELECT VALUE(e).zip.code FROM Alias a JOIN a.addresses e"),
   @NamedQuery(name="alias.entry",  query="SELECT ENTRY(e) FROM Alias a JOIN a.ids e")
})
@SuppressWarnings("unused")
public class Alias {

    @Id
    private int id;
    private String alias;
    @ElementCollection
    @Temporal(TemporalType.DATE)
    private Map<String, Date> ids;
    private Customer customer;
    @JoinColumn(name="ID", referencedColumnName="ALIAS.ALIAS")
    private Map<Customer, Address> addresses;
}
