package commands;

import enums.Language;
import util.Message;
import exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;
import util.Translator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by steve on 14/07/2016.
 */
public class AvailableCommand extends AbstractCommand{

    private final static Logger LOG = LoggerFactory.getLogger(AvailableCommand.class);

    public AvailableCommand(){
        super("available","\\s+(\\w+)\\s+(on|off|0|1|true|false)");
        setAdmin(true);
    }

    @Override
    public boolean request(IMessage message) {
        if (super.request(message)) {
            Matcher m = getMatcher(message);
            m.find();
            Language lg = Translator.getLanguageFrom(message.getChannel());
            List<Command> potentialCmds = new ArrayList<>();
            String commandName = m.group(1).trim();
            for (Command command : CommandManager.getCommands())
                if (!command.isAdmin() && command.getName().contains(commandName))
                    potentialCmds.add(command);

            if (potentialCmds.size() == 1){
                Command command = potentialCmds.get(0);
                String value = m.group(2);

                if (command instanceof AvailableCommand || command instanceof AboutCommand){
                    Message.sendText(message.getChannel(), Translator.getLabel(lg, "announce.request.1"));
                    return false;
                }
                if (value.matches("false") || value.matches("1") || value.matches("off")){
                    if (command.isPublic()) {
                        command.setPublic(false);
                        Message.sendText(message.getChannel(), Translator.getLabel(lg, "announce.request.2") + " *" + commandName
                                + "* " + Translator.getLabel(lg, "announce.request.3"));
                    }
                    else
                        new ForbiddenCommandFoundDiscordException().throwException(message, this);
                }
                else if (value.matches("true") || value.matches("0") || value.matches("on")){
                    if (! command.isPublic()) {
                        command.setPublic(true);
                        Message.sendText(message.getChannel(), Translator.getLabel(lg, "announce.request.2") + "*" + commandName
                                + "* " + Translator.getLabel(lg, "announce.request.4"));
                    }
                    else
                        new ForbiddenCommandNotFoundDiscordException().throwException(message, this);
                }
                else
                    new BadUseCommandDiscordException().throwException(message, this);
            }
            else if (potentialCmds.isEmpty())
                new CommandNotFoundDiscordException().throwException(message, this);
            else
                new TooMuchPossibilitiesDiscordException().throwException(message, this);
        }
        return false;
    }

    @Override
    public String help(Language lg, String prefixe) {
        return "**" + prefixe + name + "** " + Translator.getLabel(lg, "available.help");
    }

    @Override
    public String helpDetailed(Language lg, String prefixe) {
        return help(lg, prefixe)
                + "\n" + prefixe + "`"  + name + "*CommandForbidden* true` : " + Translator.getLabel(lg, "available.detailed.1")
                + "\n" + prefixe + "`"  + name + "*CommandForbidden* false` : " + Translator.getLabel(lg, "available.detailed.2") + "\n";
    }
}
