package ClientSide.customized.checker;

public class UIDChecker implements ValidChecker{
    private final Checker checker;

    public UIDChecker() {
        checker = new Checker();
    }

    /**
     * 检查UID的合法性
     * @param content 要检测的内容
     * @return 是否合法
     */
    @Override
    public boolean checkValid(String content) {
        boolean flag = false;
        if (checker.hasOnlyDigits(content) && checker.isLengthValued(content, 6)) {
            flag = true;
        }
        return flag;
    }
}
