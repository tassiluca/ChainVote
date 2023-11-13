package it.unibo.ds.chainvote.codes;

import java.util.Optional;

/**
 * A base implementation of {@link CodesManager} which implements main core logic, independent of any technology.
 * @param <C> the type of the context.
 */
public final class CodesManagerImpl<C> implements CodesManager<C> {

    private final CodesGeneratorStrategy codeGenerator;
    private final CodesRepository<C> codesRepository;

    /**
     * Creates a new manager.
     * @param repo the {@link CodesRepository} to use to retrieve/store data.
     * @param generator the {@link CodesGeneratorStrategy} to generate the codes.
     */
    public CodesManagerImpl(final CodesRepository<C> repo, final CodesGeneratorStrategy generator) {
        this.codesRepository = repo;
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
        if (codesRepository.get(context, electionId, userId).isPresent()) {
            throw new AlreadyGeneratedCodeException(
                "A one-time-code for the given election and user has already been generated"
            );
        }
        codesRepository.put(context, electionId, userId, generatedCode);
        return generatedCode;
    }


    @Override
    public boolean isValid(final C context, final String electionId, final String userId, final String code) {
        final Optional<OneTimeCode> searchedCode = codesRepository.get(context, electionId, userId);
        if (searchedCode.isEmpty() || !searchedCode.get().getCode().equals(code)) {
            return false;
        }
        return !searchedCode.get().consumed();
    }

    @Override
    public void invalidate(
        final C context,
        final String electionId,
        final String userId,
        final String code
    ) throws IncorrectCodeException, InvalidCodeException {
        final var searchedCode = codesRepository.get(context, electionId, userId);
        if (searchedCode.isEmpty() || !searchedCode.get().getCode().equals(code)) {
            throw new IncorrectCodeException("The given code is not associated to the given user for the given voting");
        }
        searchedCode.get().consume();
        codesRepository.replace(context, electionId, userId, searchedCode.get());
    }

    @Override
    public boolean verifyCodeOwner(final C context, final String electionId, final String userId, final String code) {
        final var searchedCode = codesRepository.get(context, electionId, userId);
        return searchedCode.isPresent() && searchedCode.get().getCode().equals(code);
    }
}
