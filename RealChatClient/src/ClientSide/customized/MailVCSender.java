package ClientSide.customized;

import java.util.Random;

public class MailVCSender {

    public String generateVerificationCode(String email) {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }
    public void sendVerificationCode(String email) {

    }
}
