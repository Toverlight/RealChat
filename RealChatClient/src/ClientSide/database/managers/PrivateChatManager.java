package ClientSide.database.managers;

import ClientSide.database.dataItems.PrivateChatItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 私聊记录管理器，用于处理私聊（privateChat）表相关操作
 */
public class PrivateChatManager implements ClientDbManager {

    public static class DatabaseConnection {
        private static final String DATABASE_URL = "jdbc:sqlite:Client\\ChatRecord.db";  // 假设数据库名为ChatRecord.db

        static {
            try {
                // 加载SQLite驱动（对于SQLite 3.x，JDBC驱动是自动加载的，因此不需要显式调用Class.forName）
                DriverManager.getConnection(DATABASE_URL); // 确保数据库文件已存在
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        /**
         * 获取数据库连接
         *
         * @return 数据库连接对象
         * @throws SQLException 数据库连接异常
         */
        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(DATABASE_URL);
        }

        /**
         * 添加私聊记录
         * @param senderUid 发送者用户ID
         * @param receiverUid 接收者用户ID
         * @param message 消息内容
         * @return 是否添加成功
         */
        public static boolean addPrivateChatRecord(String senderUid, String receiverUid, String message,
                                                   String recorderUid) {
            String query = "INSERT INTO privateChat (senderUid, receiverUid, message, recorderUid) VALUES (?,?,?,?)";
            boolean flag = false;

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, senderUid);
                statement.setString(2, receiverUid);
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
         * 获取与指定用户的私聊记录（双向获取，即发送和接收的记录都获取）
         *
         * @param myUid 当前用户ID
         * @param targetUid 目标用户ID
         * @return 私聊记录数据项列表
         */
        public static List<PrivateChatItem> getPrivateChatRecords(String myUid, String targetUid) {
            List<PrivateChatItem> chatRecords = new ArrayList<>();
            String query1 = "SELECT prId, senderUid, receiverUid, message, recorderUid, prDatetime " +
                    "FROM privateChat WHERE  ((senderUid =? AND receiverUid =?) OR (senderUid =? AND receiverUid =?))" +
                    "AND recorderUid =?";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query1)) {

                statement.setString(1, myUid);
                statement.setString(2, targetUid);
                statement.setString(3, targetUid);
                statement.setString(4, myUid);
                statement.setString(5, myUid);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    PrivateChatItem item = new PrivateChatItem();
                    item.prId = resultSet.getString("prId");
                    item.senderUid = resultSet.getString("senderUid");
                    item.receiverUid = resultSet.getString("receiverUid");
                    item.message = resultSet.getString("message");
                    item.recorderUid = resultSet.getString("recorderUid");
                    item.prDatetime = resultSet.getTimestamp("prDatetime").toLocalDateTime();
                    chatRecords.add(item);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return chatRecords;
        }

        /**
         * 根据发送者和接收者删除私聊记录（可用于清空与某用户的聊天记录等情况）
         *
         * @param senderUid 发送者用户ID
         * @param receiverUid 接收者用户ID
         * @return 是否删除成功，成功返回true，失败返回false
         */
        public static boolean deletePrivateChatRecords(String senderUid, String receiverUid, String recorderUid) {
            String query = "DELETE FROM privateChat WHERE ((senderUid =? AND receiverUid =?) OR" +
                    " (senderUid =? AND receiverUid =?)) AND recorderUid = ?";
            boolean flag = false;

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, senderUid);
                statement.setString(2, receiverUid);
                statement.setString(3, receiverUid);
                statement.setString(4, senderUid);
                statement.setString(5, recorderUid);
                int rowsAffected = statement.executeUpdate();
                flag = (rowsAffected > 0);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return flag;
        }

        public static void main(String[] args) {
//            // 测试添加私聊记录
//            int testSenderUid = 1;
//            int testReceiverUid = 2;
//            String testMessage = "这是一条测试私聊消息";
//            boolean addResult = addPrivateChatRecord(testSenderUid, testReceiverUid, testMessage);
//            System.out.println("添加私聊记录结果: " + addResult);
//
//            // 测试获取与指定用户的私聊记录
//            List<PrivateChatItem> getRecordsResult = getPrivateChatRecords(testSenderUid, testReceiverUid);
//            System.out.println("获取到的私聊记录列表:");
//            for (PrivateChatItem item : getRecordsResult) {
//                System.out.println("发送者ID: " + item.senderUid + ", 接收者ID: " + item.receiverUid + ", 消息内容: " + item.message + ", 发送时间: " + item.prDatetime);
//            }
//
//            // 测试删除与指定用户的私聊记录
//            boolean deleteResult = deletePrivateChatRecords(testSenderUid, testReceiverUid);
//            System.out.println("删除私聊记录结果: " + deleteResult);
//
//            // 再次测试获取与指定用户的私聊记录，查看删除操作后列表变化
//            getRecordsResult = getPrivateChatRecords(testSenderUid, testReceiverUid);
//            System.out.println("再次获取到的私聊记录列表:");
//            for (PrivateChatItem item : getRecordsResult) {
//                System.out.println("发送者ID: " + item.senderUid + ", 接收者ID: " + item.receiverUid + ", 消息内容: " + item.message + ", 发送时间: " + item.prDatetime);
//            }
        }
    }
}