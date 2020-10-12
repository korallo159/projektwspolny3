package jbwm.jbwm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TestKomenda extends JbwmCommand {
    public TestKomenda() {
        super("testcmd", "/test 123", "cmdtest", "ttest");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1)
            switch(args[0]) {
                case "a":
                    Jbwm.log(1);
                    break;
                    // /testcmd a
                case "b":
                    Jbwm.warn(2);
                    break;
                case "c":
                    Jbwm.error("Ala ma kota", 3);
                    break;
            }
        return args.length >= 1;
    }
}
