package github.maxsuel.agregadordeinvestimentos.exceptions;

public class InsufficientSharesException extends RuntimeException {
    public InsufficientSharesException(String message) {
        super(message);
    }
}
