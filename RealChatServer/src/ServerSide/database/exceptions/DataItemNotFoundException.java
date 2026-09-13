package ServerSide.database.exceptions;

public class DataItemNotFoundException extends ServerDbException{
    public DataItemNotFoundException(String message) {
        super(message);
    }
}
