package com.asolutions.scmsshd.runner;

import com.asolutions.scmsshd.util.SpringUtil;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.*;

/**
 * @author Oleg Ilyenko
 */
public class ScumdConfigurableRunner {

    public static final String GIT_REPOS_PLACEHOLDER_PATTERN = "\\$\\{gitRepos\\}";

    public static final String DEFAULT_SCUMD_FOLDER = ".scumd";
    public static final String DEFAULT_SCUMD_CONFIG = "scumd-config.xml";
    public static final String DEFAULT_GIT_REPOS_DIR = "git-repos";

    public static final String SCUMD_DEFAULTs_CONFIG_FILE = "/com/asolutions/scmsshd/scumd-default-config.xml";

    public static void main(String[] args) throws Exception {
        if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
            printUsage();
            System.exit(0);
        }

        String configLocation = getConfigLocation(args);

        if (configLocation == null) {
            configLocation = getDefaultConfigLocation();
        }

        // Just create context. All beans have it's own lifecycle including SSHD server
        new FileSystemXmlApplicationContext(SpringUtil.fixConfigLocation(configLocation));
    }

    private static String getDefaultConfigLocation() throws IOException {
        File defaultDir = getDeraultDir();
        if (!defaultDir.exists()) {
            createDefaultConfiguration();
        }

        File defaultConfig = new File(defaultDir, DEFAULT_SCUMD_CONFIG);
        if (!defaultConfig.exists()) {
            File gitRepos = createDefaultGitReposDir(defaultDir);
            createDefaultConfigFile(defaultConfig, gitRepos);
        }

        return defaultConfig.getAbsolutePath();
    }

    private static void createDefaultConfiguration() throws IOException {
        File defaultDir = getDeraultDir();
        defaultDir.mkdir();
        showMessage("Creating SCuMD default home directory at " + defaultDir.getAbsolutePath());


        File gitRepos = createDefaultGitReposDir(defaultDir);

        File defaultConfig = new File(defaultDir, DEFAULT_SCUMD_CONFIG);
        createDefaultConfigFile(defaultConfig, gitRepos);
    }

    private static File createDefaultGitReposDir(File parent) {
        File gitRepos = new File(parent, DEFAULT_GIT_REPOS_DIR);

        if (!gitRepos.exists()) {
            gitRepos.mkdir();
            showMessage("Creating git repositories base directory at " + gitRepos.getAbsolutePath());
        }

        return gitRepos;
    }

    private static void createDefaultConfigFile(File defaultConfig, File gitRepos) throws IOException {
        BufferedReader defaults = new BufferedReader(new InputStreamReader(ScumdConfigurableRunner.class.getResourceAsStream(SCUMD_DEFAULTs_CONFIG_FILE)));
        PrintWriter target = new PrintWriter(defaultConfig);

        try {
            String line;
            while ((line = defaults.readLine()) != null) {
                line = line.replaceAll(GIT_REPOS_PLACEHOLDER_PATTERN, gitRepos.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\"));
                target.println(line);
            }
        } finally {
            defaults.close();

            target.flush();
            target.close();
        }

        showMessage("No default configuration was found!");
        showMessage("Default configuration was created at '" + defaultConfig.getAbsolutePath() + "'. Please review configuration and start SCuMD again.");
        System.exit(1);
    }

    private static void showMessage(String message) {
        System.out.println(message);
    }

    private static File getDeraultDir() {
        return new File(System.getProperty("user.home"), DEFAULT_SCUMD_FOLDER);
    }

    private static String getConfigLocation(String[] args) {
        if (args.length == 0) {
            return null;
        } else if (args.length == 1) {
            return args[0];
        } else {
            System.err.println("Wrong number or arguments!\n");
            printUsage();
            System.exit(1);
        }

        return null;
    }

    private static void printUsage() {
        System.out.println("Usage:\n");
        System.out.println("\tjava -jar scumd-VERSION.jar [/path/to/config.xml]\n");
        System.out.println("\tIf you plain to use database, you should also add driver to the classpath:\n");
        System.out.println("\tjava -cp /path/to/my-database-driver.jar;scumd-VERSION.jar com.asolutions.scmsshd.runner.ScumdConfigurableRunner [/path/to/config.xml]");
    }
}
