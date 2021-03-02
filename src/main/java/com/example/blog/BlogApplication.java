package com.example.blog;

import com.example.blog.storage.StorageProperties;
import com.example.blog.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import java.io.*;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@ImportResource("classpath:beans.xml")
public class BlogApplication {

	public static void main(String[] args) {
		//createXMLFromCSV();   // uncomment if you want to inject new CSV
		// files must be in blog/files-database/

		SpringApplication.run(BlogApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}


	//creates XML Beans from CSV
	private static void createXMLFromCSV() {
		try {
			PrintWriter pw = new PrintWriter("src/main/resources/beans.xml");
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			pw.println("<beans xmlns=\"http://www.springframework.org/schema/beans\"\n" +
					"       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
					"       xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\">\n");
			writeToXML(pw, new BufferedReader(new FileReader("files-database/Authors.csv")), "Authors");
			writeToXML(pw, new BufferedReader(new FileReader("files-database/Posts_Authors.csv")), "PostsAuthorsId");
			writeToXML(pw, new BufferedReader(new FileReader("files-database/Posts.csv")), "Posts");
			writeToXML(pw, new BufferedReader(new FileReader("files-database/Comments.csv")), "Comments");
			writeToXML(pw, new BufferedReader(new FileReader("files-database/Attachments.csv")), "Attachments");
			pw.println("</beans>");
			pw.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//writes each CSV file
	private static void writeToXML(PrintWriter pw, BufferedReader br, String fileName) throws IOException {
		String line = "";
		int x = 0;
		if ((line = br.readLine()) != null) {
			String[] head = line.split(",");
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
				pw.println("\t<bean id=\""+fileName+(++x)+"\" class=\"com.example.blog.domain."+fileName+"\" >");
				for (int i = 0; i < head.length; i++) {
					pw.println("\t\t<property name=\""+head[i]+"\" value=\""+values[i].replaceAll("\"", "")+"\" />");
				}
				pw.println("\t</bean>\n");
			}
		}
	}
}

