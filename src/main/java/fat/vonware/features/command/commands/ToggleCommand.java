package fat.vonware.features.command.commands;

import  fat.vonware.features.command.*;
import  fat.vonware.*;
import  fat.vonware.features.modules.*;

public class ToggleCommand extends Command
{
    public ToggleCommand() {
        super("toggle", new String[] { "<toggle>", "<module>" });
    }

    public void execute(final String[] commands) {
        if (commands.length == 2) {
            final String name = commands[0].replaceAll("_", " ");
            final Module module = Vonware.moduleManager.getModuleByName(name);
            if (module != null) {
                module.toggle();
            }
            else {
                Command.sendMessage("Unable to find a module with that name!");
            }
        }
        else {
            Command.sendMessage("Please provide a valid module name!");
        }
    }
}