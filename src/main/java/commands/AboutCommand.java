package commands;

import data.Constants;
import enums.Donator;
import enums.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import util.ClientConfig;
import util.Message;
import sx.blah.discord.handle.obj.IMessage;
import util.Translator;

import java.util.Random;

/**
 * Created by Songfu on 29/05/2017.
 */
public class AboutCommand extends AbstractCommand{

    private final static Logger LOG = LoggerFactory.getLogger(AbstractCommand.class);

    public AboutCommand() {
        super("about", "");
    }

    @Override
    public boolean request(IMessage message) {
        if (super.request(message)){
            IUser author = ClientConfig.DISCORD().getApplicationOwner();
            EmbedBuilder builder = new EmbedBuilder();
            Language lg = Translator.getLanguageFrom(message.getChannel());

            builder.withTitle(Translator.getLabel(lg, "about.title")
                        .replace("{name}", Constants.name)
                        .replace("{version}", Constants.version))
                    .withDesc(Translator.getLabel(lg, "about.desc")
                        .replace("{game}", Constants.game))
                    .withColor(new Random().nextInt(16777216))
                    .withThumbnail(ClientConfig.DISCORD().getApplicationIconURL())
                    .withAuthorName(author.getName())
                    .withAuthorIcon(author.getAvatarURL());

            builder.appendField(Translator.getLabel(lg, "about.invite.title"),
                    Translator.getLabel(lg, "about.invite.desc")
                        .replace("{name}", Constants.name)
                        .replace("{invite}", Constants.invite), true)
            .appendField(Translator.getLabel(lg, "about.support.title"),
                    Translator.getLabel(lg, "about.support.desc")
                        .replace("{name}", Constants.name)
                        .replace("{discordInvite}", Constants.discordInvite), true)
            .appendField(Translator.getLabel(lg, "about.twitter.title"),
                    Translator.getLabel(lg, "about.twitter.desc")
                        .replace("{name}", Constants.name)
                        .replace("{twitter}", Constants.twitterAccount), true)
            .appendField(Translator.getLabel(lg, "about.opensource.title"),
                    Translator.getLabel(lg, "about.opensource.desc")
                        .replace("{git}", Constants.git), true)
            .appendField(Translator.getLabel(lg, "about.free.title"),
                    Translator.getLabel(lg, "about.free.desc")
                        .replace("{paypal}", Constants.paypal), true);

            try {
                IChannel news = ClientConfig.DISCORD().getChannelByID(Constants.newsChan);
                builder.appendField(Translator.getLabel(lg, "about.changelog.title"),
                        news.getFullMessageHistory().getLatestMessage().getContent(), true);
            } catch(Exception e) {LOG.error("AboutCommand.request", e);}

            StringBuilder st = new StringBuilder();
            for(Donator donator : Donator.values())
                st.append(donator.getName()).append(", ");
            st.setLength(st.length() - 2);
            builder.appendField(Translator.getLabel(lg, "about.donators.title"), st.toString() + ".", true);

            Message.sendEmbed(message.getChannel(), builder.build());
            return true;
        }

        return false;
    }

    @Override
    public String help(Language lg, String prefixe) {
        return "**" + prefixe + name + "** " + Translator.getLabel(lg, "about.help")
                .replace("{name}", Constants.name);
    }

    @Override
    public String helpDetailed(Language lg, String prefixe) {
        return help(lg, prefixe);
    }
}
