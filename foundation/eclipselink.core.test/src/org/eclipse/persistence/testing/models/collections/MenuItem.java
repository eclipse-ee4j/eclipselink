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
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.testing.models.collections.Menu;

public class MenuItem implements ChangeTracker {
    private String name;
    private float price = 0.0f;
    private java.math.BigDecimal id;
    private ValueHolderInterface menu = new ValueHolder();
    public PropertyChangeListener listener;

    public MenuItem() {
        super();
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

    public static MenuItem example1(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Remoulade de moules au safran (Mussels w/saffron sauce)");
        instance.setPrice(7.95f);

        return instance;
    }

    public static MenuItem example10(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Tijuana Burrito");
        instance.setPrice(10.5f);

        return instance;
    }

    public static MenuItem example11(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Tijuana Chimichanga");
        instance.setPrice(11.5f);

        return instance;
    }

    public static MenuItem example12(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Tostada Salad");
        instance.setPrice(3.5f);

        return instance;
    }

    public static MenuItem example13(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Quesadillas");
        instance.setPrice(7.5f);

        return instance;
    }

    public static MenuItem example14(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Taco");
        instance.setPrice(6.75f);

        return instance;
    }

    public static MenuItem example15(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Vegetarian Burrito");
        instance.setPrice(6.45f);

        return instance;
    }

    public static MenuItem example16(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Rice congee");
        instance.setPrice(1.35f);

        return instance;
    }

    public static MenuItem example17(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Peanut Butter sandwich");
        instance.setPrice(4.40f);

        return instance;
    }

    public static MenuItem example2(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Noisettes d'agneau grillees au curry et jus d'ail. (Roasted lamb loin w/a light curry & garlic jus");
        instance.setPrice(17.5f);

        return instance;
    }

    public static MenuItem example3(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Poitrine de poulet de grains rotie, creme et confit d'ail. (Roasted breast of chicken w/cream & garlic confit)");
        instance.setPrice(14.5f);

        return instance;
    }

    public static MenuItem example4(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Terrine Campargnarde 'Facon du Chef'. (Warm country-style terrine)");
        instance.setPrice(7.5f);

        return instance;
    }

    public static MenuItem example5(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Escargots au beurre d'ail. (12 escargots in a garlic butter sauce)");
        instance.setPrice(8.95f);

        return instance;
    }

    public static MenuItem example6(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Croustade de fromage de chevre, tomates et basilic. (Bruschetta w/goat cheese)");
        instance.setPrice(4.5f);

        return instance;
    }

    public static MenuItem example7(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Creme Brulee a l'Essence d'Orange. (A bistro classic flavored w/orange)");
        instance.setPrice(4.5f);

        return instance;
    }

    public static MenuItem example8(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Profiterole au Chocolat w/French vanilla ice cream & hot chocolate sauce");
        instance.setPrice(4.5f);

        return instance;
    }

    public static MenuItem example9(Menu aMenu) {
        MenuItem instance = new MenuItem();
        instance.setMenu(aMenu);
        instance.setName("Clafoutis Maison (Baked French custard w/market fresh fruits)");
        instance.setPrice(4.5f);

        return instance;
    }

    public java.math.BigDecimal getId() {
        return id;
    }

    public Menu getMenu() {
        return (Menu)getMenuHolder().getValue();
    }

    public ValueHolderInterface getMenuHolder() {
        return menu;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public void setId(java.math.BigDecimal newValue) {
        propertyChange("id", this.id, newValue);
        this.id = newValue;
    }

    public void setMenu(ValueHolderInterface newValue) {
        this.menu = newValue;
    }

    public void setMenu(Menu newValue) {
        propertyChange("menu", this.getMenuHolder().getValue(), newValue);
        getMenuHolder().setValue(newValue);
    }

    public void setMenuHolder(ValueHolderInterface newValue) {
        this.menu = newValue;
    }

    public void setName(String newValue) {
        propertyChange("name", this.name, newValue);
        this.name = newValue;
    }

    public void setPrice(float newValue) {
        propertyChange("price", new Float(this.price), new Float(newValue));
        this.price = newValue;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("COL_M_IT");

        definition.addIdentityField("ID", java.math.BigDecimal.class);
        definition.addField("NAME", String.class, 200);
        definition.addField("PRICE", Float.class);
        definition.addField("MENU_ID", java.math.BigDecimal.class, 15);

        return definition;
    }

    /**
     * Returns a String that represents the value of this object.
     * @return a string representation of the receiver
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()));
        buf.append("(");
        if (getName() != null) {
            buf.append(getName().substring(0, Math.min(10, getName().length())));
        }
        buf.append(")");
        return buf.toString();
    }
}
