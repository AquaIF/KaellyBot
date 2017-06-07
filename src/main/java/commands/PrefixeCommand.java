package commands;

import data.Constants;
import data.Guild;
import data.User;
import discord.Message;
import exceptions.NotEnoughRightsDiscordException;
import exceptions.PrefixeOutOfBoundsDiscordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

import java.util.regex.Matcher;

/**
 * Created by steve on 14/07/2016.
 */
public class PrefixeCommand extends AbstractCommand{

    private final static Logger LOG = LoggerFactory.getLogger(PrefixeCommand.class);

    public PrefixeCommand(){
        super("prefixe","\\s+(.+)");
        setUsableInMP(false);
    }

    @Override
    public boolean request(IMessage message) {
        if (super.request(message)) {

            User author = User.getUsers().get(message.getGuild().getStringID()).get(message.getAuthor().getStringID());

            if (author.getRights() >= User.RIGHT_ADMIN) {
                Matcher m = getMatcher(message);
                m.find();
                String newPrefixe = m.group(1).trim();

                if (newPrefixe.length() >= 1 && newPrefixe.length() <= Constants.prefixeLimit) {
                    Guild.getGuilds().get(message.getGuild().getStringID()).setPrefixe(newPrefixe);
                    Message.sendText(message.getChannel(), "Changement réussi. Pour invoquer une commande, "
                            + "il faudra désormais utiliser le préfixe *" + newPrefixe + "*.");
                    return true;
                }
                else
                    new PrefixeOutOfBoundsDiscordException().throwException(message, this);
            }
            else
                new NotEnoughRightsDiscordException().throwException(message, this);
        }
        return false;
    }

    @Override
    public String help(String prefixe) {
        return "**" + prefixe + name + "** change le préfixe utilisé pour invoquer une commande. Niveau administrateur uniquement.";
    }

    @Override
    public String helpDetailed(String prefixe) {
        return help(prefixe)
                + "\n`" + prefixe + name + " `*`prefixe`* : change le préfixe par celui passé en paramètre. "
                + Constants.prefixeLimit + " maximum.\n";
    }
}
