package it.unibo.ds.core.codes;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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
    public OneTimeCode generateFor(final C context, final String electionId, final String userId) throws AlreadyGeneratedCodeException {
        if (repo.get(context, electionId, userId).isPresent()) {
            throw new AlreadyGeneratedCodeException(
                "A one-time-code for election `" + electionId + "` and user `" + userId + "` has already been generated"
            );
        }
        final var generated = codeGenerator.generateCode();
        repo.put(context, electionId, userId, generated);
        return generated;
    }

    @Override
    public boolean isValid(final C context, final String electionId, final String userId, final OneTimeCode code) {
        final var searchedCode = repo.get(context, electionId, userId);
        if (searchedCode.isEmpty() || !searchedCode.get().equals(code)) {
            return false;
        }
        return !searchedCode.get().consumed();
    }

    @Override
    public void invalidate(final C context, final String electionId, final String userId, final OneTimeCode code) throws NotValidCodeException, AlreadyConsumedCodeException {
        final var searchedCode = repo.get(context, electionId, userId);
        if (searchedCode.isEmpty() || !searchedCode.get().equals(code)) {
            throw new NotValidCodeException("The given code is not associated to the given user for the given voting");
        }
        searchedCode.get().consume();
        repo.replace(context, electionId, userId, searchedCode.get());
    }

    @Override
    public boolean verifyCodeOwner(final C context, final String electionId, final String userId, final OneTimeCode code) {
        final var searchedCode = repo.get(context, electionId, userId);
        return searchedCode.isPresent() && searchedCode.get().equals(code);
    }
}
