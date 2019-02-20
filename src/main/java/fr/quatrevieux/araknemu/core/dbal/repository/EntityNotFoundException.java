package fr.quatrevieux.araknemu.core.dbal.repository;

/**
 * Base exception for entity not found
 */
public class EntityNotFoundException extends RepositoryException {
    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
