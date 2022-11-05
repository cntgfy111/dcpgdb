package music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;

public class MusicPlayer {

    private static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private static AudioPlayer currentAudioPlayer;
    private static TrackScheduler trackScheduler;

    public static AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public static AudioPlayer getCurrentAudioPlayer() {
        return currentAudioPlayer;
    }

    public static void setCurrentAudioPlayer(AudioPlayer currentAudioPlayer) {
        MusicPlayer.currentAudioPlayer = currentAudioPlayer;
    }

    public static TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    public static void setTrackScheduler(TrackScheduler trackScheduler) {
        MusicPlayer.trackScheduler = trackScheduler;
    }
}
