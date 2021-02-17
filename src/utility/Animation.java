/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import java.awt.Image;

/**
 *
 * @author 2014130020
 */
public class Animation {

    private Image[] frames;
    private boolean repeat;
    private int tick;
    private int position;
    private int delay;
    private int tickDelay;
    private boolean ticksIgnored;

    public void setAnimation(Image[] frames, boolean repeat) {
        if (frames.length > 1) {
            this.frames = frames;
            this.repeat = repeat;

            tick = 0;
            position = 0;
            delay = 10;
            tickDelay = 0;
            ticksIgnored = false;
        }
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void updateAnimation() {
        if (!ticksIgnored) {
            if (tickDelay == 0) {
                tick++;
                position = tick % frames.length;

                if ((position == frames.length - 1) && (!repeat)) {
                    ticksIgnored = true;
                }

                tickDelay = delay;
            } else {
                tickDelay--;
            }
        }
    }

    public void resetAnimation() {
        position = 0;
    }

    public void stop() {
        ticksIgnored = true;
    }

    public void resume() {
        ticksIgnored = false;
    }

    public Image getCurrentImage() {
        return frames[position];
    }

    public int getPosition() {
        return position;
    }
}
