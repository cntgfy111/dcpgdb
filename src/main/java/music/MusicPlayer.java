package music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;

public class MusicPlayer {

    public static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    public static AudioPlayer currentMusicPlayer;
    public static ServerVoiceChannel currentVoiceChannel;
    public static TextChannel currentTextChannel;
    public static TrackScheduler trackScheduler;

}
