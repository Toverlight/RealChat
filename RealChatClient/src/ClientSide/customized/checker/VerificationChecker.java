package ClientSide.customized.checker;

/**
 * 验证码合法性检查器
 */
public class VerificationChecker implements ValidChecker{
    private final Checker checker;

    public VerificationChecker() {
        checker = new Checker();
    }

    /**
     * 检查验证码的合法性
     * @param content 要检测的内容
     * @return 是否合法
     */
    @Override
    public boolean checkValid(String content) {
        boolean flag = false;
        if (checker.hasOnlyDigits(content) && checker.isLengthValued(content, 4)) {
            flag = true;
        }
        return flag;
    }
}
