package music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import org.javacord.api.entity.channel.ServerVoiceChannel;

public class MusicPlayer {

    static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    static AudioPlayer currentMusicPlayer;
    static ServerVoiceChannel currentVoiceChannel;
    static TrackScheduler trackScheduler;

}
