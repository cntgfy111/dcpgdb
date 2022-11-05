import java.util.*;

import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import music.*;
import music.slashCommands.ContinuePlaybackSlashCommandCreateListener;
import music.slashCommands.PausePlaybackSlashCommandCreateListener;
import music.slashCommands.PlayMusicSlashCommandCreateListener;
import music.slashCommands.SkipTrackSlashCommandCreateListener;
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

        removeOldSlashCommands(api, server);
        Map<Long, SlashCommandCreateListener> commands = initCommands(api, server);

        SoundCloudAudioSourceManager soundCloudAudioSourceManager = SoundCloudAudioSourceManager.createDefault();
        MusicPlayer.getPlayerManager().registerSourceManager(new YoutubeAudioSourceManager(true));
        MusicPlayer.getPlayerManager().registerSourceManager(soundCloudAudioSourceManager);
        MusicPlayer.setCurrentAudioPlayer(MusicPlayer.getPlayerManager().createPlayer());
        MusicPlayer.setTrackScheduler(new TrackScheduler(MusicPlayer.getCurrentAudioPlayer()));
        MusicPlayer.getCurrentAudioPlayer().addListener(MusicPlayer.getTrackScheduler());
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
        SlashCommand playMusic = SlashCommand.with("play", "Повайбить",
                        List.of(SlashCommandOption.createWithOptions(SlashCommandOptionType.STRING, "запрос", "ссылка или название")))
                .createForServer(server)
                .join();
        SlashCommand pausePlayback = SlashCommand.with("pause", "Остановить текущий вайб")
                .createForServer(server)
                .join();
        SlashCommand continuePlayback = SlashCommand.with("resume", "Продолжить вайб")
                .createForServer(server)
                .join();
        SlashCommand skipTrack = SlashCommand.with("skip", "Использовать когда клементин врубает дабстеп")
                .createForServer(server)
                .join();
        //Я ОБЯЗАТЕЛЬНО СДЕЛАЮ КОМАНДУ РЕПИТ
        /*SlashCommand repeatPlayback = SlashCommand.with("repeat", "Если трек качает, то это команда не подкачает")
                .createForServer(server)
                .join();*/

        return Map.of(
                playMusic.getId(), new PlayMusicSlashCommandCreateListener(server, api),
                pausePlayback.getId(), new PausePlaybackSlashCommandCreateListener(server),
                continuePlayback.getId(), new ContinuePlaybackSlashCommandCreateListener(server),
                skipTrack.getId(), new SkipTrackSlashCommandCreateListener(server)
        );
    }
}
