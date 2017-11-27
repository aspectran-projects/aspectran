/*
 * Copyright (c) 2008-2017 The Aspectran Project
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

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * <p>Created: 2017. 3. 5.</p>
 */
public class CommandLineParserTest {

    @Test
    public void testCommandLineParser() {
        CommandLineParser parser = CommandLineParser.parseCommandLine("GET /path/work1 >> abcde.txt > 12345.txt");
        Assert.assertEquals(parser.getRequestMethod().toString(), "GET");
        Assert.assertEquals(parser.getCommand(), "/path/work1");
        Assert.assertEquals(parser.getRedirectionList().toString(), "[{operator=>>, operand=abcde.txt}, {operator=>, operand=12345.txt}]");
        //System.out.println(parser.getRequestMethod());
        //System.out.println(parser.getCommand());
        //System.out.println(parser.getRedirectionList());
    }

    @Test
    public void testRedirectionOperators() {
        List<CommandLineRedirection> list = CommandLineParser.parseCommandLine(">> abcde > 12345").getRedirectionList();
        Assert.assertEquals(list.get(0).getOperator(), CommandLineRedirection.Operator.APPEND_OUT);
        Assert.assertEquals(list.get(0).getOperand(), "abcde");
        Assert.assertEquals(list.get(1).getOperator(), CommandLineRedirection.Operator.OVERWRITE_OUT);
        Assert.assertEquals(list.get(1).getOperand(), "12345");
    }

    @Test
    public void testRedirectionOperators2() {
        List<CommandLineRedirection> list = CommandLineParser.parseCommandLine("> '<abcde>' >> 12345").getRedirectionList();
        Assert.assertEquals(list.get(0).getOperator(), CommandLineRedirection.Operator.OVERWRITE_OUT);
        Assert.assertEquals(list.get(0).getOperand(), "'<abcde>'");
        Assert.assertEquals(list.get(1).getOperator(), CommandLineRedirection.Operator.APPEND_OUT);
        Assert.assertEquals(list.get(1).getOperand(), "12345");
    }

}