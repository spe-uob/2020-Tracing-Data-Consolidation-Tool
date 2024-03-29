package Group1.com.DataConsolidation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.logging.Logger;

@SpringBootApplication(scanBasePackages = "Group1.com.DataConsolidation")
public class Application { //Start our backend Application

	private static final Logger logger = Logger.getLogger(Application.class.getName());
	public static void main(String[] args) {
		ApplicationContext ctx =  SpringApplication.run(Application.class, args);
		Object y = ctx.getBean("ProgressBean");
		logger.info(y.getClass().toString());
	}


}
