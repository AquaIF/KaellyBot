package data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 12/11/2016.
 */
public class Job {

    private final static Logger LOG = LoggerFactory.getLogger(Job.class);
    private static List<String> jobs;
    private String name;
    private int level;
    private User user;

    public Job(String name, int level, User user){
        this.name = name;
        this.level = level;
        this.user = user;
    }

    public void setLevel(int level){
        this.level = level;

        Connexion connexion = Connexion.getInstance();
        Connection connection = connexion.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE Job_User SET level = ?"
                            + "WHERE name_job = ? AND id_user = ?;");
            preparedStatement.setInt(1, level);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, user.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
    }

    public void addToDatabase(){
        if (! user.getJobs().containsKey(name)) {
            user.getJobs().put(name, this);
            Connexion connexion = Connexion.getInstance();
            Connection connection = connexion.getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO Job_User(name_job, id_user, level) VALUES(?, ?, ?);");
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, user.getId());
                preparedStatement.setInt(3, level);

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    public static Map<String, Job> getJobs(User user){
        Map<String, Job> jobs = new HashMap<String, Job>();

        Connexion connexion = Connexion.getInstance();
        Connection connection = connexion.getConnection();

        String name;
        int level;

        try {
            PreparedStatement query = connection.prepareStatement("SELECT name, level"
                    + " FROM Job_User WHERE id_user = ?;");
            query.setString(1, user.getId());

            ResultSet resultSet = query.executeQuery();

            while (resultSet.next()) {
                name = resultSet.getString("name");
                level = resultSet.getInt("level");
                jobs.put(name, new Job(name, level, user));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }

        return jobs;
    }

    public static List<String> getJobs(){
        if (jobs == null){
            jobs = new ArrayList<String>();

            Connexion connexion = Connexion.getInstance();
            Connection connection = connexion.getConnection();

            try {
                PreparedStatement query = connection.prepareStatement("SELECT name FROM Job");
                ResultSet resultSet = query.executeQuery();

                while (resultSet.next())
                    jobs.add(resultSet.getString("name"));
            } catch (SQLException e) {
                LOG.error(e.getMessage());
            }
        }
        return jobs;
    }
}
