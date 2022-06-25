import java.io.File;
import java.util.ArrayList;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;

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


        ServerVoiceChannel voiceChannel = server.getVoiceChannels().stream().findAny().get();

        voiceChannel.connect().thenAccept(audioConnection -> {
            DefaultAudioPlayerManager playerManager = new DefaultAudioPlayerManager();
            playerManager.registerSourceManager(new YoutubeAudioSourceManager());
            AudioPlayer player = playerManager.createPlayer();

            LavaPlayerAudioSource source = new LavaPlayerAudioSource(api, player);
            audioConnection.setAudioSource(source);

            playerManager.loadItem("https://www.youtube.com/watch?v=6gm3Nqy3eoQ", new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    player.playTrack(track);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    playlist.getTracks().forEach(player::playTrack);
                }

                @Override
                public void noMatches() {
                    System.out.println("Прости, брат, нихуя не нашлось");

                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    System.out.println("Бляздец");
                }
            });
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });

        System.out.println("Bot started");
        System.out.println(api.createBotInvite());
    }
}
