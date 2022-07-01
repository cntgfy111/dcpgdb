import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class PlayMusicSlashCommandCreateListener implements SlashCommandCreateListener {

    Server server;
    DiscordApi api;

    PlayMusicSlashCommandCreateListener(Server server, DiscordApi api){
        this.server = server;
        this.api = api;
    }

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        String link = interaction.getOptionStringValueByIndex(0).get();

            ServerVoiceChannel voiceChannel = null;
            if (interaction.getUser().getConnectedVoiceChannel(server).isPresent()) {
                voiceChannel = interaction.getUser().getConnectedVoiceChannel(server).get();
            } else {
                interaction.createImmediateResponder()
                        .append("Зайди в войс, еблан")
                        .respond();
            }

            voiceChannel.connect().thenAccept(audioConnection -> {
                DefaultAudioPlayerManager playerManager = new DefaultAudioPlayerManager();
                playerManager.registerSourceManager(new YoutubeAudioSourceManager());
                AudioPlayer player = playerManager.createPlayer();

                LavaPlayerAudioSource source = new LavaPlayerAudioSource(api, player);
                audioConnection.setAudioSource(source);

                playerManager.loadItem(link, new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {

                        player.playTrack(track);
                        interaction.createImmediateResponder()
                                .append("Сейчас играет афигенский трек: " + link)
                                .respond();
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        playlist.getTracks().forEach(player::playTrack);
                    }

                    @Override
                    public void noMatches() {
                        System.out.println("Прости, брат, нихуя не нашлось");
                        interaction.createImmediateResponder()
                                .append("Прости, брат, нихуя не нашлось")
                                .respond();
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

    }
}
