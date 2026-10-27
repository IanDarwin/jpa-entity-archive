package jpaplay;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue(value="Member")
public class Member extends MemberBase {
	// All members inherited

    @Override
    public String toString() {
        return "Member[" + name + "]";
    }
}
