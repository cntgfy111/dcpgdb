package music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.interaction.SlashCommandInteraction;

public class AudioLoadResultHandlerImpl implements AudioLoadResultHandler {

    private final SlashCommandInteraction interaction;

    AudioLoadResultHandlerImpl(SlashCommandInteraction interaction){
        this.interaction = interaction;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        System.out.println("new track added");
        MusicPlayer.trackScheduler.queue(track);


        interaction.createImmediateResponder()
                .append("Добавил трек")
                .respond();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        for (AudioTrack track : playlist.getTracks()) {
            System.out.println("new track added");
            MusicPlayer.trackScheduler.queue(track);
        }

        interaction.createImmediateResponder()
                .append("Сейчас играет афигенский трек: " + playlist.getSelectedTrack().getInfo())
                .respond();
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


}
