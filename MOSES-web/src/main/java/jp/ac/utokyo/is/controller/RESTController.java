package jp.ac.utokyo.is.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ac.utokyo.is.beans.SimulationLogic;
import jp.ac.utokyo.is.model.InputData;

@Controller
@EnableAutoConfiguration
@ComponentScan 
public class RESTController {
	@Autowired 
	private SimulationLogic simulation;
	
	@RequestMapping(value="/simulation", consumes=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, String> simulation(@RequestBody InputData data){
		return simulation.run(data);
	}

}
