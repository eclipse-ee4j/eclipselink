/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.collections;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.descriptors.changetracking.MapChangeEvent;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.testing.models.collections.Diner;
import org.eclipse.persistence.testing.models.collections.Location;
import org.eclipse.persistence.testing.models.collections.Menu;

/**
 * Models a restaurant for testing TopLink support for JDK1.2 Collections and Maps.
 */
public class Restaurant implements ChangeTracker {
    private String name;
    private ValueHolderInterface menus;
    private Collection waiters;
    private java.math.BigDecimal id;
    private Collection locations;
    private TreeSet locations2;
    private Map preferredCustomers;
    private ValueHolderInterface slogans;
    private ValueHolderInterface services;
    private Map licenses;
    public PropertyChangeListener listener;

    /**
     * Restaurant constructor comment.
     */
    public Restaurant() {
        super();
        locations = new ArrayList();
        setLocations2(new TreeSet(getLocationComparator()));

        preferredCustomers = new Hashtable();

        menus = new ValueHolder(new Hashtable());
        slogans = new ValueHolder(new ArrayList());
        services = new ValueHolder(new ArrayList());
        licenses = new HashMap();
    }

    public PropertyChangeListener _persistence_getPropertyChangeListener() {
        return listener;
    }

    public void _persistence_setPropertyChangeListener(PropertyChangeListener listener) {
        this.listener = listener;
    }

    public void propertyChange(String propertyName, Object oldValue, Object newValue) {
        if (listener != null) {
            if (oldValue != newValue) {
                listener.propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
            }
        }
    }

    public void collectionChange(String propertyName, Collection changedCollection, Object newObject, int changeType, boolean isChangeApplied) {
        if (listener != null) {
            listener.propertyChange(new CollectionChangeEvent(this, propertyName, changedCollection, newObject, changeType, isChangeApplied));
        }
    }

    public void mapChange(String propertyName, Map changedCollection, Object key, Object newObject, int changeType, boolean isChangeApplied) {
        if (listener != null) {
            listener.propertyChange(new MapChangeEvent(this, propertyName, changedCollection, key, newObject, changeType, isChangeApplied));
        }
    }

    public static Comparator getLocationComparator() {
        return new LocationComparator();
    }

    public void addDiner(Diner aDiner) {
        getDiners().put(aDiner.getLastName(), aDiner);
        aDiner.addFavouriteRestaurant(this);
        mapChange("preferredCustomers", getDiners(), aDiner.getLastName(), aDiner, MapChangeEvent.ADD, true);
    }

    public void addLocation(Location aLocation) {
        getLocations().add(aLocation);
        collectionChange("locations", getLocations(), aLocation, CollectionChangeEvent.ADD, true);
    }

    public void addMenu(Menu aMenu) {
        aMenu.setOwner(this);

        getMenus().put(aMenu.getKey(), aMenu);
        mapChange("menus", getDiners(), aMenu.getKey(), aMenu, MapChangeEvent.ADD, true);
    }

    public void removeMenu(Menu aMenu) {
        aMenu.setOwner((Restaurant)null);

        getMenus().remove(aMenu.getKey());
        mapChange("menus", getMenus(), aMenu.getKey(), aMenu, MapChangeEvent.REMOVE, true);
    }

    public String allToString() {
        StringBuffer buf = new StringBuffer();
        buf.append("Restaurant: " + this.getName());
        buf.append(org.eclipse.persistence.internal.helper.Helper.cr());
        if (getMenus() != null) {
            buf.append("Menus: " + org.eclipse.persistence.internal.helper.Helper.getShortClassName(getMenus().getClass()));
            buf.append(getMenus().toString());
            buf.append(org.eclipse.persistence.internal.helper.Helper.cr());
        }
        if (getWaiters() != null) {
            buf.append("Waiters: " + org.eclipse.persistence.internal.helper.Helper.getShortClassName(getWaiters().getClass()));
            buf.append(getWaiters().toString());
            buf.append(org.eclipse.persistence.internal.helper.Helper.cr());
        }
        if (getPreferredCustomers() != null) {
            buf.append("PreferredCustomers: " + org.eclipse.persistence.internal.helper.Helper.getShortClassName(getPreferredCustomers().getClass()));
            buf.append(getPreferredCustomers().toString());
            buf.append(org.eclipse.persistence.internal.helper.Helper.cr());
        }
        if (getLocations() != null) {
            buf.append("Locations: " + org.eclipse.persistence.internal.helper.Helper.getShortClassName(getLocations().getClass()));
            buf.append(getLocations().toString());
            buf.append(org.eclipse.persistence.internal.helper.Helper.cr());
        }
        if (getSlogans() != null) {
            buf.append("Slogan: " + org.eclipse.persistence.internal.helper.Helper.getShortClassName(getSlogans().getClass()));
            buf.append(getSlogans().toString());
            buf.append(org.eclipse.persistence.internal.helper.Helper.cr());
        }
        return buf.toString();
    }

    public static Restaurant example1() {
        Restaurant rest = new Restaurant();
        rest.setName("Chez Abuse");

        ArrayList waiters = new ArrayList();
        waiters.add(org.eclipse.persistence.testing.models.collections.Waiter.example1(rest));
        waiters.add(org.eclipse.persistence.testing.models.collections.Waiter.example2(rest));
        waiters.add(org.eclipse.persistence.testing.models.collections.Waiter.example3(rest));
        rest.setWaiters(waiters);

        Hashtable menus = new Hashtable();
        Menu menu = Menu.example1(rest);
        menus.put(menu.getType(), menu);
        menu = Menu.example2(rest);
        menus.put(menu.getType(), menu);
        menu = Menu.example3(rest);
        menus.put(menu.getType(), menu);
        rest.setMenus(menus);

        ArrayList slogans = new ArrayList();
        slogans.add("Fine food!");
        slogans.add("Even the escargots have attitude.");
        rest.setSlogans(slogans);

        rest.getServices().add("Reservations Required");
        rest.getServices().add("Formal Attire");

        rest.getLicenses().put("Alcohol License", Boolean.TRUE);
        rest.getLicenses().put("Smoking License", Boolean.TRUE);
        rest.getLicenses().put("Food License", Boolean.TRUE);

        return rest;
    }

    public static Restaurant example2() {
        Restaurant rest = new Restaurant();
        rest.setName("Pedro's");

        ArrayList waiters = new ArrayList();
        waiters.add(org.eclipse.persistence.testing.models.collections.Waiter.example4(rest));
        waiters.add(org.eclipse.persistence.testing.models.collections.Waiter.example5(rest));
        waiters.add(org.eclipse.persistence.testing.models.collections.Waiter.example6(rest));
        rest.setWaiters(waiters);

        Hashtable menus = new Hashtable();
        Menu menu = Menu.example4(rest);
        menus.put(menu.getType(), menu);
        menu = Menu.example5(rest);
        menus.put(menu.getType(), menu);
        rest.setMenus(menus);

        ArrayList slogans = new ArrayList();
        slogans.add("Tacos are us.");
        slogans.add("Hot food!");
        rest.setSlogans(slogans);

        rest.getServices().add("Air Conditioning");

        rest.getLicenses().put("Alcohol License", Boolean.FALSE);
        rest.getLicenses().put("Smoking License", Boolean.FALSE);
        rest.getLicenses().put("Food License", Boolean.TRUE);

        return rest;
    }

    public static Restaurant example3() {
        Restaurant rest = new Restaurant();
        rest.setName("Yellow River");

        ArrayList waiters = new ArrayList();
        waiters.add(org.eclipse.persistence.testing.models.collections.Waiter.example7(rest));
        rest.setWaiters(waiters);

        Hashtable menus = new Hashtable();
        Menu menu = Menu.example6(rest);
        menus.put(menu.getType(), menu);
        rest.setMenus(menus);

        ArrayList slogans = new ArrayList();
        slogans.add("Good food!");
        slogans.add("Yum, congee!.");
        rest.setSlogans(slogans);

        rest.getServices().add("Air Conditioning");
        rest.getServices().add("Reservations Required");

        rest.getLicenses().put("Alcohol License", Boolean.TRUE);
        rest.getLicenses().put("Smoking License", Boolean.FALSE);
        rest.getLicenses().put("Food License", Boolean.TRUE);

        return rest;
    }

    public Map getDiners() {
        return preferredCustomers;
    }

    public java.math.BigDecimal getId() {
        return id;
    }

    public Collection getLocations() {
        return locations;
    }

    public Collection getServices() {
        return (Collection)services.getValue();
    }

    public Map getLicenses() {
        return licenses;
    }

    public TreeSet getLocations2() {
        return locations2;
    }

    public Map getMenus() {
        return (Map)getMenusHolder().getValue();
    }

    public ValueHolderInterface getMenusHolder() {
        return menus;
    }

    public String getName() {
        return name;
    }

    public Map getPreferredCustomers() {
        return preferredCustomers;
    }

    public Collection getSlogans() {
        return (Collection)getSlogansHolder().getValue();
    }

    public org.eclipse.persistence.indirection.ValueHolderInterface getSlogansHolder() {
        return slogans;
    }

    public Collection getWaiters() {
        return waiters;
    }

    public void setId(java.math.BigDecimal newValue) {
        propertyChange("id", this.id, id);
        this.id = newValue;
    }

    public void setLocations(Collection newValue) {
        propertyChange("locations", this.locations, newValue);
        this.locations = newValue;
    }

    public void setLocations2(TreeSet newValue) {
        propertyChange("locations2", this.locations2, newValue);
        this.locations2 = newValue;
    }

    public void setMenus(Map newValue) {
        propertyChange("menus", this.getMenusHolder().getValue(), newValue);
        this.getMenusHolder().setValue(newValue);
    }

    public void setMenusHolder(ValueHolderInterface aHolder) {
        menus = aHolder;
    }

    public void setName(String newValue) {
        propertyChange("name", this.name, newValue);
        this.name = newValue;
    }

    public void setPreferredCustomers(Map newValue) {
        propertyChange("preferredCustomers", this.preferredCustomers, newValue);
        this.preferredCustomers = newValue;
    }

    public void setSlogans(Collection aValue) {
        propertyChange("slogans", getSlogansHolder().getValue(), aValue);
        getSlogansHolder().setValue(aValue);
    }

    public void setSlogansHolder(org.eclipse.persistence.indirection.ValueHolderInterface newValue) {
        this.slogans = newValue;
    }

    public void setWaiters(Collection newValue) {
        propertyChange("waiters", this.waiters, newValue);
        this.waiters = newValue;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition sloganTableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("COL_SLOG");
        definition.addField("REST_ID", java.math.BigDecimal.class, 15);
        definition.addField("SLOGAN", String.class, 200);

        return definition;
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition servicesTableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("COL_SERVICES");
        definition.addField("REST_ID", java.math.BigDecimal.class, 15);
        definition.addField("SERVICE", String.class, 5);

        return definition;
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition licensesTableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("COL_LICENSE");
        definition.addField("REST_ID", java.math.BigDecimal.class, 15);
        definition.addField("LICENSE", String.class, 2);
        definition.addField("STATUS", Integer.class);

        return definition;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("COL_REST");
        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 40);

        return definition;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("Restaurant: " + this.getName());

        return buf.toString();
    }

    static class LocationComparator implements Comparator {
        public int compare(Object object1, Object object2) {
            if ((object1.getClass() != Location.class) || (object2.getClass() != Location.class)) {
                throw new ClassCastException("Invalid comparison : " + object1 + ", " + object2);
            }

            Location loc1 = (Location)object1;
            Location loc2 = (Location)object2;
            return String.CASE_INSENSITIVE_ORDER.compare(loc1.getArea(), loc2.getArea());
        }
    }
}
