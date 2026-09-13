package ClientSide.customized.enums.protocol;

/**
 * RETRIEVE 协议规定的找回账户的错误码
 */
public class RetrieveErrCode implements ProtocolErrCode {
    /**
     * 找回账户（修改新密码）成功
     */
    public static final int OK = 0;
    /**
     * 损坏的请求
     */
    public static final int BAD_REQ = 1;
    /**
     * UID错误（查无此人）
     */
    public static final int UID_ERR = 2;
    /**
     * 验证码已失效
     */
    public static final int VC_INE = 3;
    /**
     * 验证码错误
     */
    public static final int VC_ERR = 4;

}
