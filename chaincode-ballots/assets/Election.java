package assets;

import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.LongStream;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Election {

    @Property()
    private final String electionID;

    @Property()
    private final long votersNumber;

    @Property()
    private final LocalDateTime startingDate;

    @Property()
    private final LocalDateTime endingDate;

    @Property()
    private final List<String> choices;

    @Property()
    private final Map<String, Long> results;

    public String getElectionID() {
        return this.electionID;
    }

    public long getVotersNumber() {
        return this.votersNumber;
    }

    // LocalDateTime is immutable
    public LocalDateTime getStartingDate() {
        return this.startingDate;
    }

    public LocalDateTime getEndingDate() {
        return this.endingDate;
    }

    public List<String> getChoices() {
        return List.copyOf(this.choices);
    }

    public Map<String, Long> getResults() {
        return Map.copyOf(this.results);
    }

    public Boolean castVote(final String choice) {
        if (this.choices.contains(choice) && this.results.values().stream().reduce(Long::sum).orElseThrow() < this.votersNumber) {
            long oldValue = this.results.get(choice);
            return this.results.replace(choice, oldValue, oldValue + 1);
        }
        return false;
    }

    public Election(@JsonProperty("electionID") final String electionID, @JsonProperty("votersNumber") final long votersNumber,
                    @JsonProperty("startingDate") final LocalDateTime startingDate, @JsonProperty("endingDate") final LocalDateTime endingDate,
                    @JsonProperty("choices") final List<String> choices) {
        this.electionID = electionID;
        this.votersNumber = votersNumber;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.choices = List.copyOf(choices);
        this.results = new HashMap<>();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Election other = (Election) obj;

        return getElectionID().equals(other.getElectionID())
                &&
                getVotersNumber() == other.getVotersNumber()
                &&
                Objects.deepEquals(
                        new LocalDateTime[] {getStartingDate(), getEndingDate()},
                        new LocalDateTime[] {other.getStartingDate(), other.getEndingDate()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getElectionID(), getVotersNumber(), getStartingDate(), getEndingDate(), getChoices());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [electionID=" + this.electionID + ", voters="
                + this.votersNumber + ", starting=" + this.startingDate + ", ending=" + this.endingDate + ", choices=" + this.choices + "]";
    }
}
