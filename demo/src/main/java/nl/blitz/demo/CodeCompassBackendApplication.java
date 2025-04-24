package nl.blitz.demo;

import java.awt.Desktop;
import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class CodeCompassBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeCompassBackendApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void openBrowser() {
		try {
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				if (desktop.isSupported(Desktop.Action.BROWSE)) {
					desktop.browse(new URI("http://localhost:8080"));
				}
			}
		} catch (Exception e) {
			System.err.println("Could not open browser: " + e.getMessage());
		}
	}

}
