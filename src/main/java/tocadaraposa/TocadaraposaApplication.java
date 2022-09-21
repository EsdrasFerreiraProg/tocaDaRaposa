package tocadaraposa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tocadaraposa.config.database.*;

@SpringBootApplication
public class TocadaraposaApplication {

	public static void main(String[] args) {
		GeneralConfigs.initDatabaseConfiguration();
		SpringApplication.run(TocadaraposaApplication.class, args);
	}
}
