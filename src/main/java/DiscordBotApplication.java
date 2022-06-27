import java.util.*;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class DiscordBotApplication {
    public static void main(String[] args) {
        DiscordApi api = new DiscordApiBuilder()
                .setToken(System.getenv("TOKEN"))
                .login()
                .join();

        Server server = new ArrayList<>(api.getServersByName("dcpgdb test")).get(0);

        api.getServerSlashCommands(server)
                .thenAccept(slashCommands -> slashCommands.forEach(slashCommand ->
                        slashCommand.deleteForServer(server)))
                .join();

//
//        SlashCommand ping = SlashCommand.with("ping", "Check the functionality of this command")
//                .createForServer(server)
//                .join();
//
//        SlashCommand notPing = SlashCommand.with("aping", "skodkopakdsopa")
//                .createForServer(server)
//                .join();

        SlashCommand playMusic = SlashCommand.with("play", "plays some music",
                        List.of(SlashCommandOption.createWithOptions(SlashCommandOptionType.STRING, "link", "link to the video")))
                .createForServer(server)
                .join();
        SlashCommand pausePlayback = SlashCommand.with("pause", "stops playing music")
                .createForServer(server)
                .join();
        SlashCommand continuePlayback = SlashCommand.with("resume", "continues playing tracks")
                .createForServer(server)
                .join();
        SlashCommand skipTrack = SlashCommand.with("skip", "skips current track and goes to next")
                .createForServer(server)
                .join();
        SlashCommand repeatPlayback = SlashCommand.with("repeat", "skips current track and goes to next")
                .createForServer(server)
                .join();
        Map<Long, SlashCommandCreateListener> getListener = new HashMap<>(Map.of(playMusic.getId(), new PlayMusicSlashCommandCreateListener(server, api)));
        getListener.put(pausePlayback.getId(), new PausePlaybackSlashCommandCreateListener());
        getListener.put(continuePlayback.getId(), new ContinuePlaybackSlashCommandCreateListener());
        getListener.put(skipTrack.getId(), new SkipTrackSlashCommandCreateListener());

        api.addSlashCommandCreateListener(event -> {
            System.out.println(event.getSlashCommandInteraction().getCommandId());
            //System.out.println(playMusic.getId());
           getListener.get(event.getSlashCommandInteraction().getCommandId()).onSlashCommandCreate(event);
        });
        //playMusic.getId();
        //ServerVoiceChannel voiceChannel = server.getVoiceChannels().stream().findAny().get();



        System.out.println("Bot started");
        System.out.println(api.createBotInvite());
    }
}
