package fat.vonware.features.modules.client;

import fat.vonware.DiscordPresence;
import fat.vonware.features.command.Command;
import fat.vonware.features.modules.Module;

public class RPC extends Module {
    public static RPC INSTANCE;

    public RPC() {
        super("DiscordRPC", "Ostrich loves cp", Category.CLIENT, false, false, false);
        this.enabled.setValue(false);
        this.drawn.setValue(false);
        INSTANCE = this;
    }


    @Override
    public void onEnable() {
        DiscordPresence.start();
        Command.sendSilentMessage("DiscordRPC Started");
    }

    @Override
    public void onDisable() {
        DiscordPresence.stop();
        Command.sendSilentMessage("DiscordRPC Stopped");
    }
}

