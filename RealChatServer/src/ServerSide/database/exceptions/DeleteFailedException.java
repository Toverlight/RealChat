package ServerSide.database.exceptions;

public class DeleteFailedException extends ServerDbException{
    public DeleteFailedException(String message) {
        super(message);
    }
}
