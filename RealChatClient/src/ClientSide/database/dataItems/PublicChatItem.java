package ClientSide.database.dataItems;

import java.time.LocalDateTime;

/**
 * <h1>群聊记录数据项</h1>
 * <p>
 * - puId: String<br>
 * - gid: String<br>
 * - senderUid: String<br>
 * - message: String 群聊消息<br>
 * - recorderUid: String 记录者 uid<br>
 * - puDatetime: LocalDateTime 群聊的日期与时间<br>
 * </p>
 */
public class PublicChatItem implements ClientDataItem{
    public String puId;
    public String gid;
    public String senderUid;
    public String message;
    public String recorderUid;
    public LocalDateTime puDatetime;
}
