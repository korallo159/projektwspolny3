package jbwm.jbwm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TestKomenda extends JbwmCommand {
    public TestKomenda() {
        super("testcmd", "/test 123", "cmdtest", "ttest");
    }

    Config config = new Config("test");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return utab(args, "a", "b", "c");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1)
            switch(args[0]) {
                // /testcmd a
                case "a":
                    this.config.conf.set("test2", 2);
                    this.config.save();
                    Jbwm.log(1);
                    break;
                // /testcmd b
                case "b":
                    Jbwm.warn(this.config.conf.get("test2", 3));
                    Jbwm.warn(2);
                    break;
                // /testcmd c
                case "c":
                    this.config.reload();
                    Jbwm.error("Ala ma kota", 3);
                    break;
            }
        return args.length >= 1;
    }
}
