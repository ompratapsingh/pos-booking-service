package com.pos.booking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pos.booking.domain.Category;
import com.pos.booking.repository.MenuItemsRepository;

/**
 * 
 * @author Ompratap
 *
 */
@Service
public class MenuService {

	@Autowired
	private MenuItemsRepository menuItemsRepository;

	public List<Category> getCategory() {
		return menuItemsRepository.fetchCategories();
	}
}
