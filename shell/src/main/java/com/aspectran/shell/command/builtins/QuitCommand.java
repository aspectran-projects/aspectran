package com.aspectran.shell.command.builtins;

import com.aspectran.shell.command.AbstractCommand;
import com.aspectran.shell.command.ConsoleTerminatedException;
import com.aspectran.shell.service.ShellAspectranService;

import java.util.Map;

public class QuitCommand extends AbstractCommand {

    private static final String NAMESPACE = "builtin";

    private static final String COMMAND_NAME = "quit";

    public QuitCommand(ShellAspectranService service) {
        super(service);
    }

    @Override
    public String execute(String[] args) throws Exception {
        throw new ConsoleTerminatedException();
    }

    @Override
    public Descriptor getDescriptor() {
        return new Descriptor() {

            @Override
            public String getNamespace() {
                return NAMESPACE;
            }

            @Override
            public String getName() {
                return COMMAND_NAME;
            }

            @Override
            public String getDescription() {
                return "Releases all resources and Exits";
            }

            @Override
            public String getUsage() {
                return "Type 'quit'";
            }

            @Override
            public Map<String, String> getOptions() {
                return null;
            }

        };
    }

}
