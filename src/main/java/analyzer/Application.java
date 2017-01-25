package analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Klasa odpowiadajaca za załadowanie aplikacji.
 */
@SpringBootApplication
public class Application
{
    /**
     * Funkcja rozpoczynająca działania aplikacji.
     * @param args
     */
    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }
}
