package it.unibo.ds.core.codes;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * A base implementation of {@link CodeManager} which implements main core logic, independent of any technology.
 * @param <C> the type of the context.
 */
public final class CodeManagerImpl<C> implements CodeManager<C> {

    private final CodeGeneratorStrategy codeGenerator = new SecureRandomGenerator();
    private final CodeRepository<C> repo;

    /**
     * Creates a new manager.
     * @param repo the {@link CodeRepository} to use to retrieve/store data.
     */
    @SuppressFBWarnings("EI2")
    public CodeManagerImpl(final CodeRepository<C> repo) {
        this.repo = repo;
    }

    @Override
    public OneTimeCode generateFor(final C context, final String electionId, final String userId) {
        if (repo.get(context, electionId, userId).isPresent()) {
            throw new IllegalStateException("A one-time-code for the given election and user has already been generated");
        }
        final var generated = codeGenerator.generateCode(repo.getAllOf(context, electionId));
        repo.put(context, electionId, userId, generated);
        return generated;
    }

    @Override
    public boolean isValid(final C context, final String electionId, final OneTimeCode code) {
        final var matchingCodes = getMatchingCodes(context, electionId, code);
        return matchingCodes.size() == 1 && !matchingCodes.iterator().next().consumed();
    }

    @Override
    public void invalidate(final C context, final String electionId, final OneTimeCode code) {
        final var matchingCodes = getMatchingCodes(context, electionId, code);
        if (matchingCodes.size() != 1) {
            throw new IllegalStateException("The given code is not associated to the given election.");
        }
        final OneTimeCode searchedCode = matchingCodes.iterator().next();
        searchedCode.consume();
        repo.replace(context, electionId, searchedCode);
    }

    private Set<OneTimeCode> getMatchingCodes(final C context, final String electionId, final OneTimeCode code) {
        return repo.getAllOf(context, electionId).stream()
            .filter(c -> c.equals(code))
            .collect(Collectors.toSet());
    }

    @Override
    public boolean verifyCodeOwner(final C context, final String electionId, final String userId, final OneTimeCode code) {
        final var searchedCode = repo.get(context, electionId, userId);
        return searchedCode.isPresent() && searchedCode.get().equals(code);
    }
}
