package ClientSide.database.dataItems;

import java.time.LocalDateTime;

/**
 * <h1>私聊记录数据项</h1>
 * <p>
 * - prId: String<br>
 * - senderUid: String 发送者uid<br>
 * - receiverUid: String 接收者uid<br>
 * - message: String 私聊消息<br>
 * - recorderUid: String 记录者 uid<br>
 * - prDatetime: LocalDateTime 私聊的日期与时间<br>
 * </p>
 */
public class PrivateChatItem implements ClientDataItem{
    public String prId;
    public String senderUid;
    public String receiverUid;
    public String message;
    public String recorderUid;
    public LocalDateTime prDatetime;
}
