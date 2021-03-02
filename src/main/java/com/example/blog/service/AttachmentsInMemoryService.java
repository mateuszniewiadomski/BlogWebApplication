package com.example.blog.service;

import com.example.blog.domain.Attachments;
import com.example.blog.domain.Posts;
import com.example.blog.repositories.AttachmentsRepository;
import com.example.blog.repositories.PostsRepository;
import com.example.blog.service.manager.AttachmentsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@DependsOn("postsInMemoryService")
public class AttachmentsInMemoryService implements AttachmentsManager {

    private final List<Attachments> attachmentsList;
    private final AttachmentsRepository attachmentsRepository;
    private final PostsRepository postsRepository;

    @Autowired
    public AttachmentsInMemoryService(List<Attachments> attachmentsList, AttachmentsRepository attachmentsRepository, PostsRepository postsRepository) {
        this.attachmentsList = attachmentsList;
        this.attachmentsRepository = attachmentsRepository;
        this.postsRepository = postsRepository;
    }

    @Override
    public void save(MultipartFile[] files, int id_post) {
        for (MultipartFile file : files) {
            if (file.getOriginalFilename() != null && !file.getOriginalFilename().equals("")) {
                Attachments attachment = new Attachments();
                attachment.setId_post(id_post);
                attachment.setFilename(file.getOriginalFilename());
                attachment.setPosts(postsRepository.findById(attachment.getId_post()).get());
                attachmentsRepository.save(attachment);
            }
        }
    }

    @Override
    public void saveAll() { attachmentsRepository.saveAll(attachmentsList); }

    @Override
    public List<Attachments> getAllAttachments() { return attachmentsRepository.findAll(); }

    @Override
    public void remove(int id) { attachmentsRepository.deleteById(id); }

    @Override
    public void removeByPostId(int id) { attachmentsRepository.deleteAllByPostsId(id); }

    @Override
    public String getAttachmentFilename(int id) { return attachmentsRepository.findById(id).get().getFilename(); }

    @Override
    public Map<Integer, Integer> getAttachmentsWithAmount() {
        List<Integer> integerList = new ArrayList<Integer>();
        for (Attachments attachments : attachmentsList) {
            integerList.add(attachments.getId_post());
        }
        for (Posts post : postsRepository.findAll()) {
            integerList.add(post.getId());
        }
        Map<Integer, Integer> countMap = new HashMap<>();

        for (Integer item : integerList) {

            if (countMap.containsKey(item))
                countMap.put(item, countMap.get(item) + 1);
            else
                countMap.put(item, 1);
        }
        return countMap;
    }

    public void savePosts() {
        for (Attachments a : attachmentsList) {
            a.setPosts(postsRepository.findById(a.getId_post()).get());
        }
    }

    @PostConstruct
    public void init() {
        savePosts();
        saveAll();
    }
}
