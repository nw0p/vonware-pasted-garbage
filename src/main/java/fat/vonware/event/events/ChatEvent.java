package fat.vonware.event.events;

import fat.vonware.event.EventStage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ChatEvent
        extends EventStage {
    private final String msg;

    public ChatEvent(String msg) {
        this.msg = msg;
    }


    public String getMsg() {
        return this.msg;
    }
}

