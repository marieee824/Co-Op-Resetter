package io.github.marinersfan824.resetter;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        File configJson = new File("config.json");
        try {
            Config config;
            if (configJson.createNewFile()) {
                // Doesn't exist, create it
                System.out.println("Creating config File");
                config = createConfig();
            } else {
                // Already exists, load it
                System.out.println("Loading config");
                config = loadConfig();
            }
            reset(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Config createConfig() throws IOException {
        Scanner sc = new Scanner(System.in);

        // Get the world
        System.out.println("What is the name of your world folder?");
        String worldName = sc.nextLine();
        World world = new World(worldName);

        // Get the server jar
        System.out.println("What is the name of your minecraft server jar/fabric server jar?");
        String jarName = sc.nextLine().replace(".jar", "");
        ServerJar serverJar = new ServerJar(jarName);

        // Get whether or not to use a seed
        System.out.println("Would you like to use a seed?");
        String useSeed = sc.nextLine();
        boolean useSeed1;
        if (useSeed.equalsIgnoreCase("yes")) {
            useSeed1 = true;
        } else {
            useSeed1 = false;
        }

        // Create the config and save it as json in a file
        Config config = new Config(world, serverJar, useSeed1);
        Gson gson = new Gson();
        BufferedWriter writer = new BufferedWriter(new FileWriter("config.json"));
        String json = gson.toJson(config);
        writer.write(json);
        writer.close();
        return config;
    }

    public static Config loadConfig() throws IOException {
        // Read the config from the json file
        Gson gson = new Gson();
        BufferedReader reader = new BufferedReader(new FileReader("config.json"));
        Config config = gson.fromJson(reader, Config.class);
        if (config.getUseSeed()) {
            System.out.println("What seed would you like to use?");
            Scanner sc = new Scanner(System.in);
            Properties properties = new Properties();
            FileInputStream fileInputStream = new FileInputStream("server.properties");
            properties.load(fileInputStream);
            fileInputStream.close();

            FileOutputStream fileOutputStream = new FileOutputStream("server.properties");
            properties.setProperty("level-seed", sc.nextLine());
            properties.store(fileOutputStream, null);
            fileOutputStream.close();
        }
        reader.close();
        return config;
    }

    public static void reset(Config config) throws IOException {
        Scanner sc = new Scanner(System.in);
        Runtime runtime = Runtime.getRuntime();
        String worldName = config.getWorld().getWorldName();
        File worldFile = new File(worldName);
        String jar = config.getServerJar().getJarName();
        try {
            runtime.exec("java -Xms4096M -Xmx4096M -jar " + jar + ".jar --nogui");
            if (worldFile.exists()) {
                FileUtils.deleteDirectory(worldFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            System.out.println("Type 'reset' to reset and 'quit' to exit");
            if (sc.nextLine().equalsIgnoreCase("reset")) {
                System.out.println("Resetting...");
                loadConfig();
            } else if (sc.nextLine().equalsIgnoreCase("quit")) {
                System.out.println("I'm sorry, I either didn't understand or you exited");
                break;
            }
        }
    }
}

class ServerJar {
    private String jarName;

    public ServerJar(String jarName) {
        this.jarName = jarName;
    }

    public String getJarName() {
        return jarName;
    }
}

class World {
    private String worldName;

    public World(String worldName) {
        this.worldName = worldName;
    }

    public String getWorldName() {
        return worldName;
    }
}
class Seed {
    private String seed;

    public Seed(String seed) {
        this.seed = seed;
    }
}

class Config {
    private ServerJar serverJar;
    private World world;
    private boolean useSeed;
    private String seed;

    public Config(World world, ServerJar serverJar, boolean useSeed) {
        this.serverJar = serverJar;
        this.world = world;
        this.useSeed = useSeed;
    }

    public ServerJar getServerJar() {
        return serverJar;
    }

    public World getWorld() {
        return world;
    }
    public String getSeed() {
        return seed;
    }
    public boolean getUseSeed() {
        return useSeed;
    }
}
