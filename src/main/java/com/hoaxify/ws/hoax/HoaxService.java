package com.hoaxify.ws.hoax;

import com.hoaxify.ws.file.FileAttachment;
import com.hoaxify.ws.file.FileAttachmentRepository;
import com.hoaxify.ws.file.FileService;
import com.hoaxify.ws.hoax.vm.HoaxSubmitVM;
import com.hoaxify.ws.user.User;
import com.hoaxify.ws.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class HoaxService {

    private final HoaxRepository hoaxRepository;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final FileService fileService;

    UserService userService;

    public HoaxService(HoaxRepository hoaxRepository, FileAttachmentRepository fileAttachmentRepository, FileService fileService) {
        this.hoaxRepository = hoaxRepository;
        this.fileAttachmentRepository = fileAttachmentRepository;
        this.fileService = fileService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void save(HoaxSubmitVM hoaxSubmitVM, User user) {
        Hoax hoax = new Hoax();
        hoax.setContent(hoaxSubmitVM.getContent());
        hoax.setUser(user);
        hoax.setTimestamp(new Date());
        hoaxRepository.save(hoax);

        Optional<FileAttachment> optionalFileAttachment = fileAttachmentRepository.findById(hoaxSubmitVM.getAttachmentId());
        optionalFileAttachment.ifPresent(attachment -> {
            FileAttachment fileAttachment = attachment;
            fileAttachment.setHoax(hoax);
            fileAttachmentRepository.save(fileAttachment);
        });
    }

    public Page<Hoax> getHoaxes(Pageable page) {
        return hoaxRepository.findAll(page);
    }

    public Page<Hoax> getHoaxesByUser(String username, Pageable page) {
        User foundUser = userService.getByUsername(username);
        return hoaxRepository.findByUser(foundUser, page);
    }

    public Page<Hoax> getOldHoaxes(long id, String username, Pageable page) {
        Specification<Hoax> specification = idLessThan(id);
        if (username != null) {
            specification = specification.and(userIs(userService.getByUsername(username)));
        }
        return hoaxRepository.findAll(specification, page);
    }


    public long getNewHoaxesCount(long id, String username) {
        Specification<Hoax> specification = idGreaterThan(id);
        if (username != null) specification = specification.and(userIs(userService.getByUsername(username)));
        return hoaxRepository.count(specification);
    }


    public List<Hoax> getNewHoaxes(long id, String username, Sort sort) {
        Specification<Hoax> specification = idGreaterThan(id);
        if (username != null) specification = specification.and(userIs(userService.getByUsername(username)));

        return hoaxRepository.findAll(specification, sort);
    }

    Specification<Hoax> idLessThan(long id) {
        return (Specification<Hoax>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("id"), id);
    }

    Specification<Hoax> idGreaterThan(long id) {
        return (Specification<Hoax>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("id"), id);
    }

    Specification<Hoax> userIs(User user) {
        return (Specification<Hoax>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
    }


    public void delete(long id) {
        if (hoaxRepository.getOne(id).getFileAttachment() != null) {
            fileService.deleteAttachmentImage(hoaxRepository.getOne(id).getFileAttachment().getName());
        }
        hoaxRepository.deleteById(id);
    }

    public void deleteHoaxesByUser(String username) {
        hoaxRepository.deleteAll(hoaxRepository.findAll(userIs(userService.getByUsername(username))));
    }
}
