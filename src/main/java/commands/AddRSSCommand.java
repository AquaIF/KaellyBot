package commands;

import data.ClientConfig;
import data.Constants;
import data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.util.regex.Pattern;

/**
 * Created by steve on 14/07/2016.
 */
public class AddRSSCommand extends AbstractCommand{

    private final static Logger LOG = LoggerFactory.getLogger(AddRSSCommand.class);

    public AddRSSCommand(){
        super(Pattern.compile("rss"),
        Pattern.compile("^(!rss)\\W+[-add](http[s?]://[.*].xml)$"));
        //TODO name ?
    }

    @Override
    public boolean request(IMessage message) {
        if (super.request(message)) {

            //On check si la personne a bien les droits pour exécuter cette commande
            if (User.getUsers().get(message.getGuild().getID())
                    .get(message.getAuthor().getID()).getRights() >= User.RIGHT_MODERATOR) {
                // TODO Do command
            } else {
                RequestBuffer.request(() -> {
                    try {
                        new MessageBuilder(ClientConfig.CLIENT())
                                .withChannel(message.getChannel())
                                .withContent("Vous n'avez pas les droits suffisants pour ajouter un flux RSS;"
                                        + " demandez à un modérateur ou à l'administrateur de "
                                        + message.getGuild().getName() + " de modifier vos droits.")
                                .build();
                    } catch (DiscordException e) {
                        LOG.error(e.getErrorMessage());
                    } catch (MissingPermissionsException e) {
                        LOG.warn(Constants.name + " n'a pas les permissions pour appliquer cette requête.");
                    }
                    return null;
                });
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String help() {
        //TODO
        return null;
    }

    @Override
    public String helpDetailed() {
        //TODO
        return null;
    }
}
