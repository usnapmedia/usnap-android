package com.samsao.snapzi.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by vlegault on 15-03-25.
 */
public class VideoUtil {

    private final static String LOG_TAG = PhotoUtil.class.getSimpleName();

    public static boolean getSubVideo(String sourceVideoPath, String destVideoPath, double startTimeInSeconds, double endTimeInSeconds) {

        // Verify that end time is after start time
        if (startTimeInSeconds > endTimeInSeconds) {
            Log.e(LOG_TAG, "Video cropping end time happens before start time.");
            return false;
        }

        IsoFile isoFile = null;
        try {
            isoFile = new IsoFile(sourceVideoPath);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Source file not found.");
            e.printStackTrace();
            return false;
        }

        // Verify that cropping start time and end time are not out of range
        double videoLengthInSeconds = (double)
                isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
        if (startTimeInSeconds >= videoLengthInSeconds) {
            Log.e(LOG_TAG, "Video cropping start time is out of range.");
            return false;
        }

        // Adjust end time to be in inside video scope
        if (endTimeInSeconds > videoLengthInSeconds) {
            endTimeInSeconds = videoLengthInSeconds;
        }

        Movie movie = null;
        try {
            movie = MovieCreator.build(sourceVideoPath);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Unable to build movie from source video path.");
            e.printStackTrace();
            return false;
        }

        List<Track> tracks = movie.getTracks();
        movie.setTracks(new LinkedList<Track>());


        boolean timeCorrected = false;

        // Here we try to find a track that has sync samples. Since we can only start decoding
        // at such a sample we SHOULD make sure that the start of the new fragment is exactly
        // such a frame
        for (Track track : tracks) {
            if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                if (timeCorrected) {
                    // This exception here could be a false positive in case we have multiple tracks
                    // with sync samples at exactly the same positions. E.g. a single movie containing
                    // multiple qualities of the same video (Microsoft Smooth Streaming file)

                    throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
                }
                startTimeInSeconds = correctTimeToSyncSample(track, startTimeInSeconds, false);
                endTimeInSeconds = correctTimeToSyncSample(track, endTimeInSeconds, true);
                timeCorrected = true;
            }
        }

        for (Track track : tracks) {
            long currentSample = 0;
            double currentTime = 0;
            double lastTime = -1;
            long startSample1 = -1;
            long endSample1 = -1;

            for (int i = 0; i < track.getSampleDurations().length; i++) {
                long delta = track.getSampleDurations()[i];


                if (currentTime > lastTime && currentTime <= startTimeInSeconds) {
                    // current sample is still before the new starttime
                    startSample1 = currentSample;
                }
                if (currentTime > lastTime && currentTime <= endTimeInSeconds) {
                    // current sample is after the new start time and still before the new endtime
                    endSample1 = currentSample;
                }
                lastTime = currentTime;
                currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
                currentSample++;
            }
            try {
                movie.addTrack(new AppendTrack(new CroppedTrack(track, startSample1, endSample1)));
            } catch (IOException e) {
                Log.e(LOG_TAG, "Unable to add track to movie.");
                e.printStackTrace();
                return false;
            }
        }

        Container out = new DefaultMp4Builder().build(movie);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(String.format(destVideoPath, startTimeInSeconds, endTimeInSeconds));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileChannel fc = fos.getChannel();

        try {
            out.writeContainer(fc);
            fc.close();
            fos.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Unable to write file.");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];

            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                // samples always start with 1 but we start with zero therefore +1
                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
            }
            currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
            currentSample++;

        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }

    /**
     * Get video width
     *
     * @param videoPath
     * @return video width in pixel
     */
    public static int getVideoWidth(String videoPath) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(videoPath);
        String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        metaRetriever.release();
        return Integer.valueOf(width);
    }

    /**
     * Get video height
     *
     * @param videoPath
     * @return video height in pixel
     */
    public static int getVideoHeight(String videoPath) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(videoPath);
        String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        metaRetriever.release();
        return Integer.valueOf(height);
    }

    /**
     * Tells if the provided video is portrait oriented
     *
     * @param videoPath
     * @return true if video is portrait oriented
     */
    public static boolean isVideoPortraitOriented(String videoPath) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(videoPath);
        String rotation = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        metaRetriever.release();

        if (rotation.equals("0") || rotation.equals("180")) {
            return false;
        } else {
            return true;
        }
    }
}
