package commands;

import data.ClientConfig;
import data.Constants;
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
public class HelpCommand extends AbstractCommand{

    private final static Logger LOG = LoggerFactory.getLogger(HelpCommand.class);

    public HelpCommand(){
        super(Pattern.compile("help"),
                Pattern.compile("^(" + Constants.prefixCommand + "help)(\\s+" + Constants.prefixCommand + "?(\\w+))?$"));
    }

    @Override
    public boolean request(IMessage message) {
        if (super.request(message)) {

            StringBuilder st = new StringBuilder();
            boolean argumentFound = m.group(2) != null && m.group(2).replaceAll("^\\s+", "").length() > 0;
            for(Command command : commands)
                if (! argumentFound)
                    st.append(command.help()).append("\n");
                else if (command.getName().matcher(m.group(2)).find()){
                    st.append(command.helpDetailed());
                    break;
                }

            if (argumentFound && st.length() == 0)
                st.append("Aucune commande ne répond au nom de *")
                        .append(m.group(2).replaceAll("^\\W+", ""))
                        .append("*.");

            // Envoyer le message en privée
                RequestBuffer.request(() -> {
                    try {
                        new MessageBuilder(ClientConfig.CLIENT())
                                .withChannel(message.getChannel())
                                .withContent(st.toString())
                                .build();
                    } catch (DiscordException e) {
                        LOG.error(e.getErrorMessage());
                    } catch (MissingPermissionsException e) {
                        LOG.warn(Constants.name + " n'a pas les permissions pour appliquer cette requête.");
                    }
                    return null;
                });
                return true;
            }
            return false;
        }

    @Override
    public String help() {
        return "**" + Constants.prefixCommand + "help** explique le fonctionnement de chaque commande de " + Constants.name + ".";
    }

    @Override
    public String helpDetailed() {
        return help()
                + "\n`" + Constants.prefixCommand + "help` : explique succintement chaque commande."
                + "\n`" + Constants.prefixCommand + "help `*`command`* : explique de façon détaillée la commande spécifiée.\n";
    }
}
