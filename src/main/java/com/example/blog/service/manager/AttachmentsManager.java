package com.example.blog.service.manager;

import com.example.blog.domain.Attachments;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface AttachmentsManager {

    void save(MultipartFile[] files, int id_post);

    void saveAll();

    List<Attachments> getAllAttachments();

    void remove(int id);

    void removeByPostId(int id);

    String getAttachmentFilename(int id);

    Map<Integer, Integer> getAttachmentsWithAmount();

}
