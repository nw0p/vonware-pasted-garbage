package fat.vonware;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import fat.vonware.features.modules.misc.NameHider;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.util.ThreadUtil;

public class DiscordPresence {


    public static final String ID = "1166905811457540166";

    public static final DiscordRichPresence PRESENCE = new DiscordRichPresence();
    public static final DiscordRPC RPC = DiscordRPC.INSTANCE;
    private static Thread presenceThread;


    public static void start() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        PRESENCE.startTimestamp = System.currentTimeMillis() / 1000L;
        handlers.disconnected = ((errorCode, message) -> System.out.println("Discord RPC disconnected, errorCode: " + errorCode + ", message: " + message));
        RPC.Discord_Initialize(ID, handlers, true, null);

        presenceThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    PRESENCE.details = Minecraft.getMinecraft().isIntegratedServerRunning() ? "SinglePlayer" : (Minecraft.getMinecraft().getCurrentServerData() != null ? Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase() : "Main Menu");
                    PRESENCE.state = "Playing as " + (NameHider.INSTANCE.isEnabled() ? NameHider.INSTANCE.newName.getValue() : Minecraft.getMinecraft().getSession().getUsername()  );
                    PRESENCE.largeImageKey = "vonware1";
                    PRESENCE.largeImageText = "Killin the opp's";
                    RPC.Discord_UpdatePresence(PRESENCE);

                    Thread.sleep(3000);
                } catch (Exception ignored) {
                }
            }
        });

        presenceThread.start();
    }

    public static void stop() {
        RPC.Discord_Shutdown();
        RPC.Discord_ClearPresence();
    }
}
