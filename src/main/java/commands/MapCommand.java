package commands;

import data.Constants;
import discord.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by steve on 14/07/2016.
 */
public class MapCommand extends AbstractCommand{

    private final static Logger LOG = LoggerFactory.getLogger(MapCommand.class);
    private final static String[] urls = new String[]{
            "https://image.noelshack.com/fichiers/2017/14/1491433376-i.png"
            , "https://image.noelshack.com/fichiers/2017/14/1491433376-ii.png"
            , "https://image.noelshack.com/fichiers/2017/14/1491433375-iii.png"
            , "https://image.noelshack.com/fichiers/2017/14/1491433376-iv.png"
            , "https://image.noelshack.com/fichiers/2017/14/1491433376-v.png"
            , "https://image.noelshack.com/fichiers/2017/14/1491433377-vi.png"
            , "https://image.noelshack.com/fichiers/2017/14/1491433378-vii.png"
            , "https://image.noelshack.com/fichiers/2017/14/1491433378-viii.png"
            , "https://image.noelshack.com/fichiers/2017/14/1491433376-ix.png"
            , "https://image.noelshack.com/fichiers/2017/14/1491433379-x.png"
            , "https://image.noelshack.com/fichiers/2017/14/1491433381-xi.png"
            , "https://image.noelshack.com/fichiers/2017/14/1491433381-xii.png"
    };

    public MapCommand(){
        super(Pattern.compile("map"),
              Pattern.compile("^(" + Constants.prefixCommand + "map)"
        + "((\\s+I|\\s+II|\\s+III|\\s+IV|\\s+V|\\s+VI|\\s+VII|\\s+VIII|\\s+IX|\\s+X|\\s+XI|\\s+XII"
              + "|\\s+1|\\s+2|\\s+3|\\s+4|\\s+5|\\s+6|\\s+7|\\s+8|\\s+9|\\s+10|\\s+11|\\s+12"
              + ")+)?$"));
    }

    @Override
    public boolean request(IMessage message) {
        if (super.request(message)) {
            String[] maps;
            if (m.group(2) == null)
                maps = new String[]{"I", "II", "III", "IV", "V", "VI",
                        "VII", "VIII", "IX", "X", "XI", "XII"};
            else {
                String text = m.group(2).trim()
                        .replaceAll("1", "I").replaceAll("2", "II")
                        .replaceAll("3", "III").replaceAll("4", "IV")
                        .replaceAll("5", "V").replaceAll("6", "VI")
                        .replaceAll("7", "VII").replaceAll("8", "VIII")
                        .replaceAll("9", "IX").replaceAll("10", "X")
                        .replaceAll("11", "XI").replaceAll("12", "XII");

                maps = text.split("\\s+");
            }

            int random = new Random().nextInt(maps.length);
            Message.send(message.getChannel(), "Le combat aura lieu sur la carte "
                    + maps[random] + " !"
                    + "\n" + urls[random]);
        }
        return false;
    }

    @Override
    public boolean isUsableInMP() {
        return true;
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    @Override
    public String help() {
        return "**" + Constants.prefixCommand + "map** tire au hasard une carte du Goultarminator ou bien parmi celles"
                + " spécifiées en paramètre.";
    }

    @Override
    public String helpDetailed() {
        return help()
                + "\n`" + Constants.prefixCommand + "map` : tire une carte du Goultarminator entre I et XII compris."
                + "\n`" + Constants.prefixCommand + "map `*`map1 map2 ...`* : tire une carte parmi celles spécifiées en paramètre. Nombres romains ou numériques uniquement.\n";
    }
}
