package dev.practice.blogproject.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Long id, Class<?> entity) {
        super("The" + entity.getSimpleName().toLowerCase() + " with a given id " + id + " not found");
    }
}
