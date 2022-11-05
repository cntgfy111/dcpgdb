package music.slashCommands;

import music.AudioLoadResultHandlerImpl;
import music.LavaPlayerAudioSource;
import music.MusicPlayer;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.Arrays;

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
            return;
        }
        if (interaction.getUser().getConnectedVoiceChannel(server).isEmpty()) {
            System.out.println("NO VOICE CHANNEL");
            interaction.createImmediateResponder()
                    .append("Зайди в войс, дурак")
                    .respond();
            return;
        }
        interaction.respondLater();
        String query = interaction.getOptionStringValueByIndex(0).get();
        if (server.getAudioConnection().isEmpty()){
            System.out.println("Подключаюсь к войсу....");
            //MusicPlayer.setCurrentVoiceChannel(interaction.getUser().getConnectedVoiceChannel(server).get());
            //MusicPlayer.getCurrentVoiceChannel().connect().thenAccept(audioConnection -> {
            MusicPlayer.getTrackScheduler().setChannelForOutput(interaction.getChannel().get());
            interaction.getUser().getConnectedVoiceChannel(server).get().connect().thenAccept(audioConnection -> {
                LavaPlayerAudioSource source = new LavaPlayerAudioSource(api, MusicPlayer.getCurrentAudioPlayer());
                audioConnection.setAudioSource(source);
                if (isUrl(query)) {
                    MusicPlayer.getPlayerManager().loadItem(query, new AudioLoadResultHandlerImpl(interaction, query));
                } else {
                    MusicPlayer.getPlayerManager().loadItem("ytsearch: " + query, new AudioLoadResultHandlerImpl(interaction, query));
                }
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                interaction.createImmediateResponder()
                        .append("Произошла непредвиденная ошибка")
                        .respond();
                return null;
            });
        } else {
            if (isUrl(query)) {
                MusicPlayer.getPlayerManager().loadItem(query, new AudioLoadResultHandlerImpl(interaction, query));
            } else {
                MusicPlayer.getPlayerManager().loadItem("ytsearch: " + query, new AudioLoadResultHandlerImpl(interaction, query));
            }
        }
    }

    private boolean isUrl(String argument){
        return argument.startsWith("https://") || argument.startsWith("http://");
    }
}
