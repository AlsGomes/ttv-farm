package br.com.agdev;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import lombok.extern.log4j.Log4j2;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class MainHunter implements NativeKeyListener {

    private static final AtomicBoolean run = new AtomicBoolean(true);
    private static final AtomicInteger timesClicked = new AtomicInteger(0);
    private static Runnable hunt;

    public static void main(String[] args) {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            log.error("There was a problem registering the native hook", ex);
            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new MainHunter());
        describeActions();
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_ENTER) {
            startBot();
        }

        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            stopBot();
        }
    }

    private static void describeActions() {
        log.info("Press Enter to start");
        log.info("Press ESC to stop");
    }

    private static void startBot() {
        if (hunt != null) {
            return;
        }

        hunt = MainHunter::clickOnPoints;
        Thread thread = new Thread(hunt);

        log.info("Bot is starting...");
        thread.start();
    }

    private static void stopBot() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ex) {
            log.error("Unable to unregister native hook", ex);
            System.exit(1);
        }

        log.info("Bot is stopping...");
        System.exit(0);
    }

    private static void clickOnPoints() {
        while (run.get()) {
            try {
                for (Region screen : getScreens()) {
                    List<Match> matches = screen.findAllList(GeneralInfo.RESOURCES.resolve("enabled-points.png").toString());
                    for (Match match : matches) {
                        match.click();
                        timesClicked.incrementAndGet();
                        log.info("Clicked on point! [{}] times clicked", timesClicked.get());
                        TimeUnit.SECONDS.sleep(GeneralInfo.INTERVAL_BETWEEN_CLICKS);
                    }

                    if (!matches.isEmpty()) {
                        moveMouseToCenter();
                    }
                }
            } catch (FindFailed e) {
                log.debug("Point not found!");
            } catch (InterruptedException e) {
                log.error("Thread unexpectedly interrupted!", e);
                run.set(false);
            } finally {
                try {
//                    log.debug("Waiting for the next check...");
                    TimeUnit.SECONDS.sleep(GeneralInfo.FREQUENCY);
                } catch (InterruptedException e) {
                    log.error("Thread unexpectedly interrupted!", e);
                    run.set(false);
                }
            }
        }

        stopBot();
    }

    private static List<Region> getScreens() throws FindFailed {
        List<Region> regions = new ArrayList<>();

        for (int i = 0; i < Screen.getNumberScreens(); i++) {
            regions.add(Screen.getScreen(i));
        }

        return regions;
    }

    private static void moveMouseToCenter() {
        Region.create(
                Region.MID_HORIZONTAL,
                Region.MID_VERTICAL,
                getRandomLittleXY(),
                getRandomLittleXY(),
                Screen.getScreen(GeneralInfo.NOTEBOOK))
            .mouseMove();
    }

    private static int getRandomLittleXY() {
        return new Random().nextInt(151);
    }
}
