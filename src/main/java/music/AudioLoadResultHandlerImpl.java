package music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.interaction.SlashCommandInteraction;

public class AudioLoadResultHandlerImpl implements AudioLoadResultHandler {

    private final SlashCommandInteraction interaction;
    private final String link;

    AudioLoadResultHandlerImpl(SlashCommandInteraction interaction, String link){
        this.interaction = interaction;
        this.link = link;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        System.out.println("new track added");
        MusicPlayer.trackScheduler.queue(track);
        interaction.createFollowupMessageBuilder()
                .setContent("добавил новый трек")
                .send();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (playlist.isSearchResult()){
            System.out.println("find by query: " + playlist.getTracks().get(0).getInfo().title);
            MusicPlayer.trackScheduler.queue(playlist.getTracks().get(0));
            interaction.createFollowupMessageBuilder()
                    .setContent("добавил новый трек по запросу из ютуба")
                    .send();
        } else {
            for (AudioTrack track : playlist.getTracks()) {
                System.out.println("new track added");
                MusicPlayer.trackScheduler.queue(track);
            }
            interaction.createFollowupMessageBuilder()
                    .setContent("добавил треки из плейлиста")
                    .send();
        }
    }

    @Override
    public void noMatches() {
        System.out.println("Прости, брат, нихуя не нашлось");
        interaction.createFollowupMessageBuilder()
                .setContent("Прости, брат, нифига не нашлось")
                .send();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        System.out.println("Бляздец");
        System.out.println(exception.getMessage());
        System.out.println(exception.getCause());
        interaction.createFollowupMessageBuilder()
                .setContent("Блинпец")
                .send();
    }


}
