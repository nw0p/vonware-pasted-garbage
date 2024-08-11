package fat.vonware.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import fat.vonware.Vonware;
import fat.vonware.features.command.Command;

public class HelpCommand
        extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("Commands: ");
        for (Command command : Vonware.commandManager.getCommands()) {
            HelpCommand.sendMessage(ChatFormatting.GRAY + Vonware.commandManager.getPrefix() + command.getName());
        }
    }
}

