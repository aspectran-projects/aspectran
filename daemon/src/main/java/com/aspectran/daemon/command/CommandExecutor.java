/*
 * Copyright (c) 2008-2021 The Aspectran Project
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
package com.aspectran.daemon.command;

import com.aspectran.core.util.ExceptionUtils;
import com.aspectran.core.util.logging.Logger;
import com.aspectran.core.util.logging.LoggerFactory;
import com.aspectran.daemon.Daemon;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(CommandExecutor.class);

    private final Daemon daemon;

    private final ExecutorService executorService;

    private final BlockingQueue<Runnable> workQueue;

    private final AtomicInteger queueSize = new AtomicInteger();

    private final AtomicBoolean isolated = new AtomicBoolean();

    public CommandExecutor(Daemon daemon, int maxThreads) {
        if (daemon == null) {
            throw new IllegalArgumentException("daemon must not be null");
        }

        this.daemon = daemon;
        this.workQueue = new SynchronousQueue<>();
        this.executorService = new ThreadPoolExecutor(
                1,
                maxThreads,
                180L,
                TimeUnit.SECONDS,
                this.workQueue
        );
    }

    public boolean execute(final CommandParameters parameters, final Callback callback) {
        final String commandName = parameters.getCommandName();

        if (isolated.get()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Holds '" + commandName + "' command until the end of the command " +
                        "requiring a single execution guarantee.");
            }
            return false;
        }

        Command command = daemon.getCommandRegistry().getCommand(commandName);
        if (command == null) {
            parameters.setResult("No command mapped to '" + commandName + "'");
            try {
                callback.failure();
            } catch (Exception e) {
                logger.error("Failed to execute callback", e);
            }
            return false;
        }

        if (command.isIsolated() && queueSize.get() > 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("'" + commandName + "' command requires a single execution guarantee, " +
                        "so it is held until another command completes");
            }
            return false;
        }

        Runnable runnable = () -> {
            Thread currentThread = Thread.currentThread();
            String oldThreadName = currentThread.getName();
            try {
                String threadName = "cmd-" + commandName + "-" + queueSize;
                currentThread.setName(threadName);

                if (command.isIsolated()) {
                    isolated.set(true);
                }

                boolean success = execute(command, parameters);
                try {
                    if (success) {
                        callback.success();
                    } else {
                        callback.failure();
                    }
                } catch (Exception e) {
                    logger.error("Failed to execute callback", e);
                }
            } finally {
                currentThread.setName(oldThreadName);
                isolated.compareAndSet(true, false);
                queueSize.decrementAndGet();
            }
        };

        queueSize.incrementAndGet();
        try {
            executorService.execute(runnable);
            return true;
        } catch (RejectedExecutionException e) {
            logger.error("Failed to execute command", e);
            queueSize.decrementAndGet();
            return false;
        }
    }

    private boolean execute(Command command, CommandParameters parameters) {
        try {
            CommandResult commandResult = command.execute(parameters);
            if (commandResult.isSuccess()) {
                parameters.setResult(commandResult.getResult());
                return true;
            } else {
                parameters.setResult("[FAILED] " + commandResult.getResult());
                return false;
            }
        } catch (Exception e) {
            logger.error("Error executing daemon command " + command, e);
            parameters.setResult("[FAILED] Error executing daemon command " + command +
                    System.lineSeparator() + ExceptionUtils.getStacktrace(e));
            return false;
        }
    }

    public int getQueueSize() {
        return queueSize.get();
    }

    public void shutdown() {
        if (logger.isDebugEnabled()) {
            logger.debug("Shutting down executor...");
        }

        executorService.shutdown();
        if (!executorService.isTerminated()) {
            while (true) {
                logger.info("Waiting for executor to terminate...");
                if (executorService.isTerminated()) {
                    break;
                }
                try {
                    if (executorService.awaitTermination(3000L, TimeUnit.MILLISECONDS)) {
                        break;
                    }
                } catch (InterruptedException ignored) {
                    break;
                }
            }
        }
    }

    public interface Callback {

        void success();

        void failure();

    }

}
