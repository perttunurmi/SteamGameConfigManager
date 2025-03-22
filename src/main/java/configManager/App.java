package configManager;

import java.io.File;
import java.io.IOException;
import userInterface.cli.*;
import userInterface.gui.*;
import utils.*;

/** Entry point for the program */
public class App {
  private static String ConfigPath = "C:\\Program Files (x86)\\Steam\\userdata";
  private static String AccountID = "";
  private static String GameID = "730"; // GameID, default 730 = csgo/cs2
  private static File[] Accounts;

  /*
   * Steam game config manager
   * Author: Perttu Nurmi
   * License: MIT
   * GitHub: https://github.com/perttunurmi
   * Email: perttu.nurmi@gmail.com
   * Program that links multiple Steam accounts to use the same config files
   */
  public static void main(final String[] args) {
    // if no arguments are given start automatically  with gui
    if (args.length == 0) {
      File config = new File(ConfigPath);
      if (config.isDirectory()) {
        setAccounts();
        predictMainAccount();
        System.out.println(Accounts);
      }
      UserInterface.gui();

    } else {

      System.out.println("cli mode not working right now");
      System.out.println("Found " + Accounts.length + " accounts.");
      System.out.println("Starting backup");

      File accountDir = new File(ConfigPath, AccountID);
      File gameDir = new File(accountDir, GameID);

      System.out.println("Press CTRL+C to exit");
      while (true) {}
    }
  }

  public static void backupAllConfigs() {
    for (final File account : Accounts) {
      try {
        BackupManager.makeNewBackup(account);
        System.out.println("Created backup for account " + account.getAbsolutePath());
      } catch (final Exception error) {
        System.out.println("Error when creating a backup " + account.getAbsolutePath());
        continue;
      }
    }
  }

  public static void linkConfigs(File gameDir, File gameConfig) {
    LinkManager.createLink(gameDir, gameConfig);

    for (final File account : Accounts) {
      if (!account.getPath().contains(AccountID)) {
        File gameConfigCopy = new File(account, GameID);
        if (gameConfigCopy.exists()) {
          if (LinkManager.isSymbolicLink(gameConfigCopy)) {
            LinkManager.removeLink(gameConfigCopy);
          } else {
            try {
              BackupManager.deleteFolderRecursively(gameConfigCopy);
            } catch (IOException error) {
              error.printStackTrace();
            }
          }
        }
      }
    }
  }

  public static String getConfigPath() {
    return ConfigPath;
  }

  public static void setConfigPath(final String configPath) {
    ConfigPath = configPath;
  }

  public static String getAccountID() {
    return AccountID;
  }

  public static void setAccountID(final String accountID) {
    AccountID = accountID;
  }

  public static String getGameID() {
    return GameID;
  }

  public static void setGameID(final String gameID) {
    GameID = gameID;
  }

  public static File[] getAccounts() {
    return Accounts;
  }

  public static void setAccounts() {
    try {
      Accounts = AccountManager.getAllAccounts(ConfigPath);
    } catch (final InvalidConfigPathException error) {
      System.out.println(error.getMessage());
      System.exit(1);
    } catch (final InvalidAccountIdException error) {
      System.out.println(error.getMessage());
      System.exit(2);
    }
  }

  public static void setAccounts(final File[] accounts) {
    Accounts = accounts;
  }

  private static void runInteractively() {
    InteractiveMode.interactiveMode();
  }

  private static void runValidators() throws InvalidAccountIdException, InvalidConfigPathException {
    InputValidator.validateAccountId(AccountID);
    InputValidator.validateAccountFolder(AccountID, ConfigPath);
    // TODO: check that all is good with gameDir
  }

  /*
   * Predicts the main account by checking which account has the most config folders
   */
  private static void predictMainAccount() {
    File mostLikelyMainAccount = Accounts[0];

    for (final File account : Accounts) {
      if (account.list().length > mostLikelyMainAccount.list().length) {
        mostLikelyMainAccount = account;
      }
    }
    setAccountID(mostLikelyMainAccount.getName());
  }
}
