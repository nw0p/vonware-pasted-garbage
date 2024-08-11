package fat.vonware.util.Trails;


import net.minecraft.util.math.*;

public class Animation
{
    private double start;
    private double end;
    private double speed;
    private double current;
    private double last;
    private double progress;
    private boolean backwards;
    private boolean reverseOnEnd;
    private boolean playing;
    private AnimationMode mode;

    public Animation(final double start, final double end, final double speed, final boolean backwards, final AnimationMode mode) {
        this.progress = 0.0;
        this.start = start;
        this.end = end;
        this.speed = speed;
        this.backwards = backwards;
        this.current = start;
        this.last = start;
        this.mode = mode;
        this.playing = true;
    }


    public Animation(final double start, final double end, final double speed, final boolean backwards, final boolean reverseOnEnd, final AnimationMode mode) {
        this.progress = 0.0;
        this.start = start;
        this.end = end;
        this.speed = speed;
        this.backwards = backwards;
        this.reverseOnEnd = reverseOnEnd;
        this.current = start;
        this.last = start;
        this.mode = mode;
        this.playing = true;
    }

    public void add(final float partialTicks) {
        if (this.playing) {
            this.last = this.current;
            if (this.mode == AnimationMode.LINEAR) {
                this.current += (this.backwards ? -1 : 1) * this.speed;
            }
            else if (this.mode == AnimationMode.EXPONENTIAL) {
                for (int i = 0; i < 1.0f / partialTicks; ++i) {
                    this.current += this.speed;
                    this.speed *= this.speed;
                    if (this.speed > 0.0 && this.backwards) {
                        this.speed *= -1.0;
                    }
                }
            }
            this.current = MathHelper.clamp(this.current, this.start, this.end);
            if (this.current >= this.end) {
                if (this.reverseOnEnd) {
                    this.backwards = !this.backwards;
                }
                else {
                    this.playing = false;
                }
            }
        }
    }

    public double getStart() {
        return this.start;
    }

    public void setStart(final double start) {
        this.start = start;
    }

    public double getEnd() {
        return this.end;
    }

    public void setEnd(final double end) {
        this.end = end;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(final double speed) {
        this.speed = speed;
    }

    public double getCurrent() {
        return this.current;
    }

    public void setCurrent(final double current) {
        this.current = current;
    }

    public double getCurrent(final float partialTicks) {
        return this.playing ? (this.last + (this.current - this.last) * partialTicks) : this.current;
    }

    public AnimationMode getMode() {
        return this.mode;
    }

    public void setMode(final AnimationMode mode) {
        this.mode = mode;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public void play() {
        this.playing = true;
    }

    public void stop() {
        this.playing = false;
    }

    public boolean isBackwards() {
        return this.backwards;
    }

    public void setBackwards(final boolean backwards) {
        this.backwards = backwards;
    }

    public boolean isReverseOnEnd() {
        return this.reverseOnEnd;
    }

    public void setReverseOnEnd(final boolean reverseOnEnd) {
        this.reverseOnEnd = reverseOnEnd;
    }

    public double getProgress() {
        return this.progress;
    }


    public void setProgress(final double progress) {
        this.progress = progress;
    }
}
