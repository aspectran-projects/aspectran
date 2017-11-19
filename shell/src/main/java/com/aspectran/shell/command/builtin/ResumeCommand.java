package com.aspectran.shell.command.builtin;

import com.aspectran.shell.command.AbstractCommand;
import com.aspectran.shell.command.CommandRegistry;
import com.aspectran.shell.command.option.Option;

import java.util.Collection;

public class ResumeCommand extends AbstractCommand {

    private static final String NAMESPACE = "builtin";

    private static final String COMMAND_NAME = "resume";

    private CommandDescriptor descriptor = new CommandDescriptor();

    public ResumeCommand(CommandRegistry registry) {
        super(registry);
    }

    @Override
    public String execute(String[] args) throws Exception {
        getService().resume();
        return null;
    }

    @Override
    public Descriptor getDescriptor() {
        return descriptor;
    }

    private class CommandDescriptor implements Descriptor {

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
            return "Resume the Aspectran Shell Service";
        }

        @Override
        public String getUsage() {
            return null;
        }

        @Override
        public Collection<Option> getOptions() {
            return null;
        }

    }

}
