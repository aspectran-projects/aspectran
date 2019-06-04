/*
 * Copyright (c) 2008-2019 The Aspectran Project
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
package com.aspectran.shell.activity;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.AdapterException;
import com.aspectran.core.activity.CoreActivity;
import com.aspectran.core.activity.request.ParameterMap;
import com.aspectran.core.context.expr.ItemEvaluator;
import com.aspectran.core.context.expr.ItemExpression;
import com.aspectran.core.context.expr.token.Token;
import com.aspectran.core.context.rule.ItemRule;
import com.aspectran.core.context.rule.ItemRuleList;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.type.ItemType;
import com.aspectran.core.context.rule.type.TokenType;
import com.aspectran.core.util.StringOutputWriter;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;
import com.aspectran.shell.adapter.ShellRequestAdapter;
import com.aspectran.shell.adapter.ShellResponseAdapter;
import com.aspectran.shell.command.ConsoleTerminatedException;
import com.aspectran.shell.console.Console;
import com.aspectran.shell.service.ShellService;

import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Class ShellActivity.
 *
 * @since 2016. 1. 18.
 */
public class ShellActivity extends CoreActivity {

    private static final Log log = LogFactory.getLog(ShellActivity.class);

    private final ShellService service;

    private final Console console;

    private boolean procedural;

    private ParameterMap parameterMap;

    private Writer outputWriter;

    private boolean simpleReading;

    /**
     * Instantiates a new ShellActivity.
     *
     * @param service the {@code ShellService} instance
     * @param console the {@code Console} instance
     */
    public ShellActivity(ShellService service, Console console) {
        super(service.getActivityContext());

        this.service = service;
        this.console = console;
    }

    public void setProcedural(boolean procedural) {
        this.procedural = procedural;
    }

    public void setParameterMap(ParameterMap parameterMap) {
        this.parameterMap = parameterMap;
    }

    public void setOutputWriter(Writer outputWriter) {
        this.outputWriter = outputWriter;
    }

    @Override
    protected void adapt() throws AdapterException {
        try {
            setSessionAdapter(service.newSessionAdapter());

            ShellRequestAdapter requestAdapter = new ShellRequestAdapter();
            if (parameterMap != null) {
                requestAdapter.setParameterMap(parameterMap);
            }
            requestAdapter.setEncoding(console.getEncoding());
            setRequestAdapter(requestAdapter);

            if (outputWriter == null) {
                outputWriter = new StringOutputWriter();
            }

            ShellResponseAdapter responseAdapter = new ShellResponseAdapter(outputWriter);
            responseAdapter.setEncoding(console.getEncoding());
            setResponseAdapter(responseAdapter);

            super.adapt();
        } catch (Exception e) {
            throw new AdapterException("Failed to prepare adapters required for shell service activity", e);
        }
    }

    @Override
    protected void parseRequest() {
        showDescription();

        readParameters();
        parseDeclaredParameters();

        readAttributes();
        parseDeclaredAttributes();
    }

    /**
     * Prints a description for the {@code Translet}.
     */
    private void showDescription() {
        if (service.isVerbose()) {
            String description = getTranslet().getDescription();
            if (description != null) {
                console.writeLine(description);
            }
        }
    }

    private boolean isSimpleItemRules(ItemRuleList itemRuleList) {
        for (ItemRule itemRule : itemRuleList) {
            if (itemRule.getType() != ItemType.SINGLE) {
                return false;
            }
            Token[] tokens = itemRule.getAllTokens();
            if (tokens != null && tokens.length > 0) {
                if (tokens.length == 1) {
                    Token t = tokens[0];
                    if (t.getType() != TokenType.TEXT) {
                        if (t.getType() != TokenType.PARAMETER ||
                                !itemRule.getName().equals(t.getName())) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private void determineSimpleReading(ItemRuleList itemRuleList) {
        simpleReading = isSimpleItemRules(itemRuleList);
    }

    /**
     * Read required input parameters.
     */
    private void readParameters() {
        ItemRuleMap parameterItemRuleMap = getRequestRule().getParameterItemRuleMap();
        if (parameterItemRuleMap != null && !parameterItemRuleMap.isEmpty()) {
            ItemRuleList parameterItemRuleList = new ItemRuleList(parameterItemRuleMap.values());
            determineSimpleReading(parameterItemRuleList);
            if (procedural) {
                console.setStyle("GREEN");
                console.writeLine("Required parameters:");
                console.styleOff();

                if (!simpleReading) {
                    for (ItemRule itemRule : parameterItemRuleList) {
                        Token[] tokens = itemRule.getAllTokens();
                        if (tokens == null) {
                            Token t = new Token(TokenType.PARAMETER, itemRule.getName());
                            tokens = new Token[] { t };
                        }
                        console.setStyle("YELLOW");
                        console.write(getMandatoryMarker(itemRule.isMandatory()));
                        console.styleOff();
                        console.setStyle("bold");
                        console.write(itemRule.getName());
                        console.styleOff();
                        console.write(": ");
                        writeToken(tokens);
                        console.writeLine();
                    }
                }
            }
            readRequiredParameters(parameterItemRuleList);
        }
    }

    private void readRequiredParameters(ItemRuleList parameterItemRuleList) {
        ItemRuleList missingItemRules1;
        if (procedural) {
            if (simpleReading) {
                missingItemRules1 = readEachParameter(parameterItemRuleList);
            } else {
                missingItemRules1 = readEachToken(parameterItemRuleList);
            }
        } else {
            missingItemRules1 = checkRequiredParameters(parameterItemRuleList);
        }
        if (missingItemRules1 != null) {
            console.setStyle("YELLOW");
            console.writeLine("Required parameters are missing.");
            console.styleOff();

            ItemRuleList missingItemRules2;
            if (simpleReading) {
                missingItemRules2 = readEachParameter(missingItemRules1);
            } else {
                missingItemRules2 = readEachToken(missingItemRules1);
            }
            if (missingItemRules2 != null) {
                String[] itemNames = missingItemRules2.getItemNames();
                console.setStyle("RED");
                console.writeLine("Missing required parameters:");
                console.setStyle("bold");
                for (String name : itemNames) {
                    console.writeLine("   %s", name);
                }
                console.styleOff();
                terminate("Required parameters are missing");
            }
        }
    }

    /**
     * Read required input attributes.
     */
    private void readAttributes() {
        ItemRuleMap attributeItemRuleMap = getRequestRule().getAttributeItemRuleMap();
        if (attributeItemRuleMap != null && !attributeItemRuleMap.isEmpty()) {
            ItemRuleList attributeItemRuleList = new ItemRuleList(attributeItemRuleMap.values());
            determineSimpleReading(attributeItemRuleList);
            if (procedural) {
                console.setStyle("GREEN");
                console.writeLine("Required attributes:");
                console.styleOff();

                if (!simpleReading) {
                    for (ItemRule itemRule : attributeItemRuleList) {
                        Token[] tokens = itemRule.getAllTokens();
                        if (tokens == null) {
                            Token t = new Token(TokenType.PARAMETER, itemRule.getName());
                            tokens = new Token[] { t };
                        }
                        console.setStyle("YELLOW");
                        console.write(getMandatoryMarker(itemRule.isMandatory()));
                        console.styleOff();
                        console.setStyle("bold");
                        console.write(itemRule.getName());
                        console.styleOff();
                        console.write(": ");
                        writeToken(tokens);
                        console.writeLine();
                    }
                }
            }
            readRequiredAttributes(attributeItemRuleList);
        }
    }

    private void readRequiredAttributes(ItemRuleList attributeItemRuleList) {
        ItemRuleList missingItemRules1;
        if (procedural) {
            if (simpleReading) {
                missingItemRules1 = readEachAttribute(attributeItemRuleList);
            } else {
                missingItemRules1 = readEachToken(attributeItemRuleList);
            }
        } else {
            missingItemRules1 = checkRequiredAttributes(attributeItemRuleList);
        }
        if (missingItemRules1 != null) {
            console.setStyle("YELLOW");
            console.writeLine("Required attributes are missing.");
            console.styleOff();

            ItemRuleList missingItemRules2;
            if (simpleReading) {
                missingItemRules2 = readEachParameter(missingItemRules1);
            } else {
                missingItemRules2 = readEachToken(missingItemRules1);
            }
            if (missingItemRules2 != null) {
                String[] itemNames = missingItemRules2.getItemNames();
                console.setStyle("RED");
                console.writeLine("Missing required attributes:");
                console.setStyle("bold");
                for (String name : itemNames) {
                    console.writeLine("   %s", name);
                }
                console.styleOff();
                terminate("Required attributes are missing");
            }
        }
    }

    private ItemRuleList readEachParameter(ItemRuleList itemRuleList) {
        Set<ItemRule> missingItemRules = new LinkedHashSet<>();
        try {
            for (ItemRule ir : itemRuleList) {
                String value = readParameter(ir);
                if (StringUtils.hasLength(value)) {
                    getRequestAdapter().setParameter(ir.getName(), value);
                } else if (ir.isMandatory()) {
                    missingItemRules.add(ir);
                }
            }
        } catch (ConsoleTerminatedException e) {
            log.info("User interrupt occurred");
            terminate("User interrupt occurred");
        }
        return (missingItemRules.isEmpty() ? null : new ItemRuleList(missingItemRules));
    }

    private ItemRuleList readEachAttribute(ItemRuleList itemRuleList) {
        Set<ItemRule> missingItemRules = new LinkedHashSet<>();
        try {
            for (ItemRule ir : itemRuleList) {
                String value = readParameter(ir);
                if (StringUtils.hasLength(value)) {
                    getRequestAdapter().setAttribute(ir.getName(), value);
                } else if (ir.isMandatory()) {
                    missingItemRules.add(ir);
                }
            }
        } catch (ConsoleTerminatedException e) {
            log.info("User interrupt occurred");
            terminate("User interrupt occurred");
        }
        return (missingItemRules.isEmpty() ? null : new ItemRuleList(missingItemRules));
    }

    private String readParameter(ItemRule itemRule) {
        console.clearPrompt();
        console.setStyle("YELLOW");
        console.appendPrompt(getMandatoryMarker(itemRule.isMandatory()));
        console.styleOff();
        console.setStyle("bold");
        console.appendPrompt(itemRule.getName());
        console.styleOff();
        console.appendPrompt(": ");

        String defaultValue = null;
        Token[] tokens = itemRule.getAllTokens();
        if (tokens != null && tokens.length == 1) {
            Token token = tokens[0];
            if (token.getType() == TokenType.TEXT) {
                defaultValue = token.getDefaultValue();
            }
        }

        if (itemRule.isSecret()) {
            return console.readPassword(null, defaultValue);
        } else {
            return console.readLine(null, defaultValue);
        }
    }

    private ItemRuleList readEachToken(ItemRuleList itemRuleList) {
        console.setStyle("GREEN");
        console.writeLine("Enter a value for each token:");
        console.styleOff();

        Set<ItemRule> missingItemRules = new LinkedHashSet<>();
        try {
            Map<Token, Set<ItemRule>> inputTokens = new LinkedHashMap<>();
            for (ItemRule itemRule : itemRuleList) {
                Token[] tokens = itemRule.getAllTokens();
                if (tokens == null || tokens.length == 0) {
                    Token t = new Token(TokenType.PARAMETER, itemRule.getName());
                    tokens = new Token[] { t };
                }
                for (Token t1 : tokens) {
                    if (t1.getType() == TokenType.PARAMETER) {
                        boolean exists = false;
                        for (Token t2 : inputTokens.keySet()) {
                            if (t2.equals(t1)) {
                                exists = true;
                                break;
                            }
                        }
                        if (exists) {
                            Set<ItemRule> rules = inputTokens.get(t1);
                            rules.add(itemRule);
                        } else {
                            Set<ItemRule> rules = new LinkedHashSet<>();
                            rules.add(itemRule);
                            inputTokens.put(t1, rules);
                        }
                    }
                }
            }
            for (Map.Entry<Token, Set<ItemRule>> entry : inputTokens.entrySet()) {
                Token token = entry.getKey();
                String value = getRequestAdapter().getParameter(token.getName());
                if (value != null) {
                    console.write("   ");
                    writeToken(token);
                    console.write(": ");
                    console.writeLine(value);
                    continue;
                }
                Set<ItemRule> itemRules = entry.getValue();
                boolean secret = false;
                for (ItemRule ir : itemRules) {
                    if (ir.isSecret()) {
                        secret = true;
                        break;
                    }
                }
                String defaultValue = token.getDefaultValue();
                console.clearPrompt();
                console.appendPrompt("   ");
                console.setStyle("CYAN");
                console.appendPrompt(String.valueOf(Token.PARAMETER_SYMBOL));
                console.appendPrompt(String.valueOf(Token.BRACKET_OPEN));
                console.styleOff();
                console.appendPrompt(token.getName());
                console.setStyle("CYAN");
                console.appendPrompt(String.valueOf(Token.BRACKET_CLOSE));
                console.styleOff();
                console.appendPrompt(": ");
                if (secret) {
                    value = console.readPassword(null, defaultValue);
                } else {
                    value = console.readLine(null, defaultValue);
                }
                if (StringUtils.hasLength(value)) {
                    getRequestAdapter().setParameter(token.getName(), value);
                } else {
                    for (ItemRule ir : itemRules) {
                        if (ir.isMandatory()) {
                            missingItemRules.add(ir);
                        }
                    }
                }
            }
        } catch (ConsoleTerminatedException e) {
            log.info("User interrupt occurred");
            terminate("User interrupt occurred");
        }
        return (missingItemRules.isEmpty() ? null : new ItemRuleList(missingItemRules));
    }

    private String getMandatoryMarker(boolean mandatory) {
        return (mandatory ? " * " : "   ");
    }

    private void writeToken(Token[] tokens) {
        for (Token token : tokens) {
            writeToken(token);
        }
    }

    private void writeToken(Token token) {
        if (token.getType() == TokenType.TEXT) {
            console.write(token.stringify());
        } else {
            String str = token.stringify();
            console.setStyle("CYAN");
            console.write(str.substring(0, 2));
            console.styleOff();
            console.write(str.substring(2, str.length() - 1));
            console.setStyle("CYAN");
            console.write(str.substring(str.length() - 1));
            console.styleOff();
        }
    }

    private ItemRuleList checkRequiredParameters(ItemRuleList itemRuleList) {
        Set<ItemRule> missingItemRules = new LinkedHashSet<>();
        ItemEvaluator evaluator = new ItemExpression(this);
        for (ItemRule itemRule : itemRuleList) {
            String[] values = evaluator.evaluateAsStringArray(itemRule);
            if (values != null && values.length > 0) {
                getRequestAdapter().setParameter(itemRule.getName(), values);
            } else {
                missingItemRules.add(itemRule);
            }
        }
        return (missingItemRules.isEmpty() ? null : new ItemRuleList(missingItemRules));
    }

    private ItemRuleList checkRequiredAttributes(ItemRuleList itemRuleList) {
        Set<ItemRule> missingItemRules = new LinkedHashSet<>();
        ItemEvaluator evaluator = new ItemExpression(this);
        for (ItemRule itemRule : itemRuleList) {
            Object value = evaluator.evaluate(itemRule);
            if (value != null) {
                getRequestAdapter().setAttribute(itemRule.getName(), value);
            } else {
                missingItemRules.add(itemRule);
            }
        }
        return (missingItemRules.isEmpty() ? null : new ItemRuleList(missingItemRules));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Activity> T newActivity() {
        ShellActivity activity = new ShellActivity(service, console);
        activity.setOutputWriter(outputWriter);
        activity.setIncluded(true);
        return (T)activity;
    }

}