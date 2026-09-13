package ClientSide.customized.checker;

/**
 * 昵称合法性检查器
 */
public class NicknameChecker implements ValidChecker{
    private final Checker checker;

    public NicknameChecker() {
        checker = new Checker();
    }

    /**
     * 检查昵称的合法性
     * @param content 要检测的内容
     * @return 是否合法
     */
    @Override
    public boolean checkValid(String content) {
        boolean flag = false;
        if ((!checker.hasOtherZhCN(content)) && checker.isLengthBetween(content, 1, 18)) {
            flag = true;
        }
        return flag;
    }
}
