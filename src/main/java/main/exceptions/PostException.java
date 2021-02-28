package main.exceptions;

public class PostException extends RuntimeException {

    public PostException(long id) {

        super(String.format("Post with Id %d not found", id));
    }
}
