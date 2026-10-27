package jpaplay;

import java.time.*;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("MemberArchived")
public class MemberArchived extends MemberBase {
	LocalDate archivedDate;
	String reasonDeleted;

	@Override
    public String toString() {
        return "MemberArchived[" + name + "@" + archivedDate + "]";
    }
}
