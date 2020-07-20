package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.PersonMapper;
import com.xiaoshu.entity.Person;
import com.xiaoshu.entity.PersonVo;

@Service
public class PersonService {
	@Autowired
	
	private PersonMapper personMapper;

	public PageInfo<PersonVo> findList(PersonVo personVo, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<PersonVo> list = personMapper.findList(personVo);
		PageInfo<PersonVo> page = new PageInfo<>(list);
		return page;
	}

	public void deletePerson(int parseInt) {
		personMapper.deleteByPrimaryKey(parseInt);
		
	}

	public Person findByName(String getpName) {
		Person person = new Person();
		person.setpName(getpName);
		Person per = personMapper.selectOne(person );
		return per;
	}

	public void updatePerson(Person person) {
		personMapper.updateByPrimaryKeySelective(person);
		
	}

	public void addPerson(Person person) {
		personMapper.insert(person);
		
	}

}
