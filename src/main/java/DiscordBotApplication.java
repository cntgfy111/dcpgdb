import java.util.*;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import music.*;
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

        SoundCloudAudioSourceManager soundCloudAudioSourceManager = SoundCloudAudioSourceManager.createDefault();
        MusicPlayer.playerManager.registerSourceManager(new YoutubeAudioSourceManager(true));
        MusicPlayer.playerManager.registerSourceManager(soundCloudAudioSourceManager);
        MusicPlayer.currentMusicPlayer = MusicPlayer.playerManager.createPlayer();
        MusicPlayer.trackScheduler = new TrackScheduler(MusicPlayer.currentMusicPlayer);
        MusicPlayer.currentMusicPlayer.addListener(MusicPlayer.trackScheduler);

        api.addSlashCommandCreateListener(event -> {
            System.out.println(event.getSlashCommandInteraction().getCommandId());
            commands.get(event.getSlashCommandInteraction().getCommandId()).onSlashCommandCreate(event);
        });


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
                        List.of(SlashCommandOption.createWithOptions(SlashCommandOptionType.STRING, "query", "link to " +
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
                playMusic.getId(), new PlayMusicSlashCommandCreateListener(server, api),
                pausePlayback.getId(), new PausePlaybackSlashCommandCreateListener(),
                continuePlayback.getId(), new ContinuePlaybackSlashCommandCreateListener(),
                skipTrack.getId(), new SkipTrackSlashCommandCreateListener()
        );
    }
}
