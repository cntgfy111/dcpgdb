import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class PausePlaybackSlashCommandCreateListener implements SlashCommandCreateListener {



    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();

        if (MusicPlayer.currentVoiceChannel == null){
            interaction.createImmediateResponder()
                    .append("Бот не воспроизводит музыку сейчас")
                    .respond();
        }

        if (!MusicPlayer.currentVoiceChannel.isConnected(interaction.getUser())){
            interaction.createImmediateResponder()
                    .append("Зайди в войс, еблан")
                    .respond();
        }
        if (MusicPlayer.currentMusicPlayer.isPaused()){
            interaction.createImmediateResponder()
                    .append("Воспроизведение итак на паузе")
                    .respond();
        }
        if (MusicPlayer.currentMusicPlayer.getPlayingTrack() == null){
            interaction.createImmediateResponder()
                    .append("В очереди ничего нет")
                    .respond();
        }
        MusicPlayer.currentMusicPlayer.setPaused(true);
        interaction.createImmediateResponder()
                .append("Остановлено воспроизведение трека")
                .respond();
    }
}
