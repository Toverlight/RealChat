package ServerSide.database.managers;

import ServerSide.database.dataItems.UserItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户数据管理器
 */
public class UsersManager implements ServerDbManager {

    private static class DatabaseConnection {
        private static final String DATABASE_URL = "jdbc:sqlite:Server\\UserData.db";  // SQLite数据库路径

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
    }

    // 用于记录已存在的邮箱，方便后续检查邮箱重复性，同时记录对应的uid
    private static Map<String, String> existingEmails = new HashMap<>();
    // 记录下一个可用的uid值，初始化为100000
    private static int nextAvailableUid = 100000;

    public static int getNextAvailableUid() {
        return nextAvailableUid;
    }

    // 查询数据库中已有的最大uid值，用于初始化nextAvailableUid
    public static void initNextAvailableUid() {
        String query = "SELECT MAX(uid) AS max_uid FROM users";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int maxUidFromDb = resultSet.getInt("max_uid");
                nextAvailableUid = Math.max(nextAvailableUid, maxUidFromDb + 1);
            }
        } catch (SQLException e) {
            System.err.println("查询最大uid值时出现SQL异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 根据六位uid找用户
     *
     * @param uid 用户的UID
     * @return 找到的用户信息封装在UserItem中返回，如果用户没被找到，则返回null。
     */
    public static UserItem findUser(String uid) {
        UserItem userItem = null;
        String query = "SELECT * FROM users WHERE uid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, Integer.parseInt(uid));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                userItem = new UserItem();
                userItem.uid = String.valueOf(resultSet.getInt("uid"));
                userItem.nickname = resultSet.getString("nickname");
                userItem.email = resultSet.getString("email");
                userItem.password = resultSet.getString("password");

                // 处理注册时间字段
                Timestamp registerTimestamp = resultSet.getTimestamp("registerDatetime");
                if (registerTimestamp!= null) {
                    userItem.registerDatetime = registerTimestamp.toLocalDateTime();
                }
            }
        } catch (SQLException e) {
            System.err.println("查找用户时出现SQL异常: " + e.getMessage());
            e.printStackTrace();
        }

        return userItem;
    }

    /**
     * 添加用户
     *
     * @param userItem 用户数据项
     * @return 是否成功添加用户，成功返回true，失败返回false。
     */
    public static boolean insertUser(UserItem userItem) {
        String query = "INSERT INTO users (uid, nickname, email, password, registerDatetime) VALUES (?,?,?,?,?)";
        boolean flag = false;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // 设置uid值为下一个可用的uid
            statement.setInt(1, nextAvailableUid);
            statement.setString(2, userItem.nickname);
            statement.setString(3, userItem.email);
            statement.setString(4, userItem.password);
            statement.setTimestamp(5, Timestamp.valueOf(userItem.registerDatetime));

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                userItem.uid = String.valueOf(nextAvailableUid);
                // 更新下一个可用的uid值，使其自增
                nextAvailableUid++;
                flag = true;
                // 将新添加用户的邮箱记录下来，用于后续邮箱重复性检查
                existingEmails.put(userItem.email, userItem.uid);
            }
        } catch (SQLException e) {
            System.err.println("添加用户时出现SQL异常: " + e.getMessage());
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 删除用户
     *
     * @param uid 用户UID
     * @return 是否成功删除用户，成功返回true，失败返回false。
     */
    public static boolean deleteUser(String uid) {
        String query = "DELETE FROM users WHERE uid =?";
        boolean flag = false;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, uid);
            int rowsAffected = statement.executeUpdate();
            flag = (rowsAffected > 0);
            System.out.println("执行的删除语句: " + statement.toString());
            System.out.println("传入的删除参数: uid = " + uid);
            // 如果用户删除成功，从记录邮箱的集合中移除对应的邮箱记录
            UserItem user = findUser(uid);
            if (user!= null) {
                existingEmails.remove(user.email);
            }
        } catch (SQLException e) {
            System.err.println("删除用户时出现SQL异常: " + e.getMessage());
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 修改用户昵称
     *
     * @param uid 要操作的用户
     * @param newNickname 新昵称
     * @return 是否成功修改用户昵称，成功返回true，失败返回false。
     */
    public static boolean modifyUserNickname(String uid, String newNickname) {
        String query = "UPDATE users SET nickname =? WHERE uid =?";
        boolean flag = false;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newNickname);
            statement.setString(2, uid);
            int rowsAffected = statement.executeUpdate();
            flag = (rowsAffected > 0);
            System.out.println("执行的更新语句: " + statement.toString());
            System.out.println("传入的参数: uid = " + uid + ", newNickname = " + newNickname);
        } catch (SQLException e) {
            System.err.println("修改用户昵称时出现SQL异常: " + e.getMessage());
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 修改用户密码
     *
     * @param uid 要操作的用户
     * @param newPassword 新密码
     * @return 是否成功修改用户密码，成功返回true，失败返回false。
     */
    public static boolean modifyUserPassword(String uid, String newPassword) {
        String query = "UPDATE users SET password =? WHERE uid =?";
        boolean flag = false;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newPassword);
            statement.setString(2, uid);
            int rowsAffected = statement.executeUpdate();
            flag = (rowsAffected > 0);
            System.out.println("执行的更新语句: " + statement.toString());
            System.out.println("传入的参数: uid = " + uid + ", newPassword = " + newPassword);
        } catch (SQLException e) {
            System.err.println("修改用户密码时出现SQL异常: " + e.getMessage());
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 修改用户邮箱
     *
     * @param uid 要操作的用户
     * @param newEmail 新邮箱
     * @return 是否成功修改用户邮箱，成功返回true，失败返回false。
     */
    public static boolean modifyUserEmail(String uid, String newEmail) {
        String query = "UPDATE users SET email =? WHERE uid =?";
        boolean flag = false;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // 检查新邮箱是否已存在且不属于当前要修改的用户
            if (existingEmails.containsKey(newEmail) &&!existingEmails.get(newEmail).equals(uid)) {
                System.err.println("要修改的邮箱已被其他用户使用，无法修改");
                return false;
            }
            statement.setString(1, newEmail);
            statement.setString(2, uid);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // 如果修改成功，更新已记录的邮箱信息
                existingEmails.put(newEmail, uid);
                flag = true;
            }
            System.out.println("执行的更新语句: " + statement.toString());
            System.out.println("传入的参数: uid = " + uid + ", newEmail = " + newEmail);
        } catch (SQLException e) {
            System.err.println("修改用户邮箱时出现SQL异常: " + e.getMessage());
            e.printStackTrace();
        }

        return flag;
    }

    public static void main(String[] args) {
        // 初始化下一个可用的uid值
        initNextAvailableUid();

        //  测试添加用户
        UserItem testUser = new UserItem();
        testUser.nickname = "测试用户";
        testUser.email = "test@example.com";
        testUser.password = "123456";
        testUser.registerDatetime = LocalDateTime.now();

        boolean insertResult = insertUser(testUser);
        System.out.println("添加用户结果: " + insertResult);

         // 获取添加成功后的用户UID用于后续测试
        String testUid = testUser.uid;
        if (testUid == null) {
            System.err.println("添加用户后未正确获取到UID，无法进行后续测试");
            return;
        }

//        // 测试删除用户
//        boolean deleteResult = deleteUser(testUid);
//        System.out.println("删除用户结果: " + deleteResult);
//
//        // 测试修改用户昵称
//        boolean modifyNicknameResult = modifyUserNickname(testUid, "修改后的昵称");
//        System.out.println("修改用户昵称结果: " + modifyNicknameResult);
//
//        // 测试修改用户密码
//        boolean modifyPasswordResult = modifyUserPassword(testUid, "新密码");
//        System.out.println("修改用户密码结果: " + modifyPasswordResult);
//
//        // 测试修改用户邮箱
//        boolean modifyEmailResult = modifyUserEmail(testUid, "new_email@example.com");
//        System.out.println("修改用户邮箱结果: " + modifyEmailResult);

        // 测试查找用户（查找刚刚操作过的用户，看是否符合预期）
        UserItem foundUser = findUser("100002");
        if (foundUser!= null) {
            System.out.println("查找用户结果: 找到用户，昵称: " + foundUser.nickname + ", 邮箱: " + foundUser.email);
        } else {
            System.out.println("查找用户结果: 未找到用户");
        }
    }
}