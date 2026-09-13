package ServerSide.database.exceptions;

public class ModifyFailedException extends ServerDbException{
    public ModifyFailedException(String message) {
        super(message);
    }
}
