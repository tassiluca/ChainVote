package assets;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Ballot {

    @Property()
    private final String electionID;

    @Property()
    private final String voterCodeID;

    @Property()
    private final LocalDateTime date;

    @Property()
    private final String choice;

    public String getElectionID() {
        return this.electionID;
    }

    public String getVoterID() {
        return this.voterCodeID;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public String getChoice() {
        return this.choice;
    }

    public Ballot(@JsonProperty("electionID") final String electionID, @JsonProperty("voterCodeID") final String voterCodeID,
                  @JsonProperty("date") final LocalDateTime date, @JsonProperty("choice") final String choice) {
        this.electionID = electionID;
        this.voterCodeID = voterCodeID;
        this.date = date;
        this.choice = choice;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Ballot other = (Ballot) obj;

        return getDate().equals(other.getDate())
                &&
                Objects.deepEquals(
                        new String[] {getElectionID(), getVoterID(), getChoice()},
                        new String[] {other.getElectionID(), other.getVoterID(), other.getChoice()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getElectionID(), getVoterID(), getDate(), getChoice());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [electionID=" + this.electionID + ", voterID="
                + this.voterCodeID + ", date=" + this.date + ", choice=" + this.choice + "]";
    }
}
