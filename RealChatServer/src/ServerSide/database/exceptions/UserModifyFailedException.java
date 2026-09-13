package ServerSide.database.exceptions;

public class UserModifyFailedException extends ModifyFailedException{
    public UserModifyFailedException(String message) {
        super(message);
    }
}
