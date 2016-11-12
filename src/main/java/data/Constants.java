package data;

/**
 * Created by steve on 28/07/2016.
 */
public class Constants {

    /**
     * Application name
     */
    public final static String name = "Kaelly";

    /**
     * Author
     */
    public final static String author = "Kaysoro";

    /**
     * Database name
     */
    public final static String database = "bdd.sqlite";

    /**
     * prefix used for command call.
     * WARN : it is injected into regex expression.
     * If you use special characters as '$', don't forget to prefix it with '\\' like this : "\\$"
     */
    public final static String prefixCommand = "!";

    /**
     * Official Dofus URL for almanax
     */
    public final static String almanaxURL = "http://www.krosmoz.com/fr/almanax/";
}
