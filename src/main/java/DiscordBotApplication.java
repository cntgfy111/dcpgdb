import java.util.*;
import java.util.concurrent.CompletableFuture;

import music.ContinuePlaybackSlashCommandCreateListener;
import music.PausePlaybackSlashCommandCreateListener;
import music.PlayMusicSlashCommandCreateListener;
import music.SkipTrackSlashCommandCreateListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import util.Result;

public class DiscordBotApplication {
    public static void main(String[] args) {
        DiscordApi api = new DiscordApiBuilder()
                .setToken(System.getenv("TOKEN"))
                .login()
                .join();

        Server server = new ArrayList<>(api.getServersByName("dcpgdb test")).get(0);

        removeOldSlashCommands(api, server);
        Map<Long, SlashCommandCreateListener> commands = initCommands(api, server);


        api.addSlashCommandCreateListener(event -> {
            System.out.println(event.getSlashCommandInteraction().getCommandId());
            //System.out.println(playMusic.getId());
            commands.get(event.getSlashCommandInteraction().getCommandId()).onSlashCommandCreate(event);
        });
        //playMusic.getId();
        //ServerVoiceChannel voiceChannel = server.getVoiceChannels().stream().findAny().get();


        System.out.println("Bot started");
        System.out.println(api.createBotInvite());
    }

    private static void removeOldSlashCommands(DiscordApi api, Server server) {
        api.getServerSlashCommands(server)
                .thenAccept(slashCommands -> slashCommands.forEach(slashCommand -> slashCommand.deleteForServer(server)))
                .join();
    }

    private static Map<Long, SlashCommandCreateListener> initCommands(DiscordApi api, Server server) {
        SlashCommand playMusic = SlashCommand.with("play", "plays some music",
                        List.of(SlashCommandOption.createWithOptions(SlashCommandOptionType.STRING, "link", "link to " +
                                "the video")))
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

        return Map.of(
                pausePlayback.getId(), new PausePlaybackSlashCommandCreateListener(),
                continuePlayback.getId(), new ContinuePlaybackSlashCommandCreateListener(),
                skipTrack.getId(), new SkipTrackSlashCommandCreateListener()
        );
    }
}
