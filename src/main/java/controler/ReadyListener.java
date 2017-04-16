package controler;

import commands.HelpCommand;
import data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by steve on 14/07/2016.
 */
public class ReadyListener {
    private final static Logger LOG = LoggerFactory.getLogger(ReadyListener.class);

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        LOG.info(Constants.name + "Bot connecté !");

        LOG.info("Check des guildes");
        for(IGuild guild : ClientConfig.DISCORD().getGuilds()) {
            new Guild(guild.getStringID(), guild.getName()).addToDatabase();
            if (! guild.getName().equals(Guild.getGuilds().get(guild.getStringID()).getName()))
                Guild.getGuilds().get(guild.getStringID()).setName(guild.getName());
        }

        LOG.info("Check des utilisateurs");
        for(IGuild guild : ClientConfig.DISCORD().getGuilds())
            for(IUser user : guild.getUsers()){
                    int level;
                    if (user.getStringID().equals(guild.getOwnerID()))
                        level = User.RIGHT_ADMIN;
                    else
                        level = User.RIGHT_INVITE;

                    new User(user.getStringID(), user.getDisplayName(guild), level, Guild.getGuilds().get(guild.getStringID()))
                            .addToDatabase();
                if (! user.getDisplayName(guild).equals(User.getUsers().get(guild.getStringID()).get(user.getStringID()).getName()))
                    User.getUsers().get(guild.getStringID()).get(user.getStringID()).setName(user.getDisplayName(guild));
                }

        LOG.info("Ajout des différents listeners");
        ClientConfig.DISCORD().getDispatcher().registerListener(new GuildCreateListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new GuildLeaveListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new GuildTransferOwnershipListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new GuildUpdateListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new NickNameChangeListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new UserBanListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new UserJoinListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new UserLeaveListener());

        ClientConfig.DISCORD().getDispatcher().registerListener(new TrackStartListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new TrackQueueListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new TrackFinishListener());

        // Joue à...
        ClientConfig.DISCORD().changePlayingText(Constants.prefixCommand + new HelpCommand().getName().pattern());

        LOG.info("Ecoute des flux RSS du site Dofus...");
        RSSFinder.start();

        LOG.info("Connexion à l'API Twitter...");
        TwitterFinder.getTwitterChannels();

        LOG.info("Ecoute des messages...");
        ClientConfig.DISCORD().getDispatcher().registerListener(new MessageListener());
    }
}
