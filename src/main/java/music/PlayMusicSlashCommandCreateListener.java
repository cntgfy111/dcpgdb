package music;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.entity.channel.ServerVoiceChannelUpdater;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.channel.server.voice.ServerVoiceChannelMemberLeaveEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.audio.AudioConnectionAttachableListener;
import org.javacord.api.listener.channel.server.voice.ServerVoiceChannelMemberLeaveListener;
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
        interaction.respondLater();
        MusicPlayer.currentTextChannel = interaction.getChannel().get();
        if (interaction.getOptionStringValueByIndex(0).isEmpty()){
            interaction.createImmediateResponder()
                    .append("Магнус, ты можешь хоть что-то сказать, тяфкнуть, хрюкнуть")
                    .respond();
        }

        String query = interaction.getOptionStringValueByIndex(0).get();
        if (!interaction.getUser().getConnectedVoiceChannel(server).isPresent()) {
            interaction.createImmediateResponder()
                    .append("Зайди в войс, еблан")
                    .respond();
        }

        if (server.getAudioConnection().isPresent()){
            if (isUrl(query)) {
                MusicPlayer.playerManager.loadItem(query, new AudioLoadResultHandlerImpl(interaction, query));
            } else {
                MusicPlayer.playerManager.loadItem("ytsearch: " + query, new AudioLoadResultHandlerImpl(interaction, query));
            }
        } else {
            MusicPlayer.currentVoiceChannel = interaction.getUser().getConnectedVoiceChannel(server).get();
            MusicPlayer.currentVoiceChannel.connect().thenAccept(audioConnection -> {
                LavaPlayerAudioSource source = new LavaPlayerAudioSource(api, MusicPlayer.currentMusicPlayer);
                audioConnection.setAudioSource(source);

                if (isUrl(query)) {
                    MusicPlayer.playerManager.loadItem(query, new AudioLoadResultHandlerImpl(interaction, query));
                } else {
                    MusicPlayer.playerManager.loadItem("ytsearch: " + query, new AudioLoadResultHandlerImpl(interaction, query));
                }
            }).exceptionally(throwable -> {System.out.println(Arrays.toString(throwable.getStackTrace()));
                return null;
            });
        }

    }

    private boolean isUrl(String argument){
        return argument.startsWith("https://") || argument.startsWith("http://");
    }
}
