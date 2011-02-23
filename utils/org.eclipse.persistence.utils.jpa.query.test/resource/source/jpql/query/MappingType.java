package jpql.query;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.eclipse.persistence.annotations.DiscriminatorClass;
import org.eclipse.persistence.annotations.ReadTransformer;
import org.eclipse.persistence.annotations.Transformation;
import org.eclipse.persistence.annotations.VariableOneToOne;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

@Entity
@SuppressWarnings("unused")
public class MappingType implements Serializable {

	@Basic
	private String basic;
	@ElementCollection
	private Collection<String> element_collection;
	@Embedded
	private EmbeddableClass1 embedded;
	@EmbeddedId
	private EmbeddableClass2 embedded_id;
	@ManyToMany
	private Collection<MappingType> many_to_many;
	@ManyToOne
	@JoinColumn(name="CUST_ID", nullable=false, updatable=false)
	private Something many_to_one;
	@OneToMany
	@JoinTable(name="")
	private Collection<MappingType> one_to_many;
	@OneToOne
	private SomethingElse one_to_one;
	@Transformation
	@ReadTransformer(method="transform")
	private String transformation;
	@Transient
	private String transient_;
	@JoinColumn(name="ABC", referencedColumnName="XYZ")
	@VariableOneToOne(discriminatorClasses = {
			@DiscriminatorClass(discriminator="ONE", value=VariableImplOne.class),
			@DiscriminatorClass(discriminator="TWO", value=VariableImplTwo.class)
		},
		discriminatorColumn=@DiscriminatorColumn(
			name="DISC_COLUMN",
			discriminatorType=DiscriminatorType.STRING
		)
	)
	private Variable variable_one_to_one;

	@Version
	private Long version;

	public String getBasic() {
		return basic;
	}

	public Long getVersion() {
		return version;
	}

	public void setBasic(String basic) {
		this.basic = basic;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String transform(Record record, Session session) {
		return null;
	}

	@Embeddable
	public static class EmbeddableClass1 implements Serializable {
		private int type;
	}

	@Embeddable
	public static class EmbeddableClass2 implements Serializable {
		private int type;
	}

	@Entity
	public static class Something implements Serializable {
		@Id
		private long id;
		@OneToMany
		private Collection<MappingType> types;
	}

	@Entity
	public static class SomethingElse implements Serializable {
		@Id
		private long id;
	}

	public static interface Variable {
	}

	@Entity
	public static class VariableImplOne implements Variable {

		@Id
		private int id;

		public VariableImplOne() {
			super();
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}

	@Entity
	public static class VariableImplTwo implements Variable {

		@Id
		private int id;

		public VariableImplTwo() {
			super();
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}
}