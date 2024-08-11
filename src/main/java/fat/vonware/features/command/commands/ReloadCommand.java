package fat.vonware.features.command.commands;

import fat.vonware.Vonware;
import fat.vonware.features.command.Command;

public class ReloadCommand
        extends Command {
    public ReloadCommand() {
        super("reload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        Vonware.reload();
    }
}

