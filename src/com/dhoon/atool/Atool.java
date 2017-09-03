package com.dhoon.atool;

import com.android.internal.os.BaseCommand;

import com.dhoon.atool.pm.Pm;

import java.io.PrintStream;
import java.util.Arrays;

public final class Atool extends BaseCommand {
    private static final String COMMAND_SET_PM = "pm";

    public static void main(String[] args) {
        (new Atool()).run(args);
    }

    private String[] mRawArgs;

    @Override
    public void run(String[] args) {
        mRawArgs = args;
        run(args);
    }

    @Override
    public void onShowUsage(PrintStream out) {
        out.println(
                "usage: atool <command> [subcommand] [options]\n" +
                "usage: atool pm [subcommand] [options]\n");
    }

    private String[] getRawArgs() {
        return mRawArgs;
    }

    @Override
    public void onRun() throws Exception {
        String[] rawArgs = getRawArgs();
        String[] args = Arrays.copyOfRange(rawArgs, 1, rawArgs.length);
        String command = nextArgRequired();
        switch (command) {
            case COMMAND_SET_PM:
                Pm.subMain(args, "atool pm");
                break;
            default:
                throw new IllegalArgumentException("unknown command '" + command + "'");
        }
    }
}
