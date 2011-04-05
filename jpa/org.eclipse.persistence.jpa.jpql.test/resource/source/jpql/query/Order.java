package jpql.query;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries
({
   @NamedQuery(name="order.doubleValue", query="select object(o) FROM Order o Where SQRT(o.totalPrice) > :doubleValue"),
   @NamedQuery(name="order.sum1",        query="SELECT SUM(o.totalPrice) FROM Order o"),
   @NamedQuery(name="order.sum2",        query="SELECT SUM(o.price) FROM Order o"),
   @NamedQuery(name="order.sum3",        query="SELECT SUM(o.realPrice) FROM Order o"),
   @NamedQuery(name="order.sqrt",        query="SELECT SQRT(o.totalPrice) FROM Order o"),
   @NamedQuery(name="order.coalesce1",   query="SELECT COALESCE(o.price, o.price) FROM Order o"),
   @NamedQuery(name="order.coalesce2",   query="SELECT COALESCE(o.totalPrice, SQRT(o.realPrice)) FROM Order o"),
   @NamedQuery(name="order.coalesce3",   query="SELECT COALESCE(o.number, e.name) FROM Order o, Employee e"),
   @NamedQuery(name="order.coalesce4",   query="SELECT COALESCE(o.price, o.number) FROM Order o"),
   @NamedQuery(name="order.abs",         query="SELECT ABS(o.totalPrice) FROM Order o")
})
@SuppressWarnings("unused")
public class Order {

	private BigInteger price;
	private BigDecimal realPrice;
	private double totalPrice;
	@Id private int id;
	private String number;
}