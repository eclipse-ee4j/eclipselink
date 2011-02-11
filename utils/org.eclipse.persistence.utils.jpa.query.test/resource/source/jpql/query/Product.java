package jpql.query;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries
({
   @NamedQuery(name="product.date",     query="SELECT DISTINCT p From Product p where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate"),
   @NamedQuery(name="product.int1",     query="Select Distinct Object(p) from Product p where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL))"),
   @NamedQuery(name="product.quantity", query="SELECT AVG(p.quantity) FROM Product p"),
   @NamedQuery(name="product.max",      query="SELECT MAX(p.quantity) FROM Product p"),
   @NamedQuery(name="product.min",      query="SELECT MIN(p.quantity) FROM Product p"),
   @NamedQuery(name="product.abs",      query="SELECT ABS(p.quantity) FROM Product p")
})
public final class Product
{
	private ShelfLife shelfLife;
	private int quantity;
	private String partNumber;

	public Product()
	{
		super();
	}

	public String getPartNumber()
	{
		return partNumber;
	}

	public int getQuantity()
	{
		return quantity;
	}

	public ShelfLife getShelfLife()
	{
		return shelfLife;
	}

	public void setPartNumber(String partNumber)
	{
		this.partNumber = partNumber;
	}

	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}

	public void setShelfLife(ShelfLife shelfLife)
	{
		this.shelfLife = shelfLife;
	}
}