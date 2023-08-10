package it.unibo.ds.core.codes;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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
    public OneTimeCode generateFor(final C context, final Long votingId, final String userId) {
        if (repo.get(context, votingId, userId).isPresent()) {
            throw new IllegalStateException("A one-time-code for the given voting and user has already been generated");
        }
        final var generated = codeGenerator.generateCode(repo.getAllOf(context, votingId));
        repo.put(context, votingId, userId, generated);
        return generated;
    }

    @Override
    public boolean isValid(final C context, final Long votingId, final OneTimeCode code) {
        final var matchingCodes = repo.getAllOf(context, votingId).stream()
            .filter(c -> c.equals(code))
            .collect(Collectors.toSet());
        return matchingCodes.size() == 1 && !matchingCodes.iterator().next().consumed();
    }

    @Override
    public void invalidate(final C context, final Long votingId, final OneTimeCode code) {
        final var matchingCodes = repo.getAllOf(context, votingId).stream()
            .filter(c -> c.equals(code))
            .collect(Collectors.toSet());
        if (matchingCodes.size() != 1) {
            throw new IllegalStateException("The given code is not associated to the given voting.");
        }
        final OneTimeCode searchedCode = matchingCodes.iterator().next();
        searchedCode.consume();
        repo.replace(context, votingId, searchedCode);
    }

    @Override
    public boolean verifyCodeOwner(final C context, final Long votingId, final String userId, final OneTimeCode code) {
        final var searchedCode = repo.get(context, votingId, userId);
        return searchedCode.isPresent() && searchedCode.get().equals(code);
    }
}
