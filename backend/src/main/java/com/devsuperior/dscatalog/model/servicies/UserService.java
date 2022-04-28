package com.devsuperior.dscatalog.model.servicies;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.RoleDTO;
import com.devsuperior.dscatalog.dto.UserDTO;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.dto.UserUpdateDTO;
import com.devsuperior.dscatalog.model.entities.Role;
import com.devsuperior.dscatalog.model.entities.User;
import com.devsuperior.dscatalog.model.repositories.RoleRepository;
import com.devsuperior.dscatalog.model.repositories.UserRepository;
import com.devsuperior.dscatalog.model.servicies.exception.DatabaseException;
import com.devsuperior.dscatalog.model.servicies.exception.ResourceNotFoundException;

@Service
public class UserService implements UserDetailsService {
	
	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private BCryptPasswordEncoder cryptEncoder;

	@Autowired
	private RoleRepository roleRepository;

	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable page) {

		Page<User> users = repository.findAll(page);

		return users.map(x -> new UserDTO(x));
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {

		User user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));

		return new UserDTO(user);
	}

	@Transactional
	public UserDTO save(UserInsertDTO dto) {

		User user = new User();
		copyDtoToEntity(dto, user);
		user.setPassword(cryptEncoder.encode(dto.getPassword()));

		user = repository.save(user);

		return new UserDTO(user);
	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO dto) {

		try {
			User user = repository.getOne(id);
			copyDtoToEntity(dto, user);

			user = repository.save(user);
			return new UserDTO(user);
		}
		catch(EntityNotFoundException e){
			throw new ResourceNotFoundException("Id not found "+ id);
		}
	}
	
	public void deleteById(Long id) {

		try {
			repository.deleteById(id);
			
		}
		catch(EmptyResultDataAccessException e){
			throw new ResourceNotFoundException("Id not found "+ id);
		}
		catch(DataIntegrityViolationException e){
			throw new DatabaseException("Integrity Violation");
		}
	}

	private void copyDtoToEntity(UserDTO dto, User user) {
		user.setFristName(dto.getFristName());
		user.setLastName(dto.getLastName());
		user.setEmail(dto.getEmail());
		
		user.getRoles().clear();
		
		for(RoleDTO roleDTO : dto.getRoles()) {
			Role role = roleRepository.getOne(roleDTO.getId());
			
			user.getRoles().add(role);
		}

	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = repository.findByEmail(username);
		
		if (user == null){
			logger.error("User not found " + username);
			
			throw new UsernameNotFoundException("Email not found");
		}
		
		logger.info("User found " + username);
		
		return user;
	}

}