package music;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class PlayMusicSlashCommandCreateListener implements SlashCommandCreateListener {

    Server server;
    DiscordApi api;

    public PlayMusicSlashCommandCreateListener(Server server, DiscordApi api){
        this.server = server;
        this.api = api;
    }

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        if (interaction.getOptionStringValueByIndex(0).isEmpty()){
            interaction.createImmediateResponder()
                    .append("Магнус, ты можешь хоть что-то сказать, тяфкнуть, хрюкнуть")
                    .respond();
        }
        String link = interaction.getOptionStringValueByIndex(0).get();

        if (interaction.getUser().getConnectedVoiceChannel(server).isPresent()) {
            MusicPlayer.currentVoiceChannel = interaction.getUser().getConnectedVoiceChannel(server).get();
        } else {
            interaction.createImmediateResponder()
                    .append("Зайди в войс, еблан")
                    .respond();
        }
        if (MusicPlayer.currentMusicPlayer != null){
            MusicPlayer.playerManager.loadItem(link, new AudioLoadResultHandlerImpl(interaction));
        } else{
            MusicPlayer.currentVoiceChannel.connect().thenAccept(audioConnection -> {
                MusicPlayer.playerManager.registerSourceManager(new YoutubeAudioSourceManager());
                MusicPlayer.currentMusicPlayer = MusicPlayer.playerManager.createPlayer();
                MusicPlayer.trackScheduler = new TrackScheduler(MusicPlayer.currentMusicPlayer);
                MusicPlayer.currentMusicPlayer.addListener(MusicPlayer.trackScheduler);
                LavaPlayerAudioSource source = new LavaPlayerAudioSource(api, MusicPlayer.currentMusicPlayer);
                audioConnection.setAudioSource(source);
                MusicPlayer.playerManager.loadItem(link, new AudioLoadResultHandlerImpl(interaction));
            });
        }

    }
}
