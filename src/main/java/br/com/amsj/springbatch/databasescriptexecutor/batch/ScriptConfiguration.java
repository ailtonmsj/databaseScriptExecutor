package br.com.amsj.springbatch.databasescriptexecutor.batch;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

//@Configuration
//@EnableBatchProcessing
public class ScriptConfiguration {
	
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    @Bean
    public Boolean execute() {
    	
    	System.err.println("-----------> TESTE2");
    	
    	return true;
    }

}
