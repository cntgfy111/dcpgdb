package music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.hook.AudioOutputHook;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.channel.VoiceChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private final EmbedBuilder embed = new EmbedBuilder();
    private long MessageInfoID = 0;
    private TextChannel channelForOutput;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void setChannelForOutput(TextChannel channelForOutput) {
        this.channelForOutput = channelForOutput;
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        try{
            System.out.println("НАЧАЛО QUEUE");
            if (!player.startTrack(track, true)) {
                System.out.println("track is in queue");
                queue.offer(track);
                return;
            }
            System.out.println("new track is playing");
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        if (queue.isEmpty()){
            channelForOutput.sendMessage("queue закончилось ептить");
        }
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // Adapter dummy method
        System.out.println(exception.getLocalizedMessage());
        System.out.println(exception.getMessage());
        System.out.println(Arrays.toString(exception.getCause().getStackTrace()));
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        channelForOutput.deleteMessages(MessageInfoID);
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // Adapter dummy method
        AudioTrackInfo trackInfo = track.getInfo();
        embed.setTitle(trackInfo.title)
                .setAuthor(trackInfo.author)
                .setUrl(trackInfo.uri)
                .setColor(Color.RED);
        try {
            MessageInfoID = channelForOutput.sendMessage(embed).get().getId();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}