/*
 * Copyright (c) 2008-2020 The Aspectran Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.shell.command;

import com.aspectran.core.util.PBEncryptionUtils;
import com.aspectran.shell.command.builtins.HelpCommand;
import com.aspectran.shell.command.builtins.JettyCommand;
import com.aspectran.shell.command.builtins.PBDecryptCommand;
import com.aspectran.shell.command.builtins.PBEncryptCommand;
import com.aspectran.shell.command.builtins.SysInfoCommand;
import com.aspectran.shell.command.builtins.VerboseCommand;
import com.aspectran.shell.console.Console;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static com.aspectran.core.util.PBEncryptionUtils.ENCRYPTION_PASSWORD_KEY;

/**
 * <p>Created: 2017. 11. 19.</p>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommandTest {

    private final CommandInterpreter interpreter = new TestCommandInterpreter();

    private Console getConsole() {
        return interpreter.getConsole();
    }

    @BeforeAll
    void saveProperties() {
        // System default
        System.setProperty(ENCRYPTION_PASSWORD_KEY, "encryption-password-for-test");
    }

    @Test
    void testVerboseCommand() {
        VerboseCommand command = new VerboseCommand(interpreter.getCommandRegistry());
        getConsole().writeLine(command.getDescriptor().getDescription());
        command.printHelp(getConsole());
    }

    @Test
    void testHelpCommand() {
        HelpCommand command = new HelpCommand(interpreter.getCommandRegistry());
        getConsole().writeLine(command.getDescriptor().getDescription());
        command.printHelp(getConsole());
    }

    @Test
    void testSysInfoCommand() throws Exception {
        SysInfoCommand command = new SysInfoCommand(interpreter.getCommandRegistry());
        //getConsole().writeLine(command.getDescriptor().getDescription());
        command.printHelp(getConsole());
        CommandLineParser lineParser = new CommandLineParser("sysinfo -mem");
        command.execute(lineParser.parseOptions(command.getOptions()), getConsole());
    }

    @Test
    void testPBEncryptCommand() throws Exception {
        PBEncryptCommand command = new PBEncryptCommand(interpreter.getCommandRegistry());
        //getConsole().writeLine(command.getDescriptor().getDescription());
        command.printHelp(getConsole());
        CommandLineParser lineParser = new CommandLineParser("encrypt \"aaa ccc d\" -p=bbb");
        command.execute(lineParser.parseOptions(command.getOptions()), getConsole());
    }

    @Test
    void testPBDecryptCommand() throws Exception {
        String encrypted = PBEncryptionUtils.encrypt("1234", "bbb");

        PBDecryptCommand command = new PBDecryptCommand(interpreter.getCommandRegistry());
        //getConsole().writeLine(command.getDescriptor().getDescription());
        command.printHelp(getConsole());
        CommandLineParser lineParser = new CommandLineParser("decrypt -p=\"bbb\" " + encrypted);
        command.execute(lineParser.parseOptions(command.getOptions()), getConsole());
    }

    @Test
    void testPBEncryptCommand2() throws Exception {
        PBEncryptCommand command = new PBEncryptCommand(interpreter.getCommandRegistry());
        getConsole().writeLine(command.getDescriptor().getDescription());
        command.printHelp(getConsole());
        CommandLineParser lineParser = new CommandLineParser("encrypt input1 input2");
        command.execute(lineParser.parseOptions(command.getOptions()), getConsole());
    }

    @Test
    void testPBDecryptCommand2() throws Exception {
        String encrypted = PBEncryptionUtils.encrypt("1234");

        PBDecryptCommand command = new PBDecryptCommand(interpreter.getCommandRegistry());
        getConsole().writeLine(command.getDescriptor().getDescription());
        CommandLineParser lineParser = new CommandLineParser("decrypt " + encrypted);
        command.execute(lineParser.parseOptions(command.getOptions()), getConsole());
    }

    @Test
    void testTestCommand() throws Exception {
        TestCommand command = new TestCommand(interpreter.getCommandRegistry());
        //getConsole().writeLine(command.getDescriptor().getDescription());
        command.printHelp(getConsole());
        CommandLineParser lineParser = new CommandLineParser("test -i=aaa -D=123 -p=bbb -X -Y -Z");
        command.execute(lineParser.parseOptions(command.getOptions()), getConsole());
    }

    @Test
    void testJettyCommand() {
        JettyCommand command = new JettyCommand(interpreter.getCommandRegistry());
        //getConsole().writeLine(command.getDescriptor().getDescription());
        command.printHelp(getConsole());
    }

}
