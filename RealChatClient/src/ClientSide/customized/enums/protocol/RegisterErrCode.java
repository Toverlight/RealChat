package ClientSide.customized.enums.protocol;

/**
 * REGISTER 协议规定的注册错误码
 */
public class RegisterErrCode implements ProtocolErrCode {
    /**
     * 注册成功
     */
    public static final int OK = 0;
    /**
     * 损坏的请求
     */
    public static final int BAD_REQ = 1;
    /**
     * 验证码错误
     */
    public static final int VC_ERR = 2;
    /**
     * 验证码已失效
     */
    public static final int VC_INE = 3;
    /**
     * 邮箱已被注册
     */
    public static final int EMAIL_OCCUPIED = 4;
}
