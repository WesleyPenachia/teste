package com.natymorgs.backEndSpring.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.natymorgs.backEndSpring.domain.Cidade;
import com.natymorgs.backEndSpring.repositories.CidadeRepository;

@Service
public class CidadeService {

	@Autowired
	private CidadeRepository repo;
	
	public List<Cidade> findByEstado(Integer estadoId){
		return repo.findCidade(estadoId);
	}
}
