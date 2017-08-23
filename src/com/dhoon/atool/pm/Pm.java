package com.dhoon.atool.pm;

import static android.content.pm.PackageManager.INSTALL_FAILED_INVALID_URI;
import static android.content.pm.PackageManager.INSTALL_FAILED_USER_RESTRICTED;
import static android.content.pm.PackageManager.INSTALL_FULL_APP;
import static android.content.pm.PackageManager.INSTALL_REASON_USER;
import static android.content.pm.PackageManager.INSTALL_SUCCEEDED;

import android.content.pm.IPackageManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;

import com.android.internal.os.BaseCommand;

import java.io.PrintStream;

public final class Pm extends BaseCommand {
    private static final String COMMAND_SET_INSTALL_EXISTING_PACKAGE =
        "install-existing-package";

    public static void main(String[] args) {
        subMain(args, "pm");
    }

    public static void subMain(String[] args, String command) {
        (new Pm(command)).run(args);
    }

    private String mCommand;

    public Pm() {
        this("pm");
    }

    public Pm(String command) {
        mCommand = command;
    }

    private IPackageManager mIpm;
    private int mUserId = UserHandle.USER_SYSTEM;

    @Override
    public void onShowUsage(PrintStream out) {
        out.println(
                "usage: " + mCommand + " [subcommand] [options]\n" +
                "usage: " + mCommand + " install-existing-package [ --user <USER_ID> ] <PACKAGES>\n");
    }

    @Override
    public void onRun() throws Exception {
        mIpm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));

        if (mIpm == null) {
            System.err.println("Error: Could not access the Package Manager.  Is the system running?");
            return;
        }

        String command = nextArgRequired();
        switch (command) {
            case COMMAND_SET_INSTALL_EXISTING_PACKAGE:
                runInstallExistingPackage();
                break;
            default:
                throw new IllegalArgumentException("unknown command '" + command + "'");
        }
    }

    private boolean parseUserArg(String opt) {
        if ("--user".equals(opt)) {
            mUserId = UserHandle.parseUserArg(nextArgRequired());
            return true;
        }
        return false;
    }

    private void runInstallExistingPackage() throws RemoteException {
        String opt;
        while ((opt = nextOption()) != null) {
            if (parseUserArg(opt))
                continue;
            throw new IllegalArgumentException("Unknown option: " + opt);
        }

        String packageName = nextArgRequired();
        do {
            // doesn't support installFlags and installReason yet
            switch (mIpm.installExistingPackageAsUser(
                        packageName, mUserId, INSTALL_FULL_APP, INSTALL_REASON_USER)) {
                case INSTALL_SUCCEEDED:
                    System.out.println("Success: " + packageName);
                    break;
                case INSTALL_FAILED_INVALID_URI:
                    System.err.println("Error: Package " + packageName + " doesn't exist.");
                    break;
                case INSTALL_FAILED_USER_RESTRICTED:
                    System.err.println("Error: Package installation is disallowed.");
                    // Stop, because next attempts will be failed.
                    return;
                default:
                    System.err.println("Error: Couldn't install Package " + packageName + " for unknown error.");
            }
        } while ((packageName = nextArg()) != null);
    }
}
