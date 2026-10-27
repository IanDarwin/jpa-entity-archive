package jpaplay;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type",
        discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="MemberBase")
@Table(name="Member")
public class MemberBase {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	long id;
	String name;
	String address;

	public String toString() {
		return getClass().getSimpleName() + "[" + name + "]";
	}
}
