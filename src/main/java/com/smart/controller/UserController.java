package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private ContactRepository repo2;
	
	
	//passing common data to page
	
	
	@ModelAttribute
	public void addcommondata(Model model,Principal principal)
	{
		String username=principal.getName();
		User user=repo.getUserByUserName(username);
		System.out.println(username);
		
		//send data to dashboard using model
		model.addAttribute("user",user);
	}
	
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) 
	{
		
		return "normal/user_dashboard";
	}
	
	
	//handler for add contct
	@GetMapping("/addcontact")
	public String addcontactForm(Model model)
	{
		model.addAttribute("title","Add contact");
		model.addAttribute("contact",new Contact());
		
		return "normal/add_contact_form";
	}
	
	
	//process contact
	
	@PostMapping("/process_contact")
	public String processContact(
			@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file,
			Principal principal,HttpSession session)
	   {
		
		
		try {
			
					String name=principal.getName();
					User user=this.repo.getUserByUserName(name);
					
					//processing and uploading file
					
					if(file.isEmpty())
					{
						//if the file is empty then try try ur msg
						System.out.println("image is empty");
						contact.setImage("default.png");
					}
					
					else
					{
						//apply file to folder and update the name
						contact.setImage(file.getOriginalFilename());
						
						File saveFile=new ClassPathResource("static/img").getFile();
						
						Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
						
						Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
						
						System.out.println("image uploaded");
					}
					
					
							user.getContacts().add(contact);
							
							contact.setUser(user);
					
					
					
							this.repo.save(user);         //save wala kaam
							
							System.out.println(contact);
							
							System.out.println("user contact added");
					
					//mesage of success
					
					//session.setAttribute("msg", new Message("Your contact is added!! Add more...", "alert-sucess") );
					session.setAttribute("msg",new Message("Your contact is added!! Add more..." ,"alert-success"));
			
			
		}catch (Exception e){
			
			System.out.println("ERROR" +e.getMessage());
			e.printStackTrace();
			
			// error message
			
			//session.setAttribute("msg", new Message("something is wrong...!! try again..", "danger") );
			session.setAttribute("msg",new Message("something went wrong !!","alert-danger"));
		}
		
		return "normal/add_contact_form";
	}
	
	
	
	@GetMapping("/show-contacts/{page}")
	public String showcontacts(@PathVariable("page")Integer page , Model m,Principal principal)
	{
		m.addAttribute("title", "Show User Contacts");
		
		
//		String username=principal.getName();
//		User user=this.repo.getUserByUserName(username);
//		List<Contact> contact=user.getContacts();
		
		String username=principal.getName();
		User user=this.repo.getUserByUserName(username);
		
	     Pageable pageable=PageRequest.of(page, 5);
		
		//List<Contact> contacts=this.repo2.findContactByuser(user.getId());                //  before paging.........
		
		Page<Contact> contacts=this.repo2.findContactByuser(user.getId(),pageable);
		
		m.addAttribute("contacts",contacts);
		m.addAttribute("currentpage",page);
		m.addAttribute("totalPages",contacts.getTotalPages());
		
		
		return "normal/show_contacts";
	}
	
	
	//showing particular contact details
		@RequestMapping("/{cId}/contact")
		public String showContactDetail(@PathVariable("cId")Integer cId,Model m,Principal principal)
		{
			System.out.println("CID"+cId);
			
			Optional<Contact>  contactOptional = this.repo2.findById(cId);
			Contact contact =contactOptional.get();
			
			//checking security
			String username=principal.getName();
			User user=this.repo.getUserByUserName(username);
			
			if(user.getId()==contact.getUser().getId())
				m.addAttribute("contact",contact);
			
			
			
			return "normal/contact_detail";
		}
		
		
		
		//deleting contact
		@GetMapping("/delete/{cid}")
		public String deleteContact(@PathVariable("cid")Integer cId,Model m,Principal principal,HttpSession session)
		{
			
//			Optional<Contact>  contactOptional = this.repo2.findById(cId);
			Contact contact =this.repo2.findById(cId).get();
			
			
			
//			this.repo2.delete(contact);
			
			String username=principal.getName();
			User user=this.repo.getUserByUserName(username);
			
			
			if(user.getId()==contact.getUser().getId())
			{
				//contact.setUser(null);
				//this.repo2.delete(contact);
				user.getContacts().remove(contact);
				this.repo.save(user);
				System.out.println(" contactdeleted");
				session.setAttribute("messsage", new Message("Your contact is deleted!!", "alert-success"));
			}
			return"redirect:/user/show-contacts/0";			
		}
	
		@PostMapping("/update-contact/{cid}")
		public String updateform(@PathVariable("cid") Integer cid,Model m)
		{
			m.addAttribute("title","update_contact");
			
			Contact contact = this.repo2.findById(cid).get();
			m.addAttribute("contact",contact);
			return "normal/update_form";
		}
		
		
		
		@RequestMapping(value="/process_update/{id}",method = RequestMethod.POST)
		public String updatehandler(@ModelAttribute Contact contact,Model m,HttpSession session,Principal principal)
		{
			
			System.out.println("contact name"+contact.getName());
			System.out.println("contact ID"+contact.getcId());
			
			String name=principal.getName();
			User user=this.repo.getUserByUserName(name);
			 user.getContacts().add(contact);
			
			contact.setUser(user);
			this.repo2.save(contact);
			
			return "";
		}

}
