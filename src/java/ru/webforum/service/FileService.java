package ru.webforum.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService 
{
	@Value("${upload.path}")
	private String uploadPath;
		
	public String uploadFile(MultipartFile file) throws IOException
	{
		File uploadDir = new File(uploadPath);
		if(!uploadDir.exists())
		{
			uploadDir.mkdir();
		}

		String uuidFile = UUID.randomUUID().toString();
		String resultFilename = uuidFile + file.getOriginalFilename();
		Files.copy(file.getInputStream(), Paths.get(uploadPath + "/" + resultFilename), StandardCopyOption.REPLACE_EXISTING);
		
		return resultFilename;
	}
	
	public void deleteFile(String filename) throws IOException
	{
		//File file = new File(uploadPath + "/" + filename);
		Files.delete(Paths.get(uploadPath + "/" + filename));
		/*
		if(file.delete())
		{
			System.out.println("FILE DELETED");
		};*/
	}
}
