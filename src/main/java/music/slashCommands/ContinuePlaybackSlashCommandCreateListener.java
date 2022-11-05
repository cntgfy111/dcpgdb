package music.slashCommands;

import music.MusicPlayer;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class ContinuePlaybackSlashCommandCreateListener implements SlashCommandCreateListener {

    Server server;

    public ContinuePlaybackSlashCommandCreateListener(Server server) {
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
        if (MusicPlayer.getCurrentAudioPlayer().getPlayingTrack() == null){
            interaction.createImmediateResponder()
                    .append("В очереди ничего нет")
                    .respond();
            return;
        }
        if (MusicPlayer.getCurrentAudioPlayer().isPaused()){
            MusicPlayer.getCurrentAudioPlayer().setPaused(false);
            interaction.createImmediateResponder()
                    .append("Воспроизведение продолжается")
                    .respond();

        } else {
            interaction.createImmediateResponder()
                    .append("Музыка итак играет")
                    .respond();
        }
    }
}
