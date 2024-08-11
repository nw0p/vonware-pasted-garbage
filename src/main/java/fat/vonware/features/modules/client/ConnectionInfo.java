package fat.vonware.features.modules.client;

import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;

public class ConnectionInfo extends Module {
    public Setting<Boolean> ServerIP = this.register(new Setting<>("ServerIP", true));
    public ConnectionInfo() {
        super("ConnectionInfo", "Shows you what server you're connecting to", Module.Category.CLIENT, true, false, false);
        this.setInstance();
           }
    private static ConnectionInfo INSTANCE = new ConnectionInfo();
    public static ConnectionInfo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionInfo();
        }
        return INSTANCE;
    }
    private void setInstance() {
        INSTANCE = this;
    }
}
