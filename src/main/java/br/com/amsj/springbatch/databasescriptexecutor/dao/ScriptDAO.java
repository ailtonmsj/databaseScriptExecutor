package br.com.amsj.springbatch.databasescriptexecutor.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.stereotype.Repository;

@Repository
public class ScriptDAO {
	
	public void executeScript(final File scriptFile, DataSource dataSource) {
		
		Connection connection;
		try (Reader reader = new BufferedReader(new FileReader(scriptFile))){
			
			connection = dataSource.getConnection();
			
			ScriptRunner scriptRunner = new ScriptRunner(connection);
			scriptRunner.setAutoCommit(true);
			// STOP ON ERROR
			scriptRunner.setStopOnError(true);
			
			scriptRunner.setDelimiter("GO");
			
			scriptRunner.runScript(reader);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
