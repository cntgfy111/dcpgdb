package music.slashCommands;

import music.MusicPlayer;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class PausePlaybackSlashCommandCreateListener implements SlashCommandCreateListener {

    Server server;

    public PausePlaybackSlashCommandCreateListener(Server server) {
        this.server = server;
    }

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();

        if (interaction.getUser().getConnectedVoiceChannel(server).isEmpty()){
            interaction.createImmediateResponder()
                    .append("Бот не воспроизводит музыку сейчас")
                    .respond();
            return;
        }

        if (!interaction.getUser().getConnectedVoiceChannel(server).get().isConnected(interaction.getUser())){
            interaction.createImmediateResponder()
                    .append("Зайди в войс, еблан")
                    .respond();
            return;
        }
        if (MusicPlayer.getCurrentAudioPlayer().isPaused()){
            interaction.createImmediateResponder()
                    .append("Воспроизведение итак на паузе")
                    .respond();
            return;
        }
        if (MusicPlayer.getCurrentAudioPlayer().getPlayingTrack() == null){
            interaction.createImmediateResponder()
                    .append("В очереди ничего нет")
                    .respond();
            return;
        }
        MusicPlayer.getCurrentAudioPlayer().setPaused(true);
        interaction.createImmediateResponder()
                .append("Остановлено воспроизведение трека")
                .respond();
    }
}
