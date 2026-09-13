package ServerSide.database.exceptions;

public class InsertFailedException extends ServerDbException{
    public InsertFailedException(String message) {
        super(message);
    }
}
