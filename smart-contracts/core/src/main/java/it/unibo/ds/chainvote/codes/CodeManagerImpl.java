package it.unibo.ds.chainvote.codes;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A base implementation of {@link CodeManager} which implements main core logic, independent of any technology.
 * @param <C> the type of the context.
 */
public final class CodeManagerImpl<C> implements CodeManager<C> {

    private final CodeGeneratorStrategy codeGenerator;
    private final CodeRepository<C> codeRepository;

    /**
     * Creates a new manager.
     * @param repo the {@link CodeRepository} to use to retrieve/store data.
     * @param generator TODO
     */
    @SuppressFBWarnings("EI2")
    public CodeManagerImpl(final CodeRepository<C> repo, final CodeGeneratorStrategy generator) {
        this.codeRepository = repo;
        this.codeGenerator = generator;
    }

    @Override
    public OneTimeCode generateCodeFor(
        final C context,
        final String electionId,
        final String userId
    ) throws AlreadyGeneratedCodeException {
        return generate(context, electionId, userId, codeGenerator.generateCode());
    }

    @Override
    public OneTimeCode generateCodeFor(
        final C context,
        final String electionId,
        final String userId,
        final String arg
    ) throws AlreadyGeneratedCodeException {
        return generate(context, electionId, userId, codeGenerator.generateCode(arg));
    }

    private OneTimeCode generate(
        final C context,
        final String electionId,
        final String userId,
        final OneTimeCode generatedCode
    ) throws AlreadyGeneratedCodeException {
        if (codeRepository.get(context, electionId, userId).isPresent()) {
            throw new AlreadyGeneratedCodeException(
                "A one-time-code for the given election and user has already been generated"
            );
        }
        codeRepository.put(context, electionId, userId, generatedCode);
        return generatedCode;
    }


    @Override
    public boolean isValid(final C context, final String electionId, final String userId, final OneTimeCode code) {
        final var searchedCode = codeRepository.get(context, electionId, userId);
        if (searchedCode.isEmpty() || !searchedCode.get().equals(code)) {
            return false;
        }
        return !searchedCode.get().consumed();
    }

    @Override
    public void invalidate(
        final C context,
        final String electionId,
        final String userId,
        final OneTimeCode code
    ) throws IncorrectCodeException, InvalidCodeException {
        final var searchedCode = codeRepository.get(context, electionId, userId);
        if (searchedCode.isEmpty() || !searchedCode.get().equals(code)) {
            throw new IncorrectCodeException("The given code is not associated to the given user for the given voting");
        }
        searchedCode.get().consume();
        codeRepository.replace(context, electionId, userId, searchedCode.get());
    }

    @Override
    public boolean verifyCodeOwner(
        final C context,
        final String electionId,
        final String userId,
        final OneTimeCode code
    ) {
        final var searchedCode = codeRepository.get(context, electionId, userId);
        return searchedCode.isPresent() && searchedCode.get().equals(code);
    }
}
