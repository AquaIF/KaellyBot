package commands;

import data.Almanax;
import data.Constants;
import discord.Message;
import exceptions.AlmanaxNotFoundException;
import exceptions.IncorrectDateFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by steve on 14/07/2016.
 */
public class AlmanaxCommand extends AbstractCommand{

    private final static Logger LOG = LoggerFactory.getLogger(AlmanaxCommand.class);
    private final static DateFormat discordToBot = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
    private final static DateFormat botToAlmanax = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);

    public AlmanaxCommand(){
        super(Pattern.compile("almanax"),
                Pattern.compile("^(" + Constants.prefixCommand + "almanax)(\\s+-[b|o])?(\\s+\\d{2}/\\d{2}/\\d{4})?$"));
    }

    @Override
    public boolean request(IMessage message) {
        if (super.request(message)) {

            Date date = new Date();

            if (m.group(3) != null){
                try {
                    date = discordToBot.parse(m.group(3));
                } catch (ParseException e) {
                    new IncorrectDateFormatException().throwException(message, this);
                    return false;
                }
            }

            Almanax almanax = Almanax.get(botToAlmanax.format(date));

            if (almanax != null) {
                StringBuilder st = new StringBuilder("**Almanax du ")
                        .append(discordToBot.format(date))
                        .append(" :**\n");

                if (m.group(2) == null || m.group(2).matches("\\W+-b"))
                    st.append(almanax.getBonus()).append("\n");
                if (m.group(2) == null || m.group(2).matches("\\W+-o"))
                    st.append(almanax.getOffrande()).append("\n");

                Message.sendText(message.getChannel(), st.toString());
                return false;
            }
            else {
                new AlmanaxNotFoundException().throwException(message, this);
                return false;
            }
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
        return "**" + Constants.prefixCommand + "almanax** donne le bonus et l'offrande d'une date particulière.";
    }

    @Override
    public String helpDetailed() {
        return help()
                + "\n`" + Constants.prefixCommand + "almanax` : donne le bonus et l'offrande du jour actuel."
                + "\n`" + Constants.prefixCommand + "almanax -b` : donne uniquement le bonus du jour actuel."
                + "\n`" + Constants.prefixCommand + "almanax -o` : donne uniquement l'offrande du jour actuel."
                + "\n`" + Constants.prefixCommand + "almanax `*`jj/mm/aaaa`* : donne le bonus et l'offrande du jour spécifié."
                + "\n`" + Constants.prefixCommand + "almanax -b `*`jj/mm/aaaa`* : donne uniquement le bonus du jour spécifié."
                + "\n`" + Constants.prefixCommand + "almanax -o `*`jj/mm/aaaa`* : donne uniquement l'offrande du jour spécifié.\n";
    }
}
