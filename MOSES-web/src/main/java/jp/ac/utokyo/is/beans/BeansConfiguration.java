package jp.ac.utokyo.is.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfiguration {

	@Bean
	public SimulationLogic getSimulation() {
		return new SimulationLogic();
	}
}
