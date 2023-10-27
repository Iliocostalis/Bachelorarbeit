package org.example;

import java.util.Optional;
import java.util.Set;

public class StartConfiguration
{
    public int port = 26134;
    public int width = 720;
    public int height = 480;
    public String startEnvironment = "fin";

    public StartConfiguration(){}
    public void parseArgs(String[] args)
    {
        for (int i = 0; i < args.length; i++) {
            System.out.println("Argument " + i + ": " + args[i]);
        }

        for (int i = 0; i < args.length-1; i++)
        {
            String current = args[i];
            String next = args[i+1];

            if(!isValidArgName(current))
                continue;

            if(isValidArgName(current) && isValidArgName(next))
                continue;

            switch (current)
            {
                case "-port":
                    port = stringToInt(next, port);
                    break;
                case "-width":
                    width = stringToInt(next, width);
                    break;
                case "-height":
                    height = stringToInt(next, height);
                    break;
            }
        }
    }

    private static final Set<String> validArgs = Set.of(
            "-port", "-width", "-height", "-env"
    );

    private boolean isValidArgName(String arg)
    {
        return validArgs.contains(arg);
    }

    private int stringToInt(String string, int defaultValue)
    {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
