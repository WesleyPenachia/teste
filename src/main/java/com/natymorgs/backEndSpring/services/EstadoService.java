package com.natymorgs.backEndSpring.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.natymorgs.backEndSpring.domain.Estado;
import com.natymorgs.backEndSpring.repositories.EstadoRepository;

@Service
public class EstadoService {
	
	@Autowired
	private EstadoRepository repo;
	
	public List<Estado> findAll(){
		
		return repo.findAllByOrderByNome();
	}

}
