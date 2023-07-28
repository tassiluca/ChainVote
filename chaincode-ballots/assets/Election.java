package assets;

import java.util.*;
import java.time.LocalDateTime;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Election {

    @Property()
    private final String electionID;

    @Property()
    private final String goal;

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

    // Only for debug
    @Property()
    private final Map<String, String> voteAccountability;

    @Property()
    private final Set<String> ballots;

    public enum FixedVotes {
        INFORMAL_BALLOT("INVALID_BALLOT");

        private final String label;

        FixedVotes(String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }
    }

    public String getElectionID() {
        return this.electionID;
    }

    public String getGoal() {
        return this.goal;
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

    public Election(@JsonProperty("goal") final String goal,
            @JsonProperty("votersNumber") final long votersNumber, @JsonProperty("startingDate") final LocalDateTime startingDate,
                    @JsonProperty("endingDate") final LocalDateTime endingDate, @JsonProperty("choices") final List<String> choices) {
        this.goal = goal;
        this.votersNumber = votersNumber;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.choices = List.copyOf(choices);
        this.results = new HashMap<>();
        for (String choice: this.choices) {
            this.results.put(choice, (long) 0);
        }
        if (!this.choices.contains(FixedVotes.INFORMAL_BALLOT.getLabel())) {
            this.results.put(FixedVotes.INFORMAL_BALLOT.getLabel(), (long) 0);
        }
        this.ballots = new HashSet<>();
        this.voteAccountability = new HashMap<>();
        this.electionID = String.valueOf(this.hashCode());
    }

    public Boolean castVote(final String choice, final String voterID) {
        if (this.choices.contains(choice) &&
                this.results.values().stream().reduce(Long::sum).orElseThrow() < this.votersNumber &&
                !this.voteAccountability.containsKey(voterID)) {
            this.ballots.add(choice);
            this.voteAccountability.put(voterID, choice);
            long oldValue = this.results.get(choice);
            return this.results.replace(choice, oldValue, oldValue + 1);
        }
        return false;
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

        return getElectionID().equals(other.getElectionID()) &&
                getGoal().equals(other.getGoal()) &&
                getVotersNumber() == other.getVotersNumber() &&
                Objects.deepEquals(
                        new LocalDateTime[] {getStartingDate(), getEndingDate()},
                        new LocalDateTime[] {other.getStartingDate(), other.getEndingDate()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGoal(), getStartingDate(), getEndingDate(), getChoices());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [electionID=" + this.electionID +
                ", goal=" + this.goal + ", voters=" + this.votersNumber + ", starting=" + this.startingDate + ", ending=" +
                this.endingDate + ", choices=" + this.choices + "]";
    }
}
