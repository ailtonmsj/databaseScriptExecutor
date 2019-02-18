package br.com.amsj.springbatch.databasescriptexecutor.batch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import br.com.amsj.springbatch.databasescriptexecutor.dao.ScriptDAO;
	
@Configuration
@EnableBatchProcessing
public class ScriptBatch {
	
	@Autowired
	private ScriptDAO scriptDAO;
	
    @Value(value="${files.directory.root}/${files.directory.test-scripts-h2}")
    public String TEST_SCRIPTS_H2;
    
    public static int contador = 0;
    
    @Value(value="${files.directory.root}/${files.directory.temp-files}")
    public String tempPath;
    

	@Bean
	public void executeAll() {

		if (!this.execute(TEST_SCRIPTS_H2, this.dsH2())) {
			throw new RuntimeException("Erro na execução do TEST_SCRIPTS_H2");
		}
	}
	
	// ADD GO ON ALL SCRIPTS ON FOLDER
	private File addGO(File script) {
		
		File tempFile = null;
		
		try {
			File tempfilePath = new File(tempPath);
			
			tempFile = File.createTempFile(script.getName(), "TESTE", tempfilePath);
			
			Files.copy(script.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
			try {
				BufferedWriter out = new BufferedWriter( new FileWriter(tempFile, true)); 
		        out.write("GO"); 
		        out.close(); 
			}catch (IOException e) {
			    throw new RuntimeException("===> ERRO AO INCLUIR GO NO ARQUIVO", e);
			}
			
		} catch (IOException e) {
			throw new RuntimeException("===> ERRO AO COPIAR O ARQUIVO", e);
		}
		return tempFile;
	}
    
    
	public Boolean execute(String scriptLocation, DataSource dataSource) {

		boolean isSucess = true;

		List<File> scripts = listScripts(scriptLocation);
		List<String> successScriptNames = new ArrayList<>();
		String errorScriptName = null;

		File script = null;
		try {

			for (int cont = 0; scripts.size() > cont; cont++) {
				script = scripts.get(cont);
				//scriptDAO.executeScript(addGO(script), dataSource);
				scriptDAO.executeScript(script, dataSource);
				successScriptNames.add(script.getName());
			}
		} catch (Exception e) {
			isSucess = false;
			errorScriptName = script.getName();
			e.printStackTrace();
			System.out.println("===> OCORREU UM ERRO DURANTE O PROCESSAMENTO DO SCRIPT:");
			System.out.println(script.getName());
		}
		
		this.printResult(scriptLocation, successScriptNames, errorScriptName);
		
		return isSucess;
	}
	
	private void printResult(String scriptLocation, List<String> successScriptNames, String errorScriptName) {
		
		
		System.out.println("\n*************** RESULTADOS ***************\n");
		
		System.out.println("\nScript Location: " + scriptLocation + "\n");
		
		System.out.println("SCRIPTS COM SUCESSO:");
		for(String successScriptName : successScriptNames) {
			System.out.println("--> " + successScriptName);
		}
		System.out.println("\nSCRIPTS COM ERRO:");
		System.out.println(errorScriptName != null ? "--> " + errorScriptName : "--> nenhum");
		
		System.out.println("\nArquivos com sucesso: " + successScriptNames.size());
		System.out.println(errorScriptName != null ? "Arquivos com erro: 1" : "Arquivos com erro: 0");
		
		System.out.println("\n*************** RESULTADOS ***************\n");
			
	}

	// Get All the files to be executed
	@Bean
	public List<File> listScripts(String path) {

		File rootDirectory = new File(path);

		List<File> fileReturn = getFile(rootDirectory);

		if (fileReturn != null) {
			System.out.println("\n===> files added: " + contador);
			System.out.println("===> files size : " + fileReturn.size() + "\n");
		} else {
			System.out.println("\n===> Files not found\n");
		}

		return fileReturn;
	}
	    
	// Recursive method to get the files
	private List<File> getFile(File rootDirectory) {

		List<File> fileReturn = new ArrayList<>();

		File[] files = rootDirectory.listFiles();

		for (File file : files) {
			if (file.isDirectory() && !file.isHidden()) {
				fileReturn.addAll(getFile(file));
			} else if (file.isFile() && file.getName().endsWith(".sql")) {
				fileReturn.add(file);
				contador++;
			}
		}
		return fileReturn;
	}
	
    @Bean
    @Primary
    @Qualifier("h2")
    @ConfigurationProperties("datasource.h2")
    public DataSource dsH2() {
    	return DataSourceBuilder.create().build();
    }
    
}