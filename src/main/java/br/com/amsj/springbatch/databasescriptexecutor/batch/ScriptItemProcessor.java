package br.com.amsj.springbatch.databasescriptexecutor.batch;

import java.io.File;

import org.springframework.batch.item.ItemProcessor;

public class ScriptItemProcessor implements ItemProcessor<File, Boolean> {
	
	@Override
	public Boolean process(File script) throws Exception {
		
		System.err.println("----------------------> TESTE");
		
		//scriptDAO.executeScript(scriptFilePath);
		
		return true;
	}
}
