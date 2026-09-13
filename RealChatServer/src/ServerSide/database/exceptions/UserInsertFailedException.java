package ServerSide.database.exceptions;

public class UserInsertFailedException extends InsertFailedException{
    public UserInsertFailedException(String message) {
        super(message);
    }
}
