package ServerSide.database.exceptions;

public class UserDeleteFailedException extends DeleteFailedException{
    public UserDeleteFailedException(String message) {
        super(message);
    }
}
