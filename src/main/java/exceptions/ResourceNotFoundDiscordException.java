package exceptions;

import commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;
import util.Message;

/**
 * Created by steve on 14/11/2016.
 */
public class ResourceNotFoundDiscordException implements DiscordException {

    private final static Logger LOG = LoggerFactory.getLogger(ResourceNotFoundDiscordException.class);

    @Override
    public void throwException(IMessage message, Command command, Object... arguments) {
        Message.sendText(message.getChannel(), "Aucune ressource trouvée, recommencez en étant plus précis.");
    }
}
