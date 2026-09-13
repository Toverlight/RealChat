package ClientSide.database.dataItems;

import java.time.LocalDateTime;

/**
 * <h1>系统通知数据项</h1>
 * <p>
 * - nid: String<br>
 * - speakerUid: String 发言人 id<br>
 * - message: String 通知消息<br>
 * - recorderUid: String 记录者 uid<br>
 * - nDatetime: LocalDateTime 通知收到的日期与时间<br>
 * </p>
 */
public class NotificationItem implements ClientDataItem{
    public String nid;
    public String speakerUid;
    public String message;
    public String recorderUid;
    public LocalDateTime nDatetime;
}
