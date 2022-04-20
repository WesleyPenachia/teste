package com.natymorgs.backEndSpring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.natymorgs.backEndSpring.domain.Categoria;



@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer>{	

}
