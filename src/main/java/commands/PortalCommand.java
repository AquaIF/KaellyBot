package commands;

import data.Guild;
import data.Portal;
import data.Position;
import enums.Language;
import util.Message;
import exceptions.PortalNotFoundDiscordException;
import exceptions.TooMuchPossibilitiesDiscordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by steve on 14/07/2016.
 */
public class PortalCommand extends AbstractCommand{

    private final static Logger LOG = LoggerFactory.getLogger(PortalCommand.class);

    public PortalCommand(){
        super("pos", "(\\s+\\p{L}+)?(\\s+\\[?(-?\\d{1,2})\\s*[,|\\s]\\s*(-?\\d{1,2})\\]?)?(\\s+\\d{1,3})?");
        setUsableInMP(false);
    }

    @Override
    public boolean request(IMessage message) {
        if (super.request(message)) {
            Matcher m = getMatcher(message);
            m.find();
            if (m.group(1) == null && m.group(5) == null) { // No dimension precised
                for(Portal pos : Guild.getGuild(message.getGuild()).getPortals())
                        Message.sendEmbed(message.getChannel(), pos.getEmbedObject());
            }
            else {
                List<Portal> portals = new ArrayList<>();
                if (m.group(1) != null)
                    portals = getPortal(m.group(1), Guild.getGuild(message.getGuild()));
                if (portals.size() == 1) {
                    if (m.group(2) != null)
                        portals.get(0).setCoordonate(Position.parse("[" + m.group(3) + "," + m.group(4) + "]"));
                    if (m.group(5) != null)
                        portals.get(0).setUtilisation(Integer.parseInt(m.group(5).replaceAll("\\s", "")));

                    Message.sendEmbed(message.getChannel(), portals.get(0).getEmbedObject());
                }
                else if(portals.size() > 1)
                    new TooMuchPossibilitiesDiscordException().throwException(message, this);
                else
                    new PortalNotFoundDiscordException().throwException(message, this);
            }
        }
        return false;
    }

    public List<Portal> getPortal(String nameProposed, Guild guild){
        nameProposed = Normalizer.normalize(nameProposed, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
        nameProposed = nameProposed.replaceAll("\\W+", "");
        List<Portal> portals = new ArrayList<>();

        for(Portal portal : guild.getPortals())
            if (Normalizer.normalize(portal.getName(), Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .toLowerCase().startsWith(nameProposed))
                    portals.add(portal);
        return portals;
    }

    @Override
    public String help(Language lg, String prefixe) {
        return "**" + prefixe + name + "** donne la position des portails dimension.";
    }

    @Override
    public String helpDetailed(Language lg, String prefixe) {
        return help(lg, prefixe)
                + "\n" + prefixe + "`"  + name + "` : donne la position de tous les portails."
                + "\n" + prefixe + "`"  + name + " `*`dimension`* : donne la position du portail de la dimension désirée."
                + "\n" + prefixe + "`"  + name + " `*`dimension`*` [POS, POS]` : met à jour la position du portail de la dimension spécifiée."
                + "\n" + prefixe + "`"  + name + " `*`dimension`*` [POS, POS] `*`nombre d'uti.`* : met à jour la position et le nombre d'utilisations"
                + " de la dimension spécifiée. 0 en tant que nombre d'utilisation réinitialise le portail de la dimension spécifiée."
                + "\n" + prefixe + "`"  + name + " `*`dimension` `nombre d'uti.`* : met à jour le nombre d'utilisation.\n";
    }
}
