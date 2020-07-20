package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaoshu.dao.CategoryMapper;
import com.xiaoshu.entity.Category;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryMapper categoryMapper;

	public List<Category> findCategory() {
		List<Category> list = categoryMapper.selectAll();
		return list;
	}
	
	

}
