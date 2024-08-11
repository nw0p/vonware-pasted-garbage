package fat.vonware;

import fat.vonware.features.modules.client.RPC;
import fat.vonware.manager.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid = "vonware", name = "Vonware", version = "0.1")
public class Vonware {
    public static final String MODID = "vonware";
    public static final String MODNAME = "Vonware";
    public static final String MODVER = "v3.9.0";
    public static final Logger LOGGER = LogManager.getLogger("Vonware ");
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static InteractionManager interactionManager;
    public static PacketManager packetManager;
    public static ColorManager colorManager;
    public static HoleManager holeManager;
    public static InventoryManager inventoryManager;
    public static PotionManager potionManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static SpeedManager speedManager;
    public static ReloadManager reloadManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static ServerManager serverManager;
    public static EventManager eventManager;
    public static TextManager textManager;
    public static TimerManager timerManager;

    @Mod.Instance
    public static Vonware INSTANCE;
    private static boolean unloaded;

    static {
        unloaded = false;
    }

    public static void load() {
        LOGGER.info("\n\nLoading Vonware");
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }
        textManager = new TextManager();
        interactionManager = new InteractionManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        rotationManager = new RotationManager();
        packetManager = new PacketManager();
        eventManager = new EventManager();
        speedManager = new SpeedManager();
        potionManager = new PotionManager();
        inventoryManager = new InventoryManager();
        serverManager = new ServerManager();
        fileManager = new FileManager();
        colorManager = new ColorManager();
        positionManager = new PositionManager();
        configManager = new ConfigManager();
        holeManager = new HoleManager();
        timerManager = new TimerManager();
        LOGGER.info("Managers loaded.");
        moduleManager.init();
        LOGGER.info("Modules loaded.");
        configManager.init();
        potionManager.init();
        eventManager.init();
        LOGGER.info("EventManager loaded.");
        moduleManager.onLoad();
        if (RPC.INSTANCE.isEnabled()) {
            LOGGER.info("RPC for my kitten has been successfully fixed on load!");
            DiscordPresence.start();
        }
        LOGGER.info("Vonware successfully loaded!\n");
        Display.setTitle(MODNAME + " " + MODVER);
    }

    public static void unload(boolean unload) {
        LOGGER.info("\n\nUnloading Vonware");
        if (unload) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
        Vonware.onUnload();
        eventManager = null;
        friendManager = null;
        speedManager = null;
        holeManager = null;
        positionManager = null;
        rotationManager = null;
        configManager = null;
        commandManager = null;
        colorManager = null;
        serverManager = null;
        interactionManager = null;
        fileManager = null;
        potionManager = null;
        inventoryManager = null;
        moduleManager = null;
        textManager = null;
        LOGGER.info("Vonware unloaded!\n");
    }

    public static void reload() {
        Vonware.unload(false);
        Vonware.load();
    }

    public static void onUnload() {
        if (!unloaded) {
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig(Vonware.configManager.config.replaceFirst("vonware/", ""));
            moduleManager.onUnloadPost();
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Vonware.load();
    }
}

