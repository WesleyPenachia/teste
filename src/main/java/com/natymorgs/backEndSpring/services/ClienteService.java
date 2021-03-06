package com.natymorgs.backEndSpring.services;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.natymorgs.backEndSpring.domain.Cidade;
import com.natymorgs.backEndSpring.domain.Cliente;
import com.natymorgs.backEndSpring.domain.Endereco;
import com.natymorgs.backEndSpring.domain.enums.Perfil;
import com.natymorgs.backEndSpring.domain.enums.TipoCliente;
import com.natymorgs.backEndSpring.dto.ClienteDTO;
import com.natymorgs.backEndSpring.dto.ClienteNewDTO;
import com.natymorgs.backEndSpring.repositories.ClienteRepository;
import com.natymorgs.backEndSpring.repositories.EnderecoRepository;
import com.natymorgs.backEndSpring.security.UserSS;
import com.natymorgs.backEndSpring.services.exceptions.AuthorizationException;
import com.natymorgs.backEndSpring.services.exceptions.DataIntegretyException;
import com.natymorgs.backEndSpring.services.exceptions.ObjecNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repo;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private S3Service s3Service;
	
	@Autowired
	private imageService imageservice;
	
	@Value("${img.prefix.client.profile}")
	private String prefix;
	
	@Value("${img.profile.size}")
	private Integer size;
	
	@Autowired
	private BCryptPasswordEncoder pe;
	
	public Cliente find(Integer id) {
		
		UserSS user = UserService.authenticaded();
		if(user==null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())){
			throw new AuthorizationException("Acesso negado");
		}
		
		Optional<Cliente> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjecNotFoundException(
				"Objeto n??o encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}
	
	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repo.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}
	
	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj, obj);
		return repo.save(newObj);
	}

	public void delete(Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DataIntegretyException("N??o ?? poss??vel excluir porque h?? entidades relacionadas");
		}
	}
	
	public List<Cliente> findAll() {
		return repo.findAll();
	}
	
	
	public Cliente findByEmail(String email) {
		UserSS user = UserService.authenticaded();
		if(user==null || !user.hasRole(Perfil.ADMIN) && !email.equals(user.getUsername())) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Cliente obj = repo.findByEmail(email);
		if(obj == null) {
			throw new ObjecNotFoundException("Objeto n??o encontrado! id: " + user.getId()
					+ ", Tipo: " + Cliente.class.getName());
		}
		return obj;
		
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null, null);
	}
	
	public Cliente fromDTO(ClienteNewDTO objDto) {
		Cliente cli = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfouCnpj(), TipoCliente.toEnum(objDto.getTipo()),pe.encode(objDto.getSenha()));
		Cidade cid = new Cidade(objDto.getCidadeId(), null, null);
		Endereco end = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplemento(), objDto.getBairro(), objDto.getCep(), cli, cid);
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDto.getTelefone1());
		if (objDto.getTelefone2()!=null) {
			cli.getTelefones().add(objDto.getTelefone2());
		}
		if (objDto.getTelefone3()!=null) {
			cli.getTelefones().add(objDto.getTelefone3());
		}
		return cli;
	}
	
	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}
	
	public URI uploadProfilePicture(MultipartFile multipartFile) {
		UserSS user = UserService.authenticaded();
		
		if(user == null) {
			throw new AuthorizationException("Acesso Negado!");
		}
		
		BufferedImage  jpgImage = imageservice.getJpgImageFromFile(multipartFile);
		
		jpgImage = imageservice.cropSquare(jpgImage);
		
		jpgImage = imageservice.resize(jpgImage,size);
		
		String fileName = prefix + user.getId() + ".jpg";
		
		return s3Service.uploadFile(imageservice.getInputStream(jpgImage, "jpg"), fileName, "image");
	}
}
