package ClientSide.database.managers;

import ClientSide.database.dataItems.NotificationItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统通知数据管理器，用于处理系统通知（notification）表相关操作
 */
public class NotificationManager implements ClientDbManager {

    public static class DatabaseConnection {
        private static final String DATABASE_URL = "jdbc:sqlite:Client\\ChatRecord.db";  // 数据库名为ChatRecord.db

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
         * 添加系统通知
         *
         * @param message 通知内容
         * @return 是否添加成功
         */
        public static boolean addNotification(String speakerUid, String message, String recorderUid) {
            String query = "INSERT INTO notification (speakerUid, message, recorderUid) VALUES (?,?,?)";
            boolean flag = false;

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, speakerUid);
                statement.setString(2, message);
                statement.setString(3, recorderUid);
                int rowsAffected = statement.executeUpdate();
                flag = (rowsAffected > 0);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return flag;
        }

        /**
         * 获取所有系统通知
         *
         * @return 系统通知数据项列表
         */
        public static List<NotificationItem> getNotifications(String recorderUid) {
            List<NotificationItem> notifications = new ArrayList<>();
            String query = "SELECT nid, speakerUid, message, recorderUid, nDatetime " +
                    "FROM notification WHERE recorderUid =?";

            try (Connection connection = DatabaseConnection.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, recorderUid);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    NotificationItem item = new NotificationItem();
                    item.nid = String.valueOf(resultSet.getInt("nid"));
                    item.speakerUid = resultSet.getString("speakerUid");
                    item.message = resultSet.getString("message");
                    item.recorderUid = resultSet.getString("recorderUid");
                    item.nDatetime = resultSet.getTimestamp("nDatetime").toLocalDateTime();
                    notifications.add(item);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return notifications;
        }

        /**
         * 根据通知ID删除系统通知
         *
         * @param nid 通知ID
         * @return 是否删除成功，成功返回true，失败返回false
         */
        public static boolean deleteNotification(int nid, String recorderUid) {
            String query = "DELETE FROM notification WHERE nid =? AND recorderUid =?";
            boolean flag = false;

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, nid);
                statement.setString(2, recorderUid);
                int rowsAffected = statement.executeUpdate();
                flag = (rowsAffected > 0);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return flag;
        }

        public static void main(String[] args) {
//            // 测试添加系统通知
//            String speakerUid = "000000";   // RealChat官方号
//            String testMessage = "这是一条测试通知";
//            boolean addResult = addNotification(speakerUid, testMessage);
//            System.out.println("添加系统通知结果: " + addResult);
//
//            // 测试获取所有系统通知
//            List<NotificationItem> getNotificationsResult = getNotifications();
//            System.out.println("获取到的系统通知列表:");
//            for (NotificationItem item : getNotificationsResult) {
//                System.out.println("通知ID: " + item.nid + ", 通知内容: " + item.message + ", 通知时间: " + item.nDatetime);
//            }
//
//            // 假设获取到的通知列表不为空，取第一个通知的ID用于删除测试（实际应用中需根据具体情况选择要删除的ID）
//            if (!getNotificationsResult.isEmpty()) {
//                int testNid = Integer.parseInt(getNotificationsResult.get(0).nid);
//                // 测试根据通知ID删除系统通知
//                boolean deleteResult = deleteNotification(testNid);
//                System.out.println("删除系统通知结果: " + deleteResult);
//
//                // 再次测试获取所有系统通知，查看删除操作后列表变化
//                getNotificationsResult = getNotifications();
//                System.out.println("再次获取到的系统通知列表:");
//                for (NotificationItem item : getNotificationsResult) {
//                    System.out.println("通知ID: " + item.nid + ", 通知内容: " + item.message + ", 通知时间: " + item.nDatetime);
//                }
//            } else {
//                System.out.println("没有可用于删除测试的系统通知");
//            }
        }
    }
}