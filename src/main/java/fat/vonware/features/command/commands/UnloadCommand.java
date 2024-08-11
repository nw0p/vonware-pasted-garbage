package fat.vonware.features.command.commands;

import fat.vonware.Vonware;
import fat.vonware.features.command.Command;

public class UnloadCommand
        extends Command {
    public UnloadCommand() {
        super("unload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        Vonware.unload(true);
    }
}

