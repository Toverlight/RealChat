package ServerSide.database.exceptions;

public class UserNotFoundException extends DataItemNotFoundException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
