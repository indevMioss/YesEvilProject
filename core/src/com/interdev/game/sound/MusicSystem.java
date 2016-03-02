package com.interdev.game.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicSystem implements Disposable {
    public final static float DEFAULT_VOLUME = 0.1f;
    public final static float LOW_VOLUME = 0.05f;

    private int currentTrackIndex;
    private List<Music> musicList = new ArrayList<Music>();
    private List<String> licenseList = new ArrayList<String>();
    private TrackChangeListener trackChangeListener;
    private Random random = new Random();

    public MusicSystem() {
        Music track1 = Gdx.audio.newMusic(Gdx.files.internal("music/shadows_of_the_mind.mp3"));
        Music track2 = Gdx.audio.newMusic(Gdx.files.internal("music/dance_of_the_pixies.mp3"));
        Music track3 = Gdx.audio.newMusic(Gdx.files.internal("music/remember_the_dreams.mp3"));

        musicList.add(track1);
        licenseList.add("Music used:\n" +
                        "Shadows of the Mind by Per Kiilstofte\n" +
                        "Licensed under Creative Commons Attribution 4.0 International\n" +
                        "https://machinimasound.com/music/shadows-of-the-mind"
        );

        musicList.add(track2);
        licenseList.add("Music used:\n" +
                        "Dance of the Pixies by Jens Kiilstofte\n" +
                        "Licensed under Creative Commons Attribution 4.0 International\n" +
                        "https://machinimasound.com/music/dance-of-the-pixies"

        );

        musicList.add(track3);
        licenseList.add("Music used:\n" +
                        "Remember the Dreams by Per Kiilstofte\n" +
                        "Licensed under Creative Commons Attribution 4.0 International\n" +
                        "https://machinimasound.com/music/remember-the-dreams"
        );

        for (int i = 0; i < musicList.size(); i++) {
            final int nextTrackIndex = (i == musicList.size() - 1) ? 0 : i + 1;
            musicList.get(i).setOnCompletionListener(new Music.OnCompletionListener() {
                @Override
                public void onCompletion(Music music) {
                    currentTrackIndex = nextTrackIndex;
                    musicList.get(nextTrackIndex).play();
                    onTrackChanged();
                }
            });
        }

        setVolume(DEFAULT_VOLUME);
    }

    public void setTrackChangeListener(TrackChangeListener trackChangeListener) {
        this.trackChangeListener = trackChangeListener;
    }

    public void play() {
        currentTrackIndex = random.nextInt(musicList.size());
        System.out.println("currentTrackIndex" + currentTrackIndex);
        musicList.get(currentTrackIndex).play();
        onTrackChanged();
    }

    public void stop() {
        musicList.get(currentTrackIndex).stop();
    }

    public void setVolume(float volume) {
        for (Music music : musicList) {
            music.setVolume(volume);
        }
    }

    private void onTrackChanged() {
        if (trackChangeListener != null) {
            trackChangeListener.trackChanged(licenseList.get(currentTrackIndex));
        }
    }

    @Override
    public void dispose() {
        for (Music music : musicList) {
            music.dispose();
        }
    }

}
