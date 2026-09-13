package ClientSide.database.managers;

import ClientSide.database.dataItems.PublicChatItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 群聊记录管理器，用于处理群聊（publicChat）表相关操作
 */
public class PublicChatManager {

    private static final String DATABASE_URL = "jdbc:sqlite:Client\\ChatRecord.db";

    /**
     * 添加群聊记录
     *
     * @param gid 群组ID
     * @param senderUid 发送者用户ID
     * @param message 消息内容
     * @return 是否添加成功
     */
    public static boolean addPublicChatRecord(String gid, String senderUid, String message, String recorderUid) {
        String query = "INSERT INTO publicChat (gid, senderUid, message, recorderUid) VALUES (?,?,?,?)";
        boolean flag = false;

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, gid);
            statement.setString(2, senderUid);
            statement.setString(3, message);
            statement.setString(4, recorderUid);
            int rowsAffected = statement.executeUpdate();
            flag = (rowsAffected > 0);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 获取指定群组的群聊记录（这里假设除了根据gid查询外，还可以根据senderUid等更多条件查询，示例中添加了senderUid作为额外条件，你可按需调整）
     *
     * @param gid 群组ID
     * @param senderUid 发送者用户ID（新增的参数，用于更精准查询，可按需修改或添加更多参数）
     * @return 群聊记录数据项列表
     */
    public static List<PublicChatItem> getPublicChatRecords(String gid, String senderUid, String recorderUid) {
        List<PublicChatItem> chatRecords = new ArrayList<>();
        String query = "SELECT gid, senderUid, message, recorderUid, puDatetime FROM publicChat " +
                "WHERE gid =? AND senderUid =? AND recorderUid =?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            // 设置gid参数
            statement.setString(1, gid);
            // 设置senderUid参数
            statement.setString(2, senderUid);
            statement.setString(3, recorderUid);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                PublicChatItem item = new PublicChatItem();
                item.gid = resultSet.getString("gid");
                item.senderUid = resultSet.getString("senderUid");
                item.message = resultSet.getString("message");
                item.recorderUid = resultSet.getString("recorderUid");
                item.puDatetime = resultSet.getTimestamp("puDatetime").toLocalDateTime();
                chatRecords.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chatRecords;
    }

    /**
     * 获取指定群组的所有群聊记录
     * @param gid 群组ID
     * @return 群聊记录数据项列表
     */
    public static List<PublicChatItem> getPublicChatRecords(String gid, String recorderUid) {
        List<PublicChatItem> chatRecords = new ArrayList<>();
        String query = "SELECT gid, senderUid, message, recorderUid, puDatetime FROM publicChat " +
                "WHERE gid =? AND recorderUid =?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            // 设置gid参数
            statement.setString(1, gid);
            statement.setString(2, recorderUid);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                PublicChatItem item = new PublicChatItem();
                item.gid = resultSet.getString("gid");
                item.senderUid = resultSet.getString("senderUid");
                item.message = resultSet.getString("message");
                item.recorderUid = resultSet.getString("recorderUid");
                item.puDatetime = resultSet.getTimestamp("puDatetime").toLocalDateTime();
                chatRecords.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chatRecords;
    }

    /**
     * 根据群组ID删除群聊记录（可用于清空群组聊天记录等情况）
     *
     * @param gid 群组ID
     * @return 是否删除成功，成功返回true，失败返回false
     */
    public static boolean deletePublicChatRecords(String gid, String recorderUid) {
        String query = "DELETE FROM publicChat WHERE gid =? AND recorderUid =?";
        boolean flag = false;

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, gid);
            statement.setString(2, recorderUid);
            int rowsAffected = statement.executeUpdate();
            flag = (rowsAffected > 0);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flag;
    }

    public static void main(String[] args) {
//        // 测试添加群聊记录
//        int testGid = 1;
//        int testSenderUid = 1001;
//        String testMessage = "这是一条测试群聊消息";
//        boolean addResult = addPublicChatRecord(testGid, testSenderUid, testMessage);
//        System.out.println("添加群聊记录结果: " + addResult);
//
//        // 测试获取指定群组的群聊记录，这里传入gid和senderUid两个参数进行查询（需确保参数值符合实际数据库中的记录情况）
//        List<PublicChatItem> getRecordsResult = getPublicChatRecords(testGid, testSenderUid);
//        System.out.println("获取到的群聊记录列表:");
//        for (PublicChatItem item : getRecordsResult) {
//            System.out.println("群组ID: " + item.gid + ", 发送者ID: " + item.senderUid + ", 消息内容: " + item.message + ", 发送时间: " + item.puDatetime);
//        }
//
//        // 测试删除指定群组的群聊记录
//        boolean deleteResult = deletePublicChatRecords(testGid);
//        System.out.println("删除群聊记录结果: " + deleteResult);
//
//        // 再次测试获取指定群组的群聊记录，查看删除操作后列表变化
//        getRecordsResult = getPublicChatRecords(testGid, testSenderUid);
//        System.out.println("再次获取到的群聊记录列表:");
//        for (PublicChatItem item : getRecordsResult) {
//            System.out.println("群组ID: " + item.gid + ", 发送者ID: " + item.senderUid + ", 消息内容: " + item.message + ", 发送时间: " + item.puDatetime);
//        }
    }
}