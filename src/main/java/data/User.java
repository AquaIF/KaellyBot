package data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 28/07/2016.
 */
public class User {
    public static int RIGHT_INVITE = 0;
    public static int RIGHT_MEMBER = 1;
    public static int RIGHT_MODERATOR = 2;
    public static int RIGHT_ADMIN = 3;

    private final static Logger LOG = LoggerFactory.getLogger(User.class);
    private static Map<String, Map<String, User>> users;
    private String id;
    private String name;
    private int rights;
    private Guild guild;

    public User(String id, String name, int rights, Guild guild){
        this.id = id;
        this.name = name;
        this.rights = rights;
        this.guild = guild;
    }

    public User(String id, String name, Guild guild){
        this(id, name, User.RIGHT_INVITE, guild);
    }

    public void addToDatabase() {
        if (!getUsers().containsKey(guild.getId())) {
            getUsers().put(guild.getId(), new HashMap<String, User>());
        }

        if (! getUsers().get(guild.getId()).containsKey(id)){
            getUsers().get(guild.getId()).put(id, this);

            Connexion connexion = Connexion.getInstance();
            Connection connection = connexion.getConnection();

            try {
                PreparedStatement request = connection.prepareStatement("INSERT INTO"
                        + " User(id_user, name, id_guild, rights) VALUES (?, ?, ?, ?);");
                request.setString(1, id);
                request.setString(2, name);
                request.setString(3, guild.getId());
                request.setInt(4, rights);
                request.executeUpdate();

            } catch (SQLException e) {
                LOG.error(id + " - " + name + " : " + e.getMessage());
            }
        }
    }

    public void setName(String name){
        this.name = name;

        Connexion connexion = Connexion.getInstance();
        Connection connection = connexion.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE User SET name = ? WHERE id_user = ? AND id_guild = ?;");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, id);
            preparedStatement.setString(3, guild.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
    }

    public void changeRight(int right){
        this.rights = right;

        Connexion connexion = Connexion.getInstance();
        Connection connection = connexion.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE User SET rights = ? WHERE id_user = ? AND id_guild = ?;");
            preparedStatement.setInt(1, rights);
            preparedStatement.setString(2, id);
            preparedStatement.setString(3, guild.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
    }

    public static Map<String, Map<String, User>> getUsers(){
        if (users == null){
            users = new HashMap<String, Map<String, User>>();

            String id;
            String name;
            int right;
            Guild guildId;

            Connexion connexion = Connexion.getInstance();
            Connection connection = connexion.getConnection();

            try {
                PreparedStatement query = connection.prepareStatement("SELECT id_user, name, id_guild, rights FROM User");
                ResultSet resultSet = query.executeQuery();

                while (resultSet.next()) {
                    id = resultSet.getString("id_user");
                    name = resultSet.getString("name");
                    right = resultSet.getInt("rights");
                    guildId = Guild.getGuilds().get(resultSet.getString("id_guild"));

                    if (!users.containsKey(guildId.getId()))
                        users.put(guildId.getId(), new HashMap<String, User>());
                    users.get(guildId.getId()).put(id, new User(id, name, right, guildId));
                }
            } catch (SQLException e) {
                LOG.error(e.getMessage());
            }
        }

        return users;
    }

    public int getRights() {
        return rights;
    }

    public Guild getGuild() {
        return guild;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
