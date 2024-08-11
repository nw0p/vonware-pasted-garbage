package fat.vonware.features.modules.misc;

import fat.vonware.features.modules.*;
import fat.vonware.features.setting.*;
import fat.vonware.*;
import fat.vonware.util.Timer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import fat.vonware.util.*;
import java.util.*;

public class Spammer extends Module
{
    private static final String fileName = "Vonware/Spammer/Spammer.txt";
    private static final String defaultMessage = "Welcome to use abHack";
    private static final List<String> spamMessages;
    private static final Random rnd;
    private final Setting<Boolean> escoff;
    private final Timer timer;
    public Setting<Mode> mode;
    public Setting<Double> delay;
    public Setting<String> custom;
    public Setting<String> msgTarget;
    public Setting<Boolean> greentext;
    public Setting<Boolean> random;
    public Setting<Boolean> loadFile;

    public Spammer() {
        super("Spammer", "Spams stuff.", Category.MISC, true, false, false);
        this.escoff = (Setting<Boolean>)this.register(new Setting("EscOff", true));
        this.timer = new Timer();
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", Mode.FILE));
        this.delay = (Setting<Double>)this.register(new Setting("Delay", 5.0, 0.0, 20.0));
        this.custom = (Setting<String>)this.register(new Setting("Custom", "String", v -> this.mode.getValue() == Mode.MSG));
        this.msgTarget = (Setting<String>)this.register(new Setting("MsgTarget", "Target...", v -> this.mode.getValue() == Mode.MSG));
        this.greentext = (Setting<Boolean>)this.register(new Setting("Greentext", Boolean.FALSE, v -> this.mode.getValue() == Mode.FILE));
        this.random = (Setting<Boolean>)this.register(new Setting("Random", Boolean.FALSE, v -> this.mode.getValue() == Mode.FILE));
        this.loadFile = (Setting<Boolean>)this.register(new Setting("LoadFile", Boolean.FALSE, v -> this.mode.getValue() == Mode.FILE));
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            this.disable();
            return;
        }
        this.readSpamFile();
    }

    @Override
    public void onLogin() {
        if (this.escoff.getValue() && Vonware.moduleManager.isModuleEnabled("Spammer")) {
            this.disable();
        }
    }

    @Override
    public void onLogout() {
        if (this.escoff.getValue() && Vonware.moduleManager.isModuleEnabled("Spammer")) {
            this.disable();
        }
    }

    @Override
    public void onDisable() {
        Spammer.spamMessages.clear();
        this.timer.reset();
    }

    @Override
    public void onTick() {
        if (fullNullCheck()) {
            this.disable();
            return;
        }
        if (this.loadFile.getValue()) {
            this.readSpamFile();
            this.loadFile.setValue(false);
        }
        if (this.timer.passedS(this.delay.getValue())) {
            if (this.mode.getValue() == Mode.MSG) {
                String msg = this.custom.getValue();
                msg = "/msg " + this.msgTarget.getValue() + " " + msg;
                Spammer.mc.player.sendChatMessage(msg);
            }
            else if (Spammer.spamMessages.size() > 0) {
                String messageOut;
                if (this.random.getValue()) {
                    final int index = Spammer.rnd.nextInt(Spammer.spamMessages.size());
                    messageOut = Spammer.spamMessages.get(index);
                    Spammer.spamMessages.remove(index);
                }
                else {
                    messageOut = Spammer.spamMessages.get(0);
                    Spammer.spamMessages.remove(0);
                }
                Spammer.spamMessages.add(messageOut);
                if (this.greentext.getValue()) {
                    messageOut = "> " + messageOut;
                }
                Spammer.mc.player.connection.sendPacket((Packet)new CPacketChatMessage(messageOut.replaceAll("�", "")));
            }
            this.timer.reset();
        }
    }

    private void readSpamFile() {
        final List<String> fileInput = FileUtil.readTextFileAllLines("Vonware/Spammer/Spammer.txt");
        final Iterator<String> i = fileInput.iterator();
        Spammer.spamMessages.clear();
        while (i.hasNext()) {
            final String s = i.next();
            if (s.replaceAll("\\s", "").isEmpty()) {
                continue;
            }
            Spammer.spamMessages.add(s);
        }
        if (Spammer.spamMessages.size() == 0) {
            Spammer.spamMessages.add("pk on top!");
        }
    }

    static {
        spamMessages = new ArrayList<String>();
        rnd = new Random();
    }

    public enum Mode
    {
        FILE,
        MSG;
    }
}
