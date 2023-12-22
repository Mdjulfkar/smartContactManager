package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.contactRepository;
import com.smart.dao.userRepository;
import com.smart.entities.User;
import com.smart.entities.contact;
import com.smart.helper.message;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class usercontroller {

	@Autowired
	private userRepository userRepository;

	@Autowired
	private contactRepository contactRepository;

	@ModelAttribute
	public void common(Model model, Principal principal) {
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
	}

	@RequestMapping("/index")
	public String index(Model model, Principal principal) {
		return "normal/index";
	}

	// adding contact

	@RequestMapping("/add-contact")
	public String addcontact(Model model, Principal principal) {
		model.addAttribute("title", "Add-Contact");
		return "normal/add-contact";
	}

	// process adding contact

	@RequestMapping("/process-contact")
	public String processContact(@ModelAttribute contact contact, @RequestParam("profileimage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {

			if (file.isEmpty()) {
				System.out.println("file is empty");
				contact.setImage("contact.png");
			} else {
				contact.setImage(file.getOriginalFilename());
				File savefile = new ClassPathResource("/static/images").getFile();

				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("image is uploaded ");

			}

			String username = principal.getName();
			User user = userRepository.getUserByUserName(username);

			contact.setUser(user);
			user.getContacts().add(contact);

			userRepository.save(user);

			System.out.println("contact" + contact);

			session.setAttribute("message", new message("contact is successfully added !! add more...", "success"));

		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new message("something went wrong !! please try again...", "danger"));

		}

		return "normal/add-contact";
	}

	// view all contacts

	@RequestMapping("/viewAllContact/{page}")
	public String viewAllContact(@PathVariable("page") Integer page, Model model, Principal principal) {

		model.addAttribute("title", "view All conatcts");

		String username = principal.getName();
		User user = userRepository.getUserByUserName(username);

		Pageable pageable = PageRequest.of(page, 4);

		Page<contact> contacts = contactRepository.findContactsByUser(user.getId(), pageable);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		model.addAttribute("contacts", contacts);

		return "normal/viewAllContact";
	}

	// view particular contacts details

	@RequestMapping("/{cId}/contact")
	public String viewparticularConatact(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		String usernameString = principal.getName();
		User user = userRepository.getUserByUserName(usernameString);

		Optional<contact> contactoptional = contactRepository.findById(cId);
		contact contact = contactoptional.get();

		if (contact.getUser().getId() == user.getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		} else {
			return "normal/error";
		}
		return "normal/particular";
	}

	// delete contact

	@RequestMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		Optional<contact> optionalcontact = this.contactRepository.findById(cId);
		contact contact = optionalcontact.get();

		String usrnameString = principal.getName();
		User user = userRepository.getUserByUserName(usrnameString);

		if (contact.getUser().getId() == user.getId()) {
			contact.setUser(null);
			contactRepository.deleteById(cId);
		}

		return "redirect:/users/viewAllContact/0";
	}

	// update contact

	@PostMapping("/update/{cId}")
	public String updateContact(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		Optional<contact> contactoption = contactRepository.findById(cId);
		contact contact = contactoption.get();
		model.addAttribute("contact", contact);

		String usernameString = principal.getName();
		User user = userRepository.getUserByUserName(usernameString);

		model.addAttribute("user", user);

		return "normal/updatecontact";
	}

	// process update contact

	@PostMapping("/process-update")
	public String process_contact(@ModelAttribute contact contact, Model model, Principal principal,
			@RequestParam("profileimage") MultipartFile file, HttpSession session) {

		try {

			contact oldcontactdetails = contactRepository.findById(contact.getcId()).get();

			if (!file.isEmpty()) {

				// old image deletion process

				File deletefile = new ClassPathResource("static/images").getFile();
				File file2 = new File(deletefile, oldcontactdetails.getImage());
				file2.delete();

				// new image replacing process

				contact.setImage(file.getOriginalFilename());

				File savefile = new ClassPathResource("/static/images").getFile();
				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			} else {
				contact.setImage(oldcontactdetails.getImage());
			}

			String username = principal.getName();
			User user = userRepository.getUserByUserName(username);
			contact.setUser(user);

			this.contactRepository.save(contact);

			session.setAttribute("message", new message("you have successfully updated...", "success"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/users/" + contact.getcId() + "/contact";
	}
	
	
	//profile handler
	
	@GetMapping("/profile")
	public String profile(Model model) {
		 model.addAttribute("title","profile page");
		return "normal/profile";
	}

}
