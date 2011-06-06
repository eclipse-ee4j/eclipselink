package jpql.query;

import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries
({
	@NamedQuery(name="product.abs",      query="SELECT ABS(p.quantity) FROM Product p"),
	@NamedQuery(name="product.date",     query="SELECT DISTINCT p From Product p where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate"),
	@NamedQuery(name="product.int1",     query="Select Distinct Object(p) from Product p where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL)"),
	@NamedQuery(name="product.max",      query="SELECT MAX(p.quantity) FROM Product p"),
	@NamedQuery(name="product.min",      query="SELECT MIN(p.quantity) FROM Product p"),
	@NamedQuery(name="product.null",     query="UPDATE Product AS p SET p.partNumber = NULL"),
	@NamedQuery(name="product.quantity", query="SELECT AVG(p.quantity) FROM Product p"),
	@NamedQuery(name="product.update1",  query="UPDATE Product SET shelfLife.soldDate = CURRENT_DATE WHERE shelfLife IS NOT NULL AND shelfLife.soldDate <> CURRENT_DATE"),
	@NamedQuery(name="product.update2",  query="UPDATE Product SET partNumber = CASE enumType WHEN com.titan.domain.EnumType.FIRST_NAME THEN '1' WHEN com.titan.domain.EnumType.LAST_NAME THEN '2' ELSE '3' END"),
	@NamedQuery(name="product.update3",  query="UPDATE Product SET partNumber = CASE TYPE(project) WHEN LargeProject THEN '2' WHEN SmallProject THEN '3' ELSE '4' END")
})
@SuppressWarnings("unused")
public class Product extends AbstractProduct {

	@Id private int id;
	private Project project;
	private int quantity;
	private Date releaseDate;
	private ShelfLife shelfLife;
	private EnumType enumType;
}