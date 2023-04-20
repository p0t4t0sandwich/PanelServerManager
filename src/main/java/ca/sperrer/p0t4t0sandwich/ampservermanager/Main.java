package ca.sperrer.p0t4t0sandwich.ampservermanager;

import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class Main {
    public static void main (String [] args) throws IOException {
        Console console = System.console();

        // Check if console is null and if the system is headless
        if (console == null && !GraphicsEnvironment.isHeadless()) {
            String filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar \"" + filename + "\""});

        // Normal execution
        } else {
            // Logger
            Logger logger = Logger.getLogger("ampservermanager");
            logger.setLevel(Level.FINE);

            // Log formatter
            logger.setUseParentHandlers(false);
            ConsoleHandler handler = new ConsoleHandler();
            handler.setFormatter(new SimpleFormatter() {
                private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format(format,
                            new Date(lr.getMillis()),
                            lr.getLevel().getLocalizedName(),
                            lr.getMessage()
                    );
                }
            });
            logger.addHandler(handler);

            // Start AMPAPAI Server Manager
            AMPServerManager ampServerManager = new AMPServerManager("./", logger);
            ampServerManager.init();

            // Start terminal application
            while(true){
                String[] input = console.readLine().split(" ");

                if (input[0].equals("exit")) {
                    break;
                } else if (input[0].equals("help")) {
                    logger.info("Available commands:");
                    logger.info("help - Show this message");
                    logger.info("exit - Exit the application");
//                    logger.info("list - List all servers");

                    logger.info("start <server> - Start a server");
                    logger.info("stop <server> - Stop a server");
                    logger.info("restart <server> - Restart a server");
                    logger.info("kill <server> - Kill a server");
                    logger.info("sleep <server> - Put a server to sleep");
                    logger.info("send <server> <command> - Send a command to a server");
                    logger.info("status <server> - Get the status of a server");

                } else {
                    String response = ampServerManager.commandMessenger(input).replaceAll("§.", "");
                    if (response.equals("")) {
                        response = "Use 'help' to see a list of commands.";
                    }

                    for (String s : response.split("\n")) {
                        logger.info(s);
                    }
                }
            }
        }
    }
}