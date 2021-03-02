package com.example.blog.storage;

import com.example.blog.domain.*;
import com.example.blog.service.manager.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

	private final Path rootLocation;

	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Autowired
	AuthorsManager am;

	@Autowired
	CommentsManager cm;

	@Autowired
	AttachmentsManager atm;

	@Autowired
	PostsManager pm;

	@Autowired
	PostsAuthorsManager pam;

	@Override
	public void store(MultipartFile[] files) {

		try {
			for (MultipartFile file: files) {
				if (!(file.isEmpty())) {
					Path destinationFile = this.rootLocation.resolve(
							Paths.get(file.getOriginalFilename()))
							.normalize().toAbsolutePath();
					if (destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
						try (InputStream inputStream = file.getInputStream()) {
							Files.copy(inputStream, destinationFile,
									StandardCopyOption.REPLACE_EXISTING);
						}
					}
				}
			}
		}
		catch (IOException ignored) {
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
				.filter(path -> !path.equals(this.rootLocation))
				.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}

	@Override
	public void generateCSV() {

		//attachments
		try {
			PrintWriter pw = new PrintWriter("upload-dir/Attachments.csv");
			pw.println("id_post,filename");
			for (Attachments attachment: atm.getAllAttachments()) {
				pw.println(attachment.getId_post() + "," + attachment.getFilename());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}

		//authors
		try {
			PrintWriter pw = new PrintWriter("upload-dir/Authors.csv");
			pw.println("id,first_name,last_name,username");
			for (Authors author: am.getAllAuthors()) {
				pw.println(author.getId() + "," +  author.getFirst_name() + "," + author.getLast_name() + "," + author.getUsername());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}

		//comments
		try {
			PrintWriter pw = new PrintWriter("upload-dir/Comments.csv");
			pw.println("id,username,id_post,comment_content");
			for (Comments comment: cm.getAllComments()) {
				pw.println(comment.getId() + "," +  comment.getUsername() + "," + comment.getId_post() + "," + comment.getComment_content());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}

		//posts
		try {
			PrintWriter pw = new PrintWriter("upload-dir/Posts.csv");
			pw.println("id,post_content,tags");
			for (Posts post: pm.getAllPosts()){
				pw.println(post.getId() + "," + post.getPost_content() + "," + post.getTags());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}

		//posts_authors
		try {
			PrintWriter pw = new PrintWriter("upload-dir/PostsAuthors.csv");
			pw.println("id_post,id_author");
			for (PostsAuthors postsAuthors: pam.getAllPostsAuthors()) {
				pw.println(postsAuthors.getId_post().getId() + "," + postsAuthors.getId_author().getId());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}

}
